package com.peihan.vancleef.util;


import com.peihan.vancleef.exception.OperateFailedException;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.exception.base.SystemException;
import com.peihan.vancleef.model.Block;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rocksdb.RocksDB;

/**
 * 持久化工具类
 * 使用RocksDb进行存储
 */
public class StorageUtil {
    private static final Logger logger = LogManager.getLogger();


    private static final String DB_PATH = "blockchain-db";

    //区块链上最后一个区块的hash
    private static final String LAST_BLOCK_HASH = "l";

    public RocksDB rocksDB;

    private volatile static StorageUtil INSTANCE;

    public static StorageUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (StorageUtil.class) {
                if (INSTANCE == null) {
                    return new StorageUtil();
                }
            }
        }
        return INSTANCE;
    }

    private StorageUtil() {
        //连接数据库
        connection();
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


    public Block getBlock(String hash) throws ServiceException {
        if (StringUtils.isEmpty(hash)) {
            throw new ServiceException("hash不能为空");
        }
        Block block = null;
        try {
            block = (Block) SerializeUtil.deSerialize(rocksDB.get(SerializeUtil.serialize(hash)));
        } catch (Exception e) {
            logger.error("get block error! hash:{},exception{}", hash, e);
            throw new OperateFailedException(String.format("hash值为%s的区块get异常", hash));
        }
        return block;
    }


    public String getLastHash() throws ServiceException {
        String lastHash = null;
        try {
            lastHash = (String) SerializeUtil.deSerialize(rocksDB.get(SerializeUtil.serialize(LAST_BLOCK_HASH)));
        } catch (Exception e) {
            logger.error("get lastHash error! exception{}", e);
            throw new OperateFailedException("get lastHash error");
        }
        return lastHash;
    }

    public void putLastHash(String lastHash) throws ServiceException {
        try {
            rocksDB.put(SerializeUtil.serialize(LAST_BLOCK_HASH), SerializeUtil.serialize(lastHash));
        } catch (Exception e) {
            logger.error("put lastHash error! exception{}", e);
            throw new OperateFailedException("get lastHash error");
        }
    }

}
