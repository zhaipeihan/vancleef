package com.peihan.vancleef.model;


import com.peihan.vancleef.exception.InvalidBlockException;
import com.peihan.vancleef.exception.base.ServiceException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Block {


    /**
     * 区块索引
     */
    private long index;

    /**
     * 当前区块hash
     */
    private String hash;

    /**
     * 前一个区块hash
     */
    private String previousHash;

    /**
     * 数据
     * 根据应用的具体情况可以存放不同的数据
     * 可以存放JSON数据
     */
    private String data;

    /**
     * 时间戳
     */
    private long timeStamp;


    /**
     * 随机数 用来完成pow
     * 创世区块的nonce为0
     */
    private long nonce;


    public static void checkBlock(Block block) throws ServiceException{
        if(block == null){
            throw new InvalidBlockException("block不能为空");
        }
        if(StringUtils.isEmpty(block.getPreviousHash())){
            throw new InvalidBlockException("previousHash不能为空");
        }
        if(StringUtils.isEmpty(block.getHash())){
            throw new InvalidBlockException("hash不能为空");
        }
    }

}
