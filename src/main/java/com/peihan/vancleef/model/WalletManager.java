package com.peihan.vancleef.model;


import com.peihan.vancleef.exception.base.SystemException;
import com.peihan.vancleef.util.SerializeUtil;
import lombok.Data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理多个wallet
 */
@Data
public class WalletManager {

    private volatile static WalletManager INSTANCE;


    private static final String FILE_PATH = "./wallet/wallet.dat";

    private Map<String, Wallet> walletMap;


    private WalletManager() {

    }

    public static WalletManager getInstance() {
        if (INSTANCE == null) {
            synchronized (WalletManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WalletManager();
                }
            }
        }
        return INSTANCE;
    }


    public void init() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            this.walletMap = new HashMap<>(16);
        } else {
            loadFromFile(file);
        }
    }

    public void addWallet(String address, Wallet wallet) {
        this.walletMap.put(address, wallet);
        saveToFile();
    }


    public Wallet getWallet(String address) {
        return this.walletMap.getOrDefault(address, null);
    }

    private void saveToFile() {

        File file = new File(FILE_PATH);
        if (!file.exists() && !file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new SystemException("目录创建失败");
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(SerializeUtil.JDKSerialize(this.walletMap));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile(File file) {
        this.walletMap = (HashMap<String, Wallet>) SerializeUtil.JDKDeSerialize(readFromFile(file));
    }

    private byte[] readFromFile(File file) {
        byte[] bytes = null;
        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            byteArrayOutputStream = new ByteArrayOutputStream();
            int x = 0;
            while ((x = bufferedInputStream.read()) != -1) {
                byteArrayOutputStream.write(x);
            }
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SystemException("read file error!");
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

}
