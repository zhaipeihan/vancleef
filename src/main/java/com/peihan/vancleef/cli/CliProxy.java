package com.peihan.vancleef.cli;


import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.facade.BlockChainFacade;
import com.peihan.vancleef.facade.BlockChainFacadeImpl;
import com.peihan.vancleef.model.Block;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class CliProxy {

    private BlockChainFacade blockChainFacade;

    public CliProxy(){
        blockChainFacade = new BlockChainFacadeImpl();
    }


    public void showAllBlocks() throws ServiceException {
        List<Block> blocks = blockChainFacade.getAllBlocks();
        if(CollectionUtils.isEmpty(blocks)){
            System.out.println("has no block!");
        }else{
            for (Block block : blocks) {
                System.out.println(block);
            }
        }
    }

    public void initBlockChain(String address) throws ServiceException {
        blockChainFacade.initBlockChain(address);
    }

    public void addBlock(String data) throws ServiceException {
        blockChainFacade.addBlock(data);
    }

    public void showBalance(String address) {
        System.out.println(String.format("Balance of %s : %s",address,blockChainFacade.getBalance(address)));
    }
}
