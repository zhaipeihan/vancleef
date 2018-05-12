package con.peihan.vancleef;

import com.peihan.vancleef.model.Wallet;
import com.peihan.vancleef.util.SerializeUtil;
import com.peihan.vancleef.util.StorageUtil;
import com.peihan.vancleef.util.WalletUtil;
import org.junit.Test;

import java.util.Arrays;

public class TestStorge {


    @Test
    public void name() throws Exception {
        Wallet wallet = new Wallet();
        String address = WalletUtil.getAddress(wallet.getPublicKey());

        StorageUtil storageUtil = StorageUtil.getInstance();
        storageUtil.putWallet(address, wallet);

        Wallet getWallet = storageUtil.getWallet(address);

        assert Arrays.equals(wallet.getPublicKey(), getWallet.getPublicKey());
        assert wallet.getPrivateKey().equals(getWallet.getPrivateKey());
        assert wallet.equals(getWallet);
    }


    @Test
    public void name2() throws Exception {
        Wallet wallet = new Wallet();
        Wallet wallet1 = (Wallet) SerializeUtil.JDKDeSerialize(SerializeUtil.JDKSerialize(wallet));
        assert wallet.getPrivateKey().equals(wallet1.getPrivateKey());
    }
}
