package com.peihan.vancleef.model;


import com.peihan.vancleef.util.HashUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxInput {

    /**
     * 交易id
     */
    private String txId;

    /**
     * 交易输出索引
     */
    private int txOutputIndex;

    /**
     * 签名
     */
    private byte[] signature;

    /**
     * 公钥（未hash）
     */
    private byte[] pubKey;


    /**
     * 检查公钥hash是否用于交易输入
     *
     * @param pubKeyHash
     * @return
     */
    public boolean usesKey(byte[] pubKeyHash) {
        byte[] lockingHash = HashUtil.ripemd160Hash(this.getPubKey());
        return Arrays.equals(lockingHash, pubKeyHash);
    }

}
