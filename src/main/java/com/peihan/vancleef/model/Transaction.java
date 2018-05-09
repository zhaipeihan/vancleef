package com.peihan.vancleef.model;

import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.SerializeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    /**
     * 固定奖励
     */
    private static final int REWARD = 10;


    /**
     * 交易的hash
     */
    private String txId;

    /**
     * 交易输入
     */
    private TxInput[] txInputs;

    /**
     * 交易输出
     */
    private TxOutput[] txOutputs;


    public Transaction makeCoinbaseTx(String to, String data) {
        TxOutput txOutput = new TxOutput(REWARD,to);
        TxInput txInput = new TxInput("0",-1,data);

        Transaction transaction = new Transaction(null,new TxInput[]{txInput},new TxOutput[]{txOutput});
        transaction.setTxId(HashUtil.hash(SerializeUtil.serialize(transaction)));
        return transaction;
    }
}
