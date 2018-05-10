package com.peihan.vancleef.facade;


import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.model.Block;

import java.util.List;

public interface BlockChainFacade {

    /**
     * 初始化区块链
     * @throws ServiceException
     */
    void initBlockChain(String address) throws ServiceException;

    /**
     * 增加区块
     * @param data
     * @throws ServiceException
     */
    void addBlock(String data) throws ServiceException;


    List<Block> getAllBlocks() throws ServiceException;

    /**
     * 获取给定地址的余额
     * @return
     */
    long getBalance(String address);

}