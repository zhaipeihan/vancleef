package com.peihan.vancleef.model;


import com.peihan.vancleef.exception.InvalidBlockException;
import com.peihan.vancleef.exception.base.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block {

    /**
     * 当前区块hash
     */
    private String hash;

    /**
     * 前一个区块hash
     */
    private String previousHash;

    /**
     * 交易数据
     */
    private List<Transaction> transactions;

    /**
     * 时间戳
     */
    private long timeStamp;


    /**
     * 随机数 用来完成pow
     * 创世区块的nonce为0
     */
    private long nonce;


    public static void checkBlock(Block block) throws ServiceException {
        if (block == null) {
            throw new InvalidBlockException("block不能为空");
        }
        if (StringUtils.isEmpty(block.getPreviousHash())) {
            throw new InvalidBlockException("previousHash不能为空");
        }
        if (StringUtils.isEmpty(block.getHash())) {
            throw new InvalidBlockException("hash不能为空");
        }
    }

}
