package com.peihan.vancleef.util;

import com.peihan.vancleef.model.Block;
import com.peihan.vancleef.model.Transaction;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.util.List;

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

    public static String hash(Transaction transaction) {
        return hash(SerializeUtil.serialize(transaction));
    }

    /**
     * 计算一组交易信息的hash值
     *
     * @param transactions
     * @return
     */
    public static String hash(List<Transaction> transactions) {
        if (CollectionUtils.isEmpty(transactions)) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Transaction transaction : transactions) {
            stringBuilder.append(transaction.getTxId());
        }
        return DigestUtils.sha256Hex(stringBuilder.toString());
    }

    /**
     * 计算公钥的 RIPEMD160 Hash值
     * 此方法主要是用来根据公钥生成公钥hash
     *
     * @param pubKey 公钥
     * @return byte[]
     */
    public static byte[] ripemd160Hash(byte[] pubKey) {
        //1. 先对公钥做 sha256 处理
        byte[] shaHashedKey = DigestUtils.sha256(pubKey);
        RIPEMD160Digest ripemd160 = new RIPEMD160Digest();
        ripemd160.update(shaHashedKey, 0, shaHashedKey.length);
        byte[] output = new byte[ripemd160.getDigestSize()];
        ripemd160.doFinal(output, 0);
        return output;
    }


    /**
     * 二次hash计算
     * @param data
     * @return
     */
    public static byte[] doubleHash(byte[] data) {
        return DigestUtils.sha256(DigestUtils.sha256(data));
    }


}
