package com.peihan.vancleef.model;


import lombok.Data;

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

}
