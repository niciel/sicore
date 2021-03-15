package com.niciel.superduperitems.persistentHelper;

import com.google.common.primitives.Ints;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.Ref;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class PersistentSerializerManager implements IManager {

    public  HashMap<String , IPersistentSerialize> classNameToSerializer;

    public PersistentSerializerManager() {
        classNameToSerializer = new HashMap<>();

        IPersistentSerialize si = new IPersistentSerialize() {
            @Override
            public byte[] serialize(Object o) {
                return Ints.toByteArray((int) o);
            }

            @Override
            public Object deserialize(byte[] bytes, Ref size) {
                size.setValue(4);
                return Ints.fromByteArray(bytes);
            }


        };
        addSerializer(si , int.class);
        addSerializer(si , Integer.class);

        si = new IPersistentSerialize<String>() {

            @Override
            public byte[] serialize(String o) {
                byte[] array;
                array = o.getBytes(StandardCharsets.UTF_16);
                byte[] ret = new byte[array.length + 4];
                byte[] in = Ints.toByteArray(array.length);
                for (int i = 0 ; i < 4 ; i++)
                    ret[i] = in[i];
                int pos = 0;
                for (int i = 4 ; i < ret.length ; i++) {
                    ret[i] = array[pos];
                    pos++;
                }
                return ret;
            }

            @Override
            public String deserialize(byte[] bytes, Ref<Integer> size) {
                byte[] sz = Arrays.copyOfRange(bytes , 0 , 4 );
                size.setValue(Ints.fromByteArray(sz) + 4);
                sz = Arrays.copyOfRange(bytes , 4 , size.getValue());
                return new String(sz , StandardCharsets.UTF_16);
            }
        };
        addSerializer(si , String.class);

        si = new IPersistentSerialize<Double>() {

            @Override
            public byte[] serialize(Double o) {
                byte[] ret = new byte[8];
                ByteBuffer.wrap(ret).putDouble(o);
                return  ret;
            }

            @Override
            public Double deserialize(byte[] bytes, Ref<Integer> size) {
                size.setValue(8);
                return ByteBuffer.wrap(Arrays.copyOfRange(bytes , 0 , 8)).getDouble();
            }
        };
        addSerializer(si , Double.class);
        addSerializer(si , double.class);

        si = new IPersistentSerialize<Float>() {

            @Override
            public byte[] serialize(Float o) {
                byte[] ret = new byte[4];
                ByteBuffer.wrap(ret).putFloat(o);
                return  ret;
            }

            @Override
            public Float deserialize(byte[] bytes, Ref<Integer> size) {
                size.setValue(8);
                return ByteBuffer.wrap(Arrays.copyOfRange(bytes , 0 , 4)).getFloat();
            }
        };
        addSerializer(si , Float.class);
        addSerializer(si , float.class);

    }

    public byte[] serialize(Object o) {
        IPersistentSerialize s = classNameToSerializer.get(o.getClass().getName());
        if (s == null) {
            return null;
        }
        return s.serialize(o);
    }

    public <T> T deserialize(Class<T> clazz, byte[] array) {
        IPersistentSerialize ser = getSerializer(clazz);
        Ref<Integer> ref = new Ref<>();
        return (T) ser.deserialize(array , ref);
    }

    public <T> IPersistentSerialize<T> getSerializer(Class<T> clazz) {
        IPersistentSerialize s = classNameToSerializer.get(clazz.getName());
        return s;
    }

    public void addSerializer(IPersistentSerialize serializer , Class clazz) {
        classNameToSerializer.put(clazz.getName() , serializer);
    }



}
