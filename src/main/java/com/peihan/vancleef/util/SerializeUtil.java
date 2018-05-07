package com.peihan.vancleef.util;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.peihan.vancleef.exception.base.SystemException;

import java.util.Arrays;

/**
 * 序列化和反序列化的工具类
 * 使用kryo实现
 */
public class SerializeUtil {

    private static final Kryo kryo = new Kryo();

    public static byte[] serialize(Object object) {
        if (object == null) {
            throw new SystemException("serialize object can not be null");
        }
        Output output = new Output(4096, 4096);
        kryo.writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }

    public static Object deSerialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            //此处暂时老考虑不抛异常
            //throw new SystemException("deSerialize bytes can not be empty(null or length == 0)");
            return null;
        }
        Input input = new Input(bytes);
        Object object = kryo.readClassAndObject(input);
        input.close();
        return object;
    }
}
