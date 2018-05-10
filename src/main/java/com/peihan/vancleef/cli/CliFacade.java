package com.peihan.vancleef.cli;


import com.peihan.vancleef.exception.base.ServiceException;

public interface CliFacade {

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


    void showAllBlocks() throws ServiceException;


    /**
     * 获取给定地址的余额
     * @return
     */
    long getBalance(String address);

}
