package com.peihan.vancleef.util;


import com.peihan.vancleef.model.Wallet;

import java.util.Arrays;

/**
 * 钱包相关的工具类，提供相关的地址转换
 * 增加checkHash在Base58Util中完成
 */
public class WalletUtil {

    /**
     * 生成钱包地址算法
     * 1. 首先使用随机数发生器生成一个『私钥』。一般来说这是一个256bits的数，拥有了这串数字就可以对相应『钱包地址』中的比特币进行操作，所以必须被安全地保存起来。
     * 2. 『私钥』经过SECP256K1算法处理生成了『公钥』。SECP256K1是一种椭圆曲线算法，通过一个已知『私钥』时可以算得『公钥』，而『公钥』已知时却无法反向计算出『私钥』。这是保障比特币安全的算法基础。
     * 3. 同SHA256一样，RIPEMD160也是一种Hash算法，由『公钥』可以计算得到『公钥哈希』，而反过来是行不通的。
     * 4. 将一个字节的地址版本号连接到『公钥哈希』头部（对于比特币网络的pubkey地址，这一字节为“0”），然后对其进行两次SHA256运算，将结果的前4字节作为『公钥哈希』的校验值，连接在其尾部。
     * 5. 将上一步结果使用BASE58进行编码(比特币定制版本)，就得到了『钱包地址』。
     * 6. 比如, 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa
     *
     * @return 钱包地址
     */
    public static String getAddress(byte[] publicKey) {
        return Base58Util.bytesToBase58(addVersion(HashUtil.ripemd160Hash(publicKey)));
    }

    /**
     * 进行地址到publicKeyHash的逆过程
     *
     * @param address
     * @return
     */
    public static byte[] getPublicKeyHash(String address) {
        byte[] versionedPayload = Base58Util.base58ToBytes(address);
        //排除版本号
        return Arrays.copyOfRange(versionedPayload, 1, versionedPayload.length);
    }

    /**
     * 根据原始公钥生成公钥hash
     *
     * @param publicKey
     * @return
     */
    public static byte[] getPublicKeyHash(byte[] publicKey) {
        return HashUtil.ripemd160Hash(publicKey);
    }


    public static Wallet getWalletByAddress(String address) {
        return StorageUtil.getInstance().getWallet(address);
    }

    /**
     * 在publicKey的开头增加0x00的版本号
     *
     * @param source
     * @return
     */
    private static byte[] addVersion(byte[] source) {
        return MagicUtil.mergeBytes(new byte[]{0}, source);
    }

}
