package com.niciel.superduperitems.cfg;

import org.bukkit.configuration.ConfigurationSection;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ObjectCfgSerializer implements FieldSerializer , SerializationCallBack {


    private String type;
    private Class clazz;
    private List<Data> serialization = new ArrayList<>();

    private boolean isCallbackEnable;

    public ObjectCfgSerializer(Class clazz , String type) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        isCallbackEnable = SerializationCallBack.class.isAssignableFrom(clazz);
        Cfg cfg ;
        Data d ;
        this.clazz = clazz;
        this.type = type;
        for (Field f : clazz.getDeclaredFields()) {
            cfg = f.getAnnotation(Cfg.class);
            if (cfg == null)
                continue;
            if (! f.isAccessible())
                f.setAccessible(true);
            d = new Data();
            try {
                d.getter = lookup.unreflectGetter(f);
                d.setter = lookup.unreflectSetter(f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            d.path = cfg.path();

            serialization.add(d);
        }
    }

    @Override
    public void serialize(Object o, ConfigurationSection section) {
        Object fieldObject = null;
        for (Data d : serialization) {
            try {
                fieldObject = d.getter.invoke(o);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (fieldObject == null)
                continue;
            ConfigApi.serialize(fieldObject , d.path , section);
        }
    }

    @Override
    public Object deserialize(ConfigurationSection section) {

        Object o = null;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Data d : serialization) {
            try {
                d.setter.invoke(o , ConfigApi.deserialize(d.path , section));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (isCallbackEnable)
            ((SerializationCallBack) o).onSerializeEnd();
        return o;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public void onSerializeEnd() {

    }

    private class Data {
        MethodHandle getter;
        MethodHandle setter;
        String path;
    }

}
