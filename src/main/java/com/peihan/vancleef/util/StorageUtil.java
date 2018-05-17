package com.peihan.vancleef.util;


import com.peihan.vancleef.exception.OperateFailedException;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.exception.base.SystemException;
import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.TxInput;
import com.peihan.vancleef.model.TxOutput;
import com.peihan.vancleef.model.Wallet;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持久化工具类
 * 使用RocksDb进行存储
 */
public class StorageUtil {
    private static final Logger logger = LogManager.getLogger();


    private static final String DB_PATH = "blockchain-db";

    //区块链上最后一个区块的hash
    private static final String LAST_BLOCK_HASH = "l";


    //存储UTXO集
    private static final String CHAINSTATE = "chainstate";

    //chainstate桶
    private Map<String, List<TxOutput>> chainstateBucket;


    private RocksDB rocksDB;

    private volatile static StorageUtil INSTANCE;

    public static StorageUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (StorageUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StorageUtil();
                }
            }
        }
        return INSTANCE;
    }

    private StorageUtil() {
        //连接数据库
        connection();
        initChainstateBucket();
    }

    private void initChainstateBucket() {
        try {
            Map<String, List<TxOutput>> chainstate = (Map<String, List<TxOutput>>) SerializeUtil.deSerialize(rocksDB.get(SerializeUtil.serialize(CHAINSTATE)));
            if(chainstate == null){
                this.chainstateBucket = new ConcurrentHashMap<>();
                rocksDB.put(SerializeUtil.serialize(CHAINSTATE),SerializeUtil.serialize(chainstateBucket));
            }else{
                this.chainstateBucket = chainstate;
            }
        } catch (RocksDBException e) {
            throw new SystemException("init chainstate bucket error!");
        }
    }

    private void connection() {
        try {
            rocksDB = RocksDB.open(DB_PATH);
        } catch (Exception e) {
            logger.error("connect rocksdb error! exception{}", e);
            throw new SystemException("rocksdb连接错误");
        }
    }

    public void putBlock(Block block) throws ServiceException {
        Block.checkBlock(block);
        if (getBlock(block.getHash()) != null) {
            throw new OperateFailedException("block已存在");
        }
        try {
            this.rocksDB.put(SerializeUtil.serialize(block.getHash()), SerializeUtil.serialize(block));
        } catch (Exception e) {
            logger.error("save block error! block:{},exception{}", block, e);
            throw new OperateFailedException("put block error");
        }
    }


    public Block getBlock(String hash) {
        if (StringUtils.isEmpty(hash)) {
            throw new SystemException("hash不能为空");
        }
        Block block = null;
        try {
            block = (Block) SerializeUtil.deSerialize(rocksDB.get(SerializeUtil.serialize(hash)));
        } catch (Exception e) {
            logger.error("get block error! hash:{},exception{}", hash, e);
            throw new SystemException(String.format("hash值为%s的区块get异常", hash));
        }
        return block;
    }


    public String getLastBlockHash() {
        String lastHash = null;
        try {
            lastHash = (String) SerializeUtil.deSerialize(rocksDB.get(SerializeUtil.serialize(LAST_BLOCK_HASH)));
        } catch (Exception e) {
            logger.error("get lastHash error! exception{}", e);
            throw new SystemException("get lastHash error");
        }
        return lastHash;
    }

    public void putLastBlockHash(String lastHash) {
        try {
            rocksDB.put(SerializeUtil.serialize(LAST_BLOCK_HASH), SerializeUtil.serialize(lastHash));
        } catch (Exception e) {
            logger.error("put lastHash error! exception{}", e);
            throw new SystemException("get lastHash error");
        }
    }


    public List<TxOutput> getUXTOs(String key){
        return this.chainstateBucket.getOrDefault(key,null);
    }

    public void putUXTOs(String key, List<TxOutput> txOutputs){
        this.chainstateBucket.put(key,txOutputs);
        flushChainstate();
    }

    public void cleanChainstateBucket(){
        this.chainstateBucket.clear();
        flushChainstate();
    }

    public void reGenerateChainstate(Map<String, List<TxOutput>> chainstate){
        this.chainstateBucket = chainstate;
        flushChainstate();
        initChainstateBucket();
    }


    public Map<String,List<TxOutput>> getAllUTXOs(){
        return this.chainstateBucket;
    }

    public void deleteUTXOs(String key){
        this.chainstateBucket.remove(key);
    }


    private void flushChainstate(){
        try {
            rocksDB.put(SerializeUtil.serialize(CHAINSTATE),SerializeUtil.serialize(this.chainstateBucket));
        } catch (RocksDBException e) {
            throw new SystemException("put UXTO error");
        }
    }

    /*public void putWallet(String address, Wallet wallet) {
        try {
            rocksDB.put(SerializeUtil.JDKSerialize(address), SerializeUtil.JDKSerialize(wallet));
        } catch (RocksDBException e) {
            logger.error("put wallet error! exception {}", e);
            throw new SystemException("put wallet error!");
        }
    }


    public Wallet getWallet(String address) {
        Wallet wallet = null;
        try {
            wallet = (Wallet) SerializeUtil.JDKDeSerialize(rocksDB.get(SerializeUtil.JDKSerialize(address)));
        } catch (RocksDBException e) {
            logger.error("get wallet error ! exception:{}", e);
            throw new SystemException("get wallet error");
        }
        return wallet;
    }
*/
}
