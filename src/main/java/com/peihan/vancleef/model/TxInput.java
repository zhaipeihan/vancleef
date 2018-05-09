package com.peihan.vancleef.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 解锁脚本
     */
    private String scriptKey;
}
