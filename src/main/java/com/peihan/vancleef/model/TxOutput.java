package com.peihan.vancleef.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 锁定脚本
     */
    private String scriptPubKey;


    public boolean canBeUnlockedWith(String unlockingData) {
        return this.getScriptPubKey().endsWith(unlockingData);
    }

}
