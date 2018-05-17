package com.peihan.vancleef.model;


import com.peihan.vancleef.util.StorageUtil;
import com.peihan.vancleef.util.WalletUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UTXO池，缓存UTXO，不用每次获取UTXO都迭代区块链
 */
public class UTXOPool {

    private static final StorageUtil storageUtil = StorageUtil.getInstance();

    private static volatile UTXOPool INSTANCE;


    private UTXOPool() {
        reIndex();
    }


    public static UTXOPool getInstance() {

        if (INSTANCE == null) {
            synchronized (UTXOPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UTXOPool();
                }
            }
        }
        return INSTANCE;
    }


    private void reIndex() {
        BlockChain blockChain = BlockChain.getInstance();
        Map<String, List<TxOutput>> allUXTOs = getAllUTXOs(blockChain);
        storageUtil.reGenerateChainstate(allUXTOs);
    }


    public Map<String, List<TxOutput>> getAllUTXOs(String address) {

        Map<String, List<TxOutput>> allUTXOs = storageUtil.getAllUTXOs();

        Map<String, List<TxOutput>> UTXOs = new HashMap<>();

        for (Map.Entry<String, List<TxOutput>> entry : allUTXOs.entrySet()) {
            String k = entry.getKey();
            List<TxOutput> txOutputs = entry.getValue();

            if (!CollectionUtils.isEmpty(txOutputs)) {
                for (TxOutput txOutput : txOutputs) {
                    if (txOutput.isLockedWithKey(WalletUtil.getPublicKeyHash(address))) {
                        if (UTXOs.get(k) == null) {
                            List<TxOutput> tops = new ArrayList<>();
                            tops.add(txOutput);
                            UTXOs.put(k, tops);
                        } else {
                            UTXOs.get(k).add(txOutput);
                        }
                    }
                }
            }
        }

        return UTXOs;
    }


    //添加区块时需要同步更新UXTO池
    public void updateUXTOPool(Block block) {
        List<Transaction> transactions = block.getTransactions();

        for (Transaction transaction : transactions) {

            //非创币交易需要处理交易输入
            if (!transaction.isCoinbase()) {
                for (TxInput txInput : transaction.getTxInputs()) {
                    String key = txInput.getTxId();
                    List<TxOutput> originalTxOutputs = storageUtil.getUXTOs(key);
                    List<TxOutput> newTxOutputs = new ArrayList<>();


                    for (TxOutput originalTxOutput : originalTxOutputs) {
                        if (originalTxOutput.getIndex() != txInput.getTxOutputIndex()) {
                            newTxOutputs.add(originalTxOutput);
                        }
                    }

                    if (CollectionUtils.isEmpty(newTxOutputs)) {
                        storageUtil.deleteUTXOs(key);
                    } else {
                        storageUtil.putUXTOs(key, newTxOutputs);
                    }
                }
            }
            //交易输出直接add
            List<TxOutput> txOutputs = transaction.getTxOutputs();
            storageUtil.putUXTOs(transaction.getTxId(),txOutputs);
        }
    }


    /**
     * 获取所有的未花费的交易输出
     * UTXO: 未花费的交易输出
     */
    private Map<String, List<TxOutput>> getAllUTXOs(BlockChain blockChain) {
        Map<String, List<Integer>> allSTXOs = getAllSTXOs(blockChain);
        Map<String, List<TxOutput>> allUTXOs = new HashMap<>();
        BlockChain.BlockChainIterator iterator = blockChain.getBlockChainIterator();

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
     */
    private Map<String, List<Integer>> getAllSTXOs(BlockChain blockChain) {

        Map<String, List<Integer>> allSTXO = new HashMap<>();

        BlockChain.BlockChainIterator iterator = blockChain.getBlockChainIterator();

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


}
