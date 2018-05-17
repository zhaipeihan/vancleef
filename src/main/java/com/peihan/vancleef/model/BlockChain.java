package com.peihan.vancleef.model;


import com.peihan.vancleef.action.Pow;
import com.peihan.vancleef.exception.OperateFailedException;
import com.peihan.vancleef.exception.VerifyFailedException;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.MagicUtil;
import com.peihan.vancleef.util.StorageUtil;
import com.peihan.vancleef.util.WalletUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

import java.util.*;

/**
 * 区块链
 */
public class BlockChain {

    private static final Logger logger = LogManager.getLogger();

    private final StorageUtil storage = StorageUtil.getInstance();

    private final WalletManager walletManager = WalletManager.getInstance();

    private volatile static BlockChain INSTANCE;

    private String lastBlockHash;

    public static BlockChain getInstance() {
        if (INSTANCE == null) {
            synchronized (BlockChain.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BlockChain();
                }
            }
        }
        return INSTANCE;
    }

    public BlockChain() {
        refreshLastBlockHash();
    }

    public void transfer(String from, String to, int amount) throws ServiceException {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(makeNormalTx(from, to, amount));
        //增加一笔挖矿奖励
        transactions.add(Transaction.makeCoinbaseTx(from, ""));
        addBlock(transactions);
    }

    public class BlockChainIterator {

        private String currentBlockHash;

        public BlockChainIterator(String currentBlockHash) {
            this.currentBlockHash = currentBlockHash;
        }

        public boolean hasNext() {
            if (StringUtils.isEmpty(currentBlockHash)) {
                return false;
            }
            Block block = storage.getBlock(currentBlockHash);
            return block != null;
        }

        public Block next() {
            Block block = storage.getBlock(currentBlockHash);
            currentBlockHash = block.getPreviousHash();
            return block;
        }
    }

    /**
     * 获取区块链的迭代器
     *
     * @return
     */
    public BlockChainIterator getBlockChainIterator() {
        return new BlockChainIterator(this.lastBlockHash);
    }

    /**
     * 创建区块链
     * 先查看rocksdb里有没有对应的区块链信息，如果有，从rocksdb里创建，否则创建新的区块链
     */
    public void createBlockChain(String address) throws ServiceException {
        String lastBlockHash = storage.getLastBlockHash();
        if (StringUtils.isEmpty(lastBlockHash)) {
            initBlockChain(address);
        } else {
            refreshLastBlockHash();
        }
    }

    /**
     * 向区块链中添加一个区块，返回当前区块链中的最后索引
     */
    public void addBlock(List<Transaction> transactions) throws ServiceException {
        verifyTransactions(transactions);
        Block block = new Block();
        block.setTransactions(transactions);
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setPreviousHash(lastBlockHash);
        //需要进行执行pow算法
        Pow.pow(block);
        addBlock(block);
        //更新缓存池
        UTXOPool.getInstance().updateUXTOPool(block);
    }

    private void verifyTransactions(List<Transaction> transactions) throws OperateFailedException, VerifyFailedException {
        for (Transaction transaction : transactions) {
            if (transaction.isCoinbase()) {
                continue;
            }
            if (!verifyTransaction(transaction)) {
                throw new VerifyFailedException("Invalid transaction");
            }
        }
    }


    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        BlockChainIterator iterator = getBlockChainIterator();
        while (iterator.hasNext()) {
            blocks.add(iterator.next());
        }
        return blocks;
    }


    public long getBalance(String address) {
        Map<String, List<TxOutput>> UTXOs = UTXOPool.getInstance().getAllUTXOs(address);
        long total = 0;


        for (Map.Entry<String, List<TxOutput>> entry : UTXOs.entrySet()) {
            List<TxOutput> v = entry.getValue();
            if (CollectionUtils.isEmpty(v)) {
                continue;
            }
            for (TxOutput utxo : v) {
                if (utxo != null) {
                    total += utxo.getValue();
                }
            }
        }

        return total;
    }


    /**
     * 创建普通交易
     *
     * @param from
     * @param to
     * @param amount
     * @return
     */
    private Transaction makeNormalTx(String from, String to, int amount) throws OperateFailedException, VerifyFailedException {

        Wallet fromWallet = walletManager.getWallet(from);

        if (fromWallet == null || fromWallet.getPrivateKey() == null || fromWallet.getPublicKey() == null) {
            throw new OperateFailedException("get wallet error!");
        }
        //该地址下的所有的UXTO
        Map<String, List<TxOutput>> allUXTOs = UTXOPool.getInstance().getAllUTXOs(from);

        //获取用来创建交易的UXTO
        SpentUXTO spentUXTO = makeSpentUXTOs(allUXTOs, amount);

        if (spentUXTO == null || spentUXTO.getTotal() < amount) {
            throw new OperateFailedException(String.format("%s has no enough UXTO", from));
        }


        List<TxInput> txInputs = new ArrayList<>();
        List<TxOutput> txOutputs = new ArrayList<>();

        for (Map.Entry<String, List<TxOutput>> entry : spentUXTO.getUXTOs().entrySet()) {
            String txId = entry.getKey();
            List<TxOutput> txSpentOutputs = entry.getValue();

            for (TxOutput txSpentOutput : txSpentOutputs) {
                TxInput txInput = new TxInput(txId, txSpentOutput.getIndex(), null, fromWallet.getPublicKey());
                txInputs.add(txInput);
            }
        }

        txOutputs.add(TxOutput.makeTxOutput(amount, to));

        if (spentUXTO.getTotal() > amount) {
            txOutputs.add(TxOutput.makeTxOutput(spentUXTO.getTotal() - amount, from));
        }

        Transaction transaction = new Transaction();
        transaction.setTxInputs(txInputs);
        transaction.setTxOutputs(txOutputs);
        transaction.setTxId(HashUtil.hashHex(transaction));
        transaction.refreshTxOutputIndex();

        //用私钥对交易进行签名
        signTransaction(transaction, fromWallet.getPrivateKey());
        return transaction;
    }

    private void signTransaction(Transaction transaction, BCECPrivateKey privateKey) throws OperateFailedException {
        Map<String, Transaction> prevTxMap = getPrevTxMap(transaction);
        try {
            transaction.sign(privateKey, prevTxMap);
        } catch (Exception e) {
            throw new OperateFailedException("sign error");
        }
    }


    private boolean verifyTransaction(Transaction transaction) throws OperateFailedException {
        Map<String, Transaction> prevTxMap = getPrevTxMap(transaction);
        try {
            return transaction.verify(prevTxMap);
        } catch (Exception e) {
            throw new OperateFailedException("verify error");
        }
    }

    private Map<String, Transaction> getPrevTxMap(Transaction transaction) throws OperateFailedException {
        Map<String, Transaction> prevTxMap = new HashMap<>();
        for (TxInput txInput : transaction.getTxInputs()) {
            Transaction tx = getTransaction(txInput.getTxId());
            prevTxMap.put(txInput.getTxId(), tx);
        }
        return prevTxMap;
    }

    private Transaction getTransaction(String txId) throws OperateFailedException {
        BlockChainIterator iterator = getBlockChainIterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            for (Transaction transaction : block.getTransactions()) {
                if (Objects.equals(txId, transaction.getTxId())) {
                    return transaction;
                }
            }
        }
        throw new OperateFailedException("can not get transaction");
    }

    private SpentUXTO makeSpentUXTOs(Map<String, List<TxOutput>> UXTOs, int amount) {
        if (MapUtils.isEmpty(UXTOs)) {
            return null;
        }

        SpentUXTO spentUXTO = new SpentUXTO();
        Map<String, List<TxOutput>> spentUXTOs = new HashMap<>();

        int total = 0;

        for (Map.Entry<String, List<TxOutput>> entry : UXTOs.entrySet()) {
            String k = entry.getKey();
            List<TxOutput> txOutputs = entry.getValue();
            if (!CollectionUtils.isEmpty(txOutputs)) {
                for (TxOutput txOutput : txOutputs) {
                    if (txOutput == null) {
                        continue;
                    }
                    if (total < amount) {
                        total += txOutput.getValue();
                        if (spentUXTOs.get(k) == null) {
                            List<TxOutput> spentTxOutputs = new ArrayList<>();
                            spentTxOutputs.add(txOutput);
                            spentUXTOs.put(k, spentTxOutputs);
                        } else {
                            spentUXTOs.get(k).add(txOutput);
                        }
                    } else {
                        spentUXTO.setTotal(total);
                        spentUXTO.setUXTOs(spentUXTOs);
                        return spentUXTO;

                    }
                }
            }
        }
        spentUXTO.setTotal(total);
        spentUXTO.setUXTOs(spentUXTOs);
        return spentUXTO;
    }

    /**
     * 获取给定地址的未花费的交易输出
     * UTXO: 未花费的交易输出
     *
     * @param address
     * @return
     */
    private Map<String, List<TxOutput>> getAllUTXOs(String address) {
        Map<String, List<Integer>> allSTXOs = getAllSTXOs(address);
        Map<String, List<TxOutput>> allUTXOs = new HashMap<>();
        BlockChainIterator iterator = getBlockChainIterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block == null) {
                continue;
            }

            for (Transaction transaction : block.getTransactions()) {
                List<Integer> spentOutputIndex = allSTXOs.get(transaction.getTxId());
                boolean needCheck = !CollectionUtils.isEmpty(spentOutputIndex);

                for (TxOutput txOutput : transaction.getTxOutputs()) {
                    if ((txOutput == null)
                            || (needCheck && spentOutputIndex.contains(txOutput.getIndex()))) {
                        continue;
                    }
                    if (txOutput.isLockedWithKey(WalletUtil.getPublicKeyHash(address))) {

                        if (allUTXOs.get(transaction.getTxId()) == null) {
                            List<TxOutput> txOutputs = new ArrayList<>();
                            txOutputs.add(txOutput);
                            allUTXOs.put(transaction.getTxId(), txOutputs);
                        } else {
                            allUTXOs.get(transaction.getTxId()).add(txOutput);
                        }
                    }
                }
            }
        }
        return allUTXOs;
    }


    /**
     * 获取给定地址的所有交易中花费掉的交易输出
     * STXO : 花费的交易输出
     *
     * @param address
     * @return
     */
    private Map<String, List<Integer>> getAllSTXOs(String address) {

        Map<String, List<Integer>> allSTXO = new HashMap<>();

        BlockChainIterator iterator = getBlockChainIterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block == null) {
                continue;
            }

            for (Transaction transaction : block.getTransactions()) {
                if (transaction == null || transaction.isCoinbase()) {
                    continue;
                }

                for (TxInput txInput : transaction.getTxInputs()) {
                    if (txInput == null) {
                        continue;
                    }
                    if (txInput.usesKey(WalletUtil.getPublicKeyHash(address))) {
                        if (allSTXO.get(txInput.getTxId()) == null) {
                            List<Integer> outputIndexs = new ArrayList<>();
                            outputIndexs.add(txInput.getTxOutputIndex());
                            allSTXO.put(txInput.getTxId(), outputIndexs);
                        } else {
                            allSTXO.get(txInput.getTxId()).add(txInput.getTxOutputIndex());
                        }
                    }
                }
            }
        }
        return allSTXO;
    }


    /**
     * 获取所有的未花费的交易输出
     * UTXO: 未花费的交易输出
     *
     * @param
     * @return
     */
    private Map<String, List<TxOutput>> getAllUTXOs() {
        Map<String, List<Integer>> allSTXOs = getAllSTXOs();
        Map<String, List<TxOutput>> allUTXOs = new HashMap<>();
        BlockChainIterator iterator = getBlockChainIterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block == null) {
                continue;
            }

            for (Transaction transaction : block.getTransactions()) {
                List<Integer> spentOutputIndex = allSTXOs.get(transaction.getTxId());
                boolean needCheck = !CollectionUtils.isEmpty(spentOutputIndex);

                for (TxOutput txOutput : transaction.getTxOutputs()) {
                    if ((txOutput == null)
                            || (needCheck && spentOutputIndex.contains(txOutput.getIndex()))) {
                        continue;
                    }

                    if (allUTXOs.get(transaction.getTxId()) == null) {
                        List<TxOutput> txOutputs = new ArrayList<>();
                        txOutputs.add(txOutput);
                        allUTXOs.put(transaction.getTxId(), txOutputs);
                    } else {
                        allUTXOs.get(transaction.getTxId()).add(txOutput);
                    }
                }
            }
        }
        return allUTXOs;
    }


    /**
     * 获取所有交易中花费掉的交易输出
     * STXO : 花费的交易输出
     *
     * @param
     * @return
     */
    private Map<String, List<Integer>> getAllSTXOs() {

        Map<String, List<Integer>> allSTXO = new HashMap<>();

        BlockChainIterator iterator = getBlockChainIterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block == null) {
                continue;
            }

            for (Transaction transaction : block.getTransactions()) {
                if (transaction == null || transaction.isCoinbase()) {
                    continue;
                }

                for (TxInput txInput : transaction.getTxInputs()) {
                    if (txInput == null) {
                        continue;
                    }
                    if (allSTXO.get(txInput.getTxId()) == null) {
                        List<Integer> outputIndexs = new ArrayList<>();
                        outputIndexs.add(txInput.getTxOutputIndex());
                        allSTXO.put(txInput.getTxId(), outputIndexs);
                    } else {
                        allSTXO.get(txInput.getTxId()).add(txInput.getTxOutputIndex());
                    }
                }
            }
        }
        return allSTXO;
    }

    /**
     * 获取创世区块
     * 创世区块nonce为0
     */
    private Block makeGenesisBlock(String address) throws ServiceException {
        Block block = new Block();
        block.setPreviousHash(MagicUtil.makeEmptyHashStr());
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setTransactions(new ArrayList<>(Collections.singleton(Transaction.makeCoinbaseTx(address, "this is the coinbase"))));
        block.setNonce(0);
        block.setHash(HashUtil.hash(block));
        return block;
    }

    /**
     * 初始化区块链，并且增加一个创世区块
     */
    private void initBlockChain(String address) throws ServiceException {
        Block genesisBlock = makeGenesisBlock(address);
        addBlock(genesisBlock);
    }

    /**
     * 增加一个区块
     *
     * @param block
     * @throws ServiceException
     */
    private void addBlock(Block block) throws ServiceException {
        storage.putBlock(block);
        storage.putLastBlockHash(block.getHash());
        refreshLastBlockHash();
    }

    private void refreshLastBlockHash() {
        this.lastBlockHash = storage.getLastBlockHash();
    }


    //验证区块链是否有效
    public boolean isBlockChainValid() throws ServiceException {
        List<Block> blocks = getAllBlocks();
        return isBlockChainValid(blocks);
    }


    //验证区块链是否有效
    public boolean isBlockChainValid(List<Block> blocks) throws ServiceException {

        if (CollectionUtils.isEmpty(blocks)) {
            return false;
        }

        //只有创世区块
        else if (blocks.size() == 1) {
            Block block = blocks.get(0);
            if (!Objects.equals(block.getPreviousHash(), "0")) {
                return false;
            }
        } else {
            //从创世区块之后的第一个区块开始
            for (int i = 1; i < blocks.size(); i++) {
                Block currentBlock = blocks.get(i);
                Block preBlock = blocks.get(i - 1);
                if (currentBlock == null || preBlock == null) {
                    return false;
                }
                //当前区块的hash必须和对整个区块做hash的结果一样
                if (!Objects.equals(currentBlock.getHash(), HashUtil.hash(currentBlock))) {
                    return false;
                }
                //当前区块的preHash必须和前一个区块一样
                if (!Objects.equals(currentBlock.getPreviousHash(), HashUtil.hash(preBlock))) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 共识算法
     */
    public void consensus(List<Block> otherNodeBlocks) throws ServiceException {
        if (!isBlockChainValid(otherNodeBlocks)) {
            return;
        }

        List<Block> blocks = getAllBlocks();

        if (otherNodeBlocks.size() <= blocks.size()) {
            return;
        }

        //每次都使用最长链替换当前节点
        for (int i = blocks.size(); i < otherNodeBlocks.size(); i++) {
            addBlock(otherNodeBlocks.get(i));
        }
    }


}