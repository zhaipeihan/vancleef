package com.peihan.vancleef.model;

import com.peihan.vancleef.exception.VerifyFailedException;
import com.peihan.vancleef.util.HashUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

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
        transaction.setTxId(HashUtil.hashHex(transaction));
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
            while (iterator.hasNext()) {
                int index = iterator.nextIndex();
                iterator.next().setIndex(index);
            }
        }
    }


    public void sign(BCECPrivateKey privateKey, Map<String, Transaction> prevTxMap) throws Exception {
        //创币交易不需要签名
        if (this.isCoinbase()) {
            return;
        }

        //再次验证在preTxMap中是否找的到
        for (TxInput txInput : this.txInputs) {
            if (!prevTxMap.containsKey(txInput.getTxId()) || prevTxMap.get(txInput.getTxId()) == null) {
                throw new VerifyFailedException(String.format("txId:%s can not found"));
            }
        }

        //获得交易副本
        Transaction duplicateTx = duplicate();


        Security.addProvider(new BouncyCastleProvider());
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
        ecdsaSign.initSign(privateKey);


        ListIterator<TxInput> iterator = this.duplicate().getTxInputs().listIterator();


        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            TxInput dupTxInput = iterator.next();

            Transaction prevTx = prevTxMap.get(dupTxInput.getTxId());
            TxOutput txOutput = prevTx.getTxOutputs().get(dupTxInput.getTxOutputIndex());


            dupTxInput.setPublicKey(txOutput.getPublicKeyHash());
            dupTxInput.setSignature(null);

            byte[] txId = HashUtil.hash(duplicateTx);

            dupTxInput.setPublicKey(null);

            ecdsaSign.update(txId);

            byte[] signature = ecdsaSign.sign();
            this.txInputs.get(index).setSignature(signature);
        }
    }

    private Transaction duplicate() {
        List<TxInput> txInputs = this.txInputs.stream().map(txInput -> new TxInput(txInput.getTxId(), txInput.getTxOutputIndex(), null, null)).collect(Collectors.toList());
        List<TxOutput> txOutputs = this.txOutputs.stream().map(txOutput -> new TxOutput(txOutput.getValue(), txOutput.getPublicKeyHash())).collect(Collectors.toList());
        Transaction transaction = new Transaction(this.getTxId(), txInputs, txOutputs);
        transaction.refreshTxOutputIndex();
        return transaction;
    }
}
