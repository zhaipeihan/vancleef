package com.peihan.vancleef.model;

import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.SerializeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private List<TxInput> txInputs;

    /**
     * 交易输出
     */
    private List<TxOutput> txOutputs;


    //创世区块中的交易
    public static Transaction makeCoinbaseTx(String to, String data) {
        TxOutput txOutput = new TxOutput(REWARD, to);
        TxInput txInput = new TxInput("0", -1, data);

        Transaction transaction = new Transaction(null, new ArrayList<>(Collections.singletonList(txInput)),new ArrayList<>(Collections.singletonList(txOutput)));
        transaction.setTxId(HashUtil.hash(SerializeUtil.serialize(transaction)));
        return transaction;
    }

    /**
     * 是否是创币交易
     * @return
     */
    public boolean isCoinbase(){
        if(this.txInputs != null && this.txInputs.size() == 1
                && this.txInputs.get(0).getTxOutputIndex() == -1
                && this.txInputs.get(0).getTxId().equals("0")){
            return true;
        }
        return false;
    }


}
