package com.niciel.superduperitems.serialization;

import com.niciel.superduperitems.utils.Ref;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class ListSerializer implements IPersistentSerialize<List> {

    private PersistentSerializerManager mag;

    public ListSerializer(PersistentSerializerManager mag) {
        this.mag = mag;
    }

    @Override
    public byte[] serialize(List list) {
        int elements = list.size();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        byte[] name;
        byte[] serialized;
        for (int i = 0 ; i < list.size() ; i++) {
            buf.put(mag.serialize(list.get(i).getClass().getName()));
            buf.put(mag.serialize(list.get(i)));
        }
        return buf.array();
    }

    @Override
    public List deserialize(byte[] bytes, Ref<Integer> size) {
        size.setValue(4);
        int elements = mag.deserialize(int.class , bytes);
        bytes = Arrays.copyOfRange(bytes , 0 , 4);
        String name;
        for (int i = 0 ; i < elements ; i++) {
//            TODO
        }

        return null;
    }
}
