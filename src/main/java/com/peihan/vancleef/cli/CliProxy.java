package com.peihan.vancleef.cli;


import com.peihan.vancleef.exception.OperateFailedException;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.facade.BlockChainFacade;
import com.peihan.vancleef.facade.BlockChainFacadeImpl;
import com.peihan.vancleef.facade.WalletFacade;
import com.peihan.vancleef.facade.WalletFacadeImpl;
import com.peihan.vancleef.model.Block;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class CliProxy {

    private BlockChainFacade blockChainFacade;

    private WalletFacade walletFacade;

    public CliProxy() {
        blockChainFacade = new BlockChainFacadeImpl();
        walletFacade = new WalletFacadeImpl();
    }


    public void showAllBlocks() throws ServiceException {
        List<Block> blocks = blockChainFacade.getAllBlocks();
        if (CollectionUtils.isEmpty(blocks)) {
            System.out.println("has no block!");
        } else {
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
        System.out.println(String.format("Balance of %s : %s", address, blockChainFacade.getBalance(address)));
    }

    public void transfer(String from, String to, int amount) throws ServiceException {
        blockChainFacade.transfer(from, to, amount);
    }

    public void createWallet() {
        String address = walletFacade.createWallet();
        System.out.println(String.format("wallet address : %s",address));
    }

    public void pull() throws ServiceException {
        blockChainFacade.pull();
    }

    public void push() throws OperateFailedException {
        blockChainFacade.push();
    }
}
