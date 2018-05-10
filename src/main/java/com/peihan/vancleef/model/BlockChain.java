package com.peihan.vancleef.model;


import com.peihan.vancleef.action.Pow;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.MagicUtil;
import com.peihan.vancleef.util.StorageUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 区块链
 */
public class BlockChain {

    private static final Logger logger = LogManager.getLogger();

    private final StorageUtil storage = StorageUtil.getInstance();

    private String lastBlockHash;

    private volatile static BlockChain INSTANCE;

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
        Block block = new Block();
        block.setTransactions(transactions);
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setPreviousHash(lastBlockHash);
        //需要进行执行pow算法
        Pow.pow(block);
        addBlock(block);
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
        Map<String, List<TxOutput>> UTXOs = getAllUTXOs(address);
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
                    if (txOutput.canBeUnlockedWith(address)) {

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
                    if (txInput.canBeUnlockedWith(address)) {
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
     * 获取创世区块
     * 创世区块nonce为0
     */
    private Block makeGenesisBlock(String address) {
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




    /*
    public boolean isBlockChainValid() {
        if (blockChain == null || blockChain.size() < 1) {
            return false;
        }
        //只有创世区块
        else if (blockChain.size() == 1) {
            Block block = blockChain.get(0);
            if (!Objects.equals(block.getPreviousHash(), "0")) {
                return false;
            }
        } else {
            //从创世区块之后的第一个区块开始
            for (int i = 1; i < blockChain.size(); i++) {
                Block currentBlock = blockChain.get(i);
                Block preBlock = blockChain.get(i - 1);
                if (currentBlock == null || preBlock == null) {
                    return false;
                }
                //当前区块的hash必须和对整个区块做hash的结果一样
                if (!Objects.equals(currentBlock.getHash(), MagicUtil.hash(currentBlock))) {
                    return false;
                }
                //当前区块的preHash必须和前一个区块一样
                if (!Objects.equals(currentBlock.getPreviousHash(), MagicUtil.hash(preBlock))) {
                    return false;
                }
            }
        }
        return true;
    }
    */


}