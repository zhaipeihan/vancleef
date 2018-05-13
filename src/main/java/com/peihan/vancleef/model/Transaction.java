package com.peihan.vancleef.model;

import com.peihan.vancleef.exception.VerifyFailedException;
import com.peihan.vancleef.util.HashUtil;
import com.peihan.vancleef.util.SignatureUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private static final Logger logger = LogManager.getLogger();

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
                throw new VerifyFailedException(String.format("txId:%s can not found", txInput.getTxId()));
            }
        }

        //获得交易副本
        Transaction duplicateTx = duplicate();

        ListIterator<TxInput> iterator = duplicateTx.getTxInputs().listIterator();


        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            TxInput dupTxInput = iterator.next();

            Transaction prevTx = prevTxMap.get(dupTxInput.getTxId());
            TxOutput txOutput = prevTx.getTxOutputs().get(dupTxInput.getTxOutputIndex());


            dupTxInput.setPublicKey(txOutput.getPublicKeyHash());
            dupTxInput.setSignature(null);


            byte[] txIds = HashUtil.hash(duplicateTx);

            dupTxInput.setPublicKey(null);

            byte[] signature = SignatureUtil.sign(txIds, privateKey);
            this.txInputs.get(index).setSignature(signature);
        }
    }

    public Transaction duplicate() {
        List<TxInput> txInputs = this.txInputs.stream().map(txInput -> new TxInput(txInput.getTxId(), txInput.getTxOutputIndex(), null, null)).collect(Collectors.toList());
        List<TxOutput> txOutputs = this.txOutputs.stream().map(txOutput -> new TxOutput(txOutput.getValue(), txOutput.getPublicKeyHash())).collect(Collectors.toList());
        Transaction transaction = new Transaction(this.getTxId(), txInputs, txOutputs);
        transaction.refreshTxOutputIndex();
        return transaction;
    }

    public boolean verify(Map<String, Transaction> prevTxMap) throws Exception {
        //创币交易不需要签名
        if (this.isCoinbase()) {
            return true;
        }

        //再次验证在preTxMap中是否找的到
        for (TxInput txInput : this.txInputs) {
            if (!prevTxMap.containsKey(txInput.getTxId()) || prevTxMap.get(txInput.getTxId()) == null) {
                throw new VerifyFailedException(String.format("txId:%s can not found"));
            }
        }

        //获得交易副本
        Transaction duplicateTx = duplicate();


        ListIterator<TxInput> iterator = this.getTxInputs().listIterator();
        while (iterator.hasNext()) {

            int index = iterator.nextIndex();
            TxInput txInput = iterator.next();

            Transaction prevTx = prevTxMap.get(txInput.getTxId());


            TxOutput prevTxOutput = prevTx.getTxOutputs().get(txInput.getTxOutputIndex());


            TxInput copyTxInput = duplicateTx.getTxInputs().get(index);
            copyTxInput.setSignature(null);
            copyTxInput.setPublicKey(prevTxOutput.getPublicKeyHash());


            byte[] txids = HashUtil.hash(duplicateTx);
            copyTxInput.setPublicKey(null);



            if (!SignatureUtil.verify(txids, txInput.getSignature(), txInput.getPublicKey())) {
                return false;
            }

        }
        return true;
    }
}
