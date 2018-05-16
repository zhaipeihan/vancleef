package com.peihan.vancleef.util;

import com.peihan.vancleef.config.Config;
import com.peihan.vancleef.exception.OperateFailedException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class YamlUtil {

    public static Config getConfig() throws OperateFailedException {
        String path = YamlUtil.class.getClassLoader().getResource("config.yml").getPath();
        Yaml yaml = new Yaml();
        Config config = null;
        try {
            config = yaml.loadAs(new FileInputStream(new File(path)),Config.class);
        } catch (FileNotFoundException e) {
            throw new OperateFailedException("read config yaml error");
        }
        return config;
    }

}
