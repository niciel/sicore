package com.niciel.superduperitems.persistentHelper;

import com.niciel.superduperitems.utils.Dual;
import com.niciel.superduperitems.utils.Ref;
import org.apache.commons.lang.ArrayUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ObjectSerializer<T> implements IPersistentSerialize<T> {


    private ArrayList<Data> fieldToSerializer;

    private Class clazz;

    public ObjectSerializer(Class<T> clazz , PersistentSerializerManager mag) {
        this.clazz = clazz;
        PersistentSerializerManager manager =
                mag;

        fieldToSerializer = new ArrayList();
        Dual p;
        PersistentData data;
        IPersistentSerialize ser;
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (Field f : clazz.getDeclaredFields()) {
            data = f.getAnnotation(PersistentData.class);
            if (data == null)
                continue;
            ser = manager.getSerializer(f.getType());
            if (ser == null) {
                System.out.println("nullasek : " + fieldToSerializer.size());
                return;
//                TODO
            }
            if (f.isAccessible() == false)
                f.setAccessible(true);
            try {
                fieldToSerializer.add(new Data(lookup.unreflectSetter(f) , lookup.unreflectGetter(f) , ser));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
//                TODO
                return;
            }
        }
    }


    public Class<T> getSerializedClass() {
        return clazz;
    }


    @Override
    public byte[] serialize(T t) {
        ArrayList<Byte> list = new ArrayList();
        Object o;
        for (Data p : fieldToSerializer) {
            o = p.get(t);
            if (o == null)
                return null;
            Collections.addAll(list , ArrayUtils.toObject(p.serializer.serialize(o)));
        }
        Byte[] out = new Byte[list.size()];
        out = list.toArray(out);
        return ArrayUtils.toPrimitive(out);
    }


    @Override
    public T deserialize(byte[] bytes , Ref<Integer> ref) {
        Object instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length).put(bytes);
        buffer.rewind();
        Object field;
        int bottom = 0;
        byte[] array;
        Ref<Integer> size = new Ref<Integer>();
        for (Data d : fieldToSerializer) {
            array = Arrays.copyOfRange(bytes , bottom , bytes.length);
            size = new Ref<>();
            field = d.serializer.deserialize(array ,size);
            bottom += size.getValue();
            d.set(instance , field);
        }
        ref.setValue(bottom);
        return (T) instance;
    }


    private class Data {
        MethodHandle setter;
        MethodHandle getter;
        IPersistentSerialize serializer;

        public Data(MethodHandle setter, MethodHandle getter, IPersistentSerialize serializer) {
            this.setter = setter;
            this.getter = getter;
            this.serializer = serializer;
        }

        public Object get(Object o) {
            try {
                return getter.invoke(o);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        public void set(Object obj, Object toAdd) {
            try {
                setter.invoke(obj , toAdd);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
