package com.peihan.vancleef.util;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.Transaction;
import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {


    public static String hash(Block block) {
        if (block == null) {
            return "";
        }
        //将整个区块做hash
        String source = block.getPreviousHash() + String.valueOf(block.getTimeStamp()) + hash(block.getTransactions()) + String.valueOf(block.getNonce());
        return DigestUtils.sha256Hex(source);
    }

    public static String hashWithNonce(Block block, long nonce) {
        if (block == null) {
            return "";
        }
        String source = block.getPreviousHash() + String.valueOf(block.getTimeStamp()) + hash(block.getTransactions()) + String.valueOf(nonce);
        return DigestUtils.sha256Hex(source);
    }

    public static String hash(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return DigestUtils.sha256Hex(bytes);
    }

    /**
     * 计算一组交易信息的hash值
     *
     * @param transactions
     * @return
     */
    public static String hash(Transaction[] transactions) {
        if (transactions == null || transactions.length == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Transaction transaction : transactions) {
            stringBuilder.append(transaction.getTxId());
        }
        return DigestUtils.sha256Hex(stringBuilder.toString());
    }


}
