package com.peihan.vancleef.model;


import com.peihan.vancleef.util.WalletUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxOutput {

    /**
     * 输出索引
     */
    private int index;

    /**
     * 数值
     */
    private int value;

    /**
     * 公钥Hash
     */
    private byte[] publicKeyHash;


    public TxOutput(int value, byte[] pubKeyHash) {
        this.value = value;
        this.publicKeyHash = pubKeyHash;
    }

    /**
     * 创建交易输出
     *
     * @param value
     * @param address
     * @return
     */
    public static TxOutput makeTxOutput(int value, String address) {
        return new TxOutput(value, WalletUtil.getPublicKeyHash(address));
    }

    /**
     * 检查给定的pubkeyhash能否解锁交易输出
     *
     * @param pubKeyHash
     * @return
     */
    public boolean isLockedWithKey(byte[] pubKeyHash) {
        return Arrays.equals(this.publicKeyHash, pubKeyHash);
    }

}
