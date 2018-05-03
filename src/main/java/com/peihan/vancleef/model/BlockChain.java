package com.peihan.vancleef.model;


import com.peihan.vancleef.util.MagicUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块链
 */
@Data
public class BlockChain {

    /**
     * 区块链
     */
    private List<Block> blockChain;

    /**
     * 获取创世区块
     */
    private Block makeGenesisBlock() {
        Block block = new Block();
        block.setIndex(0);
        block.setPreviousHash(MagicUtil.makeEmptyHashStr());
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setData("this is the genesis block");
        block.setHash(MagicUtil.hash(block));
        return block;
    }

    /**
     * 初始化初始化，并且增加一个创世区块
     */
    public void initBlockChain() {
        blockChain = new ArrayList<>();
        blockChain.add(makeGenesisBlock());
    }


    /**
     * 向区块链中添加一个区块，返回当前区块链中的最后索引
     */
    public long addBlock(String data) {
        Block block = new Block();
        block.setData(data);
        block.setTimeStamp(MagicUtil.getNowTimeStamp());
        block.setPreviousHash(lastBlock().getHash());
        block.setIndex(lastBlock().getIndex() + 1);
        block.setHash(MagicUtil.hash(block));
        blockChain.add(block);
        return lastIndex();
    }


    public void showAllBlocks() {
        blockChain.forEach(System.out::println);
    }


    private Block lastBlock() {
        return blockChain.get(lastIndex());
    }

    private int lastIndex() {
        return blockChain.size() - 1;
    }

}
