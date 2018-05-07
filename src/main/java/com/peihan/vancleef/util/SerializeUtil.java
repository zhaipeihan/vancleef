package com.peihan.vancleef.util;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 序列化和反序列化的工具类
 * 使用kryo实现
 */
public class SerializeUtil {

    private static final Kryo kryo = new Kryo();

    public static byte[] serialize(Object object){
        Output output = new Output(4096,4096);
        kryo.writeClassAndObject(output,object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }

    public static Object deSerialize(byte[] bytes){
        Input input = new Input(bytes);
        Object object = kryo.readClassAndObject(input);
        input.close();
        return object;
    }
}
