package com.peihan.vancleef.model;


import com.peihan.vancleef.action.Pow;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.util.MagicUtil;
import com.peihan.vancleef.util.StorageUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 区块链
 */
public class BlockChain {

    private static final Logger logger = LogManager.getLogger();

    private final StorageUtil storage = StorageUtil.getInstance();

    private String lastBlockHash;

    public class BlockChainIterator {

        private String currentBlockHash;

        public BlockChainIterator(String currentBlockHash) {
            this.currentBlockHash = currentBlockHash;
        }

        public boolean hasNext() {
            if (StringUtils.isEmpty(currentBlockHash)) {
                return false;
            }
            Block block = null;
            try {
                block = storage.getBlock(currentBlockHash);
            } catch (ServiceException e) {
                logger.error("get block error exception:{}", e);
            }
            return block != null;
        }

        public Block next() throws ServiceException {
            Block block = storage.getBlock(currentBlockHash);
            currentBlockHash = block.getPreviousHash();
            return block;
        }
    }

    /**
     * 获取区块链的迭代器
     * @return
     */
    public BlockChainIterator getBlockChainIterator(){
        return new BlockChainIterator(this.lastBlockHash);
    }

    /**
     * 创建区块链
     * 先查看rocksdb里有没有对应的区块链信息，如果有，从rocksdb里创建，否则创建新的区块链
     *
     */
    public void createBlockChain() throws ServiceException {
        String lastBlockHash = storage.getLastBlockHash();
        if(StringUtils.isEmpty(lastBlockHash)){
            initBlockChain();
        }else{
            refreshLastBlockHash();
        }
    }

    /**
     * 向区块链中添加一个区块，返回当前区块链中的最后索引
     */
    public void addBlock(String data) throws ServiceException {
        Block block = new Block();
        block.setData(data);
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setPreviousHash(lastBlockHash);
        //需要进行执行pow算法
        Pow.pow(block);
        addBlock(block);
    }


    /**
     * 获取创世区块
     * 创世区块nonce为0
     */
    private Block makeGenesisBlock() {
        Block block = new Block();
        block.setPreviousHash(MagicUtil.makeEmptyHashStr());
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setData("this is the genesis block");
        block.setNonce(0);
        block.setHash(MagicUtil.hash(block));
        return block;
    }

    /**
     * 初始化区块链，并且增加一个创世区块
     */
    private void initBlockChain() throws ServiceException {
        Block genesisBlock = makeGenesisBlock();
        addBlock(genesisBlock);
    }

    /**
     * 增加一个区块
     *
     * @param block
     * @throws ServiceException
     */
    private void addBlock(Block block) throws ServiceException {
        storage.putBlock(block);
        storage.putLastBlockHash(block.getHash());
        refreshLastBlockHash();
    }

    private void refreshLastBlockHash() throws ServiceException {
        this.lastBlockHash = storage.getLastBlockHash();
    }


    /*
    public boolean isBlockChainValid() {
        if (blockChain == null || blockChain.size() < 1) {
            return false;
        }
        //只有创世区块
        else if (blockChain.size() == 1) {
            Block block = blockChain.get(0);
            if (!Objects.equals(block.getPreviousHash(), "0")) {
                return false;
            }
        } else {
            //从创世区块之后的第一个区块开始
            for (int i = 1; i < blockChain.size(); i++) {
                Block currentBlock = blockChain.get(i);
                Block preBlock = blockChain.get(i - 1);
                if (currentBlock == null || preBlock == null) {
                    return false;
                }
                //当前区块的hash必须和对整个区块做hash的结果一样
                if (!Objects.equals(currentBlock.getHash(), MagicUtil.hash(currentBlock))) {
                    return false;
                }
                //当前区块的preHash必须和前一个区块一样
                if (!Objects.equals(currentBlock.getPreviousHash(), MagicUtil.hash(preBlock))) {
                    return false;
                }
            }
        }
        return true;
    }
    */


}