package com.peihan.vancleef.model;


import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.util.MagicUtil;
import com.peihan.vancleef.util.StorageUtil;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 区块链
 */
@Data
public class BlockChain {

    private static final Logger logger = LogManager.getLogger();
    private final StorageUtil storage = StorageUtil.getInstance();

    private String lastBlockHash;


    /**
     * 获取创世区块
     * 创世区块nonce为0
     */
    private Block makeGenesisBlock() {
        Block block = new Block();
        block.setIndex(0);
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
    public void initBlockChain() throws ServiceException {
        Block genesisBlock = makeGenesisBlock();
        addBlock(genesisBlock);
    }

    /**
     * 增加一个区块
     * @param block
     * @throws ServiceException
     */
    public void addBlock(Block block) throws ServiceException {
        storage.putBlock(block);
        storage.putLastBlockHash(block.getHash());
        refreshLastBlockHash();
    }

    private void refreshLastBlockHash() throws ServiceException {
        this.lastBlockHash = storage.getLastBlockHash();
    }


    /**
     * 向区块链中添加一个区块，返回当前区块链中的最后索引
     */
    /*public long addBlock(String data) throws ServiceException {
        Block block = new Block();
        block.setData(data);
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setPreviousHash(lastBlock().getHash());
        block.setIndex(lastBlock().getIndex() + 1);
        //需要进行执行pow算法
        Pow.pow(block);

        addBlock(block);
        return lastIndex();
    }*/

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