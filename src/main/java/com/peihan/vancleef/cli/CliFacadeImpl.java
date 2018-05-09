package com.peihan.vancleef.cli;

import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CliFacadeImpl implements CliFacade {

    private static final BlockChain blockChain = BlockChain.getInstance();
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void initBlockChain() throws ServiceException {
        blockChain.createBlockChain();
    }

    @Override
    public void addBlock(String data) throws ServiceException {
        blockChain.createBlockChain();
        blockChain.addBlock(data);
    }

    @Override
    public void showAllBlocks() throws ServiceException {
        blockChain.createBlockChain();
        List<Block> blocks = blockChain.getAllBlocks();
        if(CollectionUtils.isEmpty(blocks)){
            logger.info("has no block");
        }else{
            for (Block block : blocks) {
                logger.info(block);
            }
        }
    }
}
