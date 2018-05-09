package com.peihan.vancleef.cli;


import com.peihan.vancleef.exception.base.ServiceException;

public interface CliFacade {

    /**
     * 初始化区块链
     * @throws ServiceException
     */
    void initBlockChain() throws ServiceException;

    /**
     * 增加区块
     * @param data
     * @throws ServiceException
     */
    void addBlock(String data) throws ServiceException;


    void showAllBlocks() throws ServiceException;

}
