package com.peihan.vancleef.util;


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
import java.util.Arrays;

/**
 * 签名工具
 */
public class SignatureUtil {

    public static byte[] sign(byte[] source, BCECPrivateKey privateKey) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(source);
        return ecdsaSign.sign();
    }


    public static boolean verify(byte[] source, byte[] sign, byte[] publicKey) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        ECParameterSpec ecParameters = ECNamedCurveTable.getParameterSpec("secp256k1");
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);

        BigInteger x = new BigInteger(1, Arrays.copyOfRange(publicKey, 1, 33));
        BigInteger y = new BigInteger(1, Arrays.copyOfRange(publicKey, 33, 65));
        ECPoint ecPoint = ecParameters.getCurve().createPoint(x, y);

        ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, ecParameters);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        ecdsaVerify.initVerify(pubKey);
        ecdsaVerify.update(source);
        return ecdsaVerify.verify(sign);
    }


}
