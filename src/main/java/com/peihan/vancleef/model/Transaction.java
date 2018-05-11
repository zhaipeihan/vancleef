package com.peihan.vancleef.model;

import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.SerializeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

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


    //创币交易
    public static Transaction makeCoinbaseTx(String address, String data) {
        TxOutput txOutput = TxOutput.makeTxOutput(REWARD, address);
        TxInput txInput = new TxInput("0", -1, null, data.getBytes());
        Transaction transaction = new Transaction(null, new ArrayList<>(Collections.singletonList(txInput)), new ArrayList<>(Collections.singletonList(txOutput)));
        transaction.setTxId(HashUtil.hash(transaction));
        transaction.refreshTxOutputIndex();
        return transaction;
    }

    /**
     * 是否是创币交易
     *
     * @return
     */
    public boolean isCoinbase() {
        if (this.txInputs != null && this.txInputs.size() == 1
                && this.txInputs.get(0).getTxOutputIndex() == -1
                && this.txInputs.get(0).getTxId().equals("0")) {
            return true;
        }
        return false;
    }


    public void refreshTxOutputIndex() {
        if (!CollectionUtils.isEmpty(txOutputs)) {
            ListIterator<TxOutput> iterator = txOutputs.listIterator();
            while(iterator.hasNext()){
                int index = iterator.nextIndex();
                iterator.next().setIndex(index);
            }
        }
    }


}
