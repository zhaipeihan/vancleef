package com.peihan.vancleef;

import com.peihan.vancleef.model.TxInput;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TestIt {


    @Test
    public void name() throws Exception {

        List<TxInput> txInputs = new ArrayList<>();
        txInputs.add(new TxInput("1",1,null,null));


        ListIterator<TxInput> listIterator = txInputs.listIterator();

        while(listIterator.hasNext()){
            TxInput txInput = listIterator.next();
            byte[] data = new byte[]{0,1};
            txInput.setPublicKey(data);
            txInput.setSignature(data);
        }

        System.out.println(txInputs);





 }
}
