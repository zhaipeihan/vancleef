package com.peihan.vancleef.facade;

import com.peihan.vancleef.cache.NodeBlockCache;
import com.peihan.vancleef.exception.OperateFailedException;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.BlockChain;
import com.peihan.vancleef.model.Transaction;
import com.peihan.vancleef.p2p.Node;
import com.peihan.vancleef.thread.NodeStartThread;
import com.peihan.vancleef.util.MagicUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BlockChainFacadeImpl implements BlockChainFacade {
    private static final BlockChain blockChain = BlockChain.getInstance();
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void initBlockChain(String address) throws ServiceException {
        blockChain.createBlockChain(address);
    }

    @Override
    public void addBlock(String data) throws ServiceException {
        blockChain.addBlock(new ArrayList<>(Collections.singleton(Transaction.makeCoinbaseTx(data, data))));
    }

    @Override
    public List<Block> getAllBlocks() throws ServiceException {
        return blockChain.getAllBlocks();

    }

    @Override
    public long getBalance(String address) {
        return blockChain.getBalance(address);
    }

    @Override
    public void transfer(String from, String to, int amount) throws ServiceException {
        blockChain.transfer(from, to, amount);
    }


    @Override
    public void pull() throws ServiceException {
        //请求其他节点的数据
        Node.getInstance().requestAllBlock();
        if (!NodeBlockCache.getInstance().isEmpty()) {
            logger.info("各节点缓存不为空，开始进行共识算法");
            Map<String, List<Block>> map = NodeBlockCache.getInstance().getAll();
            //对每个节点数据执行共识算法
            for (Map.Entry<String, List<Block>> entry : map.entrySet()) {
                List<Block> otherNodeBlocks = entry.getValue();
                blockChain.consensus(otherNodeBlocks);
            }
        }
    }


    @Override
    public void push() throws OperateFailedException {
        Node.getInstance().pushToOtherNode(blockChain.getAllBlocks());
    }
}
