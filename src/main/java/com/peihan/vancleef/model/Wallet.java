package com.peihan.vancleef.model;

import com.peihan.vancleef.exception.base.SystemException;
import lombok.Data;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

@Data
public class Wallet {

    /**
     * 私钥
     */
    private BCECPrivateKey privateKey;
    /**
     * 公钥
     */
    private byte[] publicKey;


    public Wallet() {
        initWallet();
    }

    private void initWallet() {
        try {
            KeyPair keyPair = makeKeyPair();
            BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
            BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

            byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);

            this.setPrivateKey(privateKey);
            this.setPublicKey(publicKeyBytes);
        } catch (Exception e) {
            throw new SystemException("init wallet error!");
        }

    }

    /**
     * 创建新的密钥对
     *
     * @return
     * @throws Exception
     */
    private KeyPair makeKeyPair() throws Exception {
        // 注册 BC Provider
        Security.addProvider(new BouncyCastleProvider());
        // 创建椭圆曲线算法的密钥对生成器，算法为 ECDSA
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        // 椭圆曲线（EC）域参数设定
        // bitcoin 为什么会选择 secp256k1，详见：https://bitcointalk.org/index.php?topic=151120.0
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        g.initialize(ecSpec, new SecureRandom());
        return g.generateKeyPair();
    }

}
