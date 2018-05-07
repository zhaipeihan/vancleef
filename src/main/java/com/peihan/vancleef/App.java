package com.peihan.vancleef;

import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import com.peihan.vancleef.util.StorageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {


    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws ServiceException {
        BlockChain blockChain = new BlockChain();
        blockChain.createBlockChain();
        String lastHash = StorageUtil.getInstance().getLastBlockHash();
        logger.info("当前最后一个区块hash为{}", lastHash);
        Block block = StorageUtil.getInstance().getBlock(lastHash);
        logger.info("最后一个区块信息为{}", block);

    }
}
