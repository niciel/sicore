package com.niciel.superduperitems.cfg;

import com.mysql.fabric.xmlrpc.base.Param;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class MaterialSerializer<T> implements FieldSerializer<T> {


    public Class type;
    public MethodHandle valueOf;
    public String nameType;

    @Override
    public void serialize(T o, ConfigurationSection section) {
        section.set("type" , o.toString());
    }

    @Override
    public T deserialize(ConfigurationSection section) {
        if (section.contains("type")) {
            if (section.isString("type")) {
                try {
                    return (T) valueOf.invoke(section.getString("type"));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }


    @Override
    public String type() {
        return nameType;
    }


    public static MaterialSerializer createSerializer(Class enumClass , String nameType) {
        if (! enumClass.isEnum())
            return null;
        MaterialSerializer s = new MaterialSerializer();
        s.type = enumClass;
        s.nameType = nameType;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            Method m = enumClass.getDeclaredMethod("valueOf", String.class);
            s.valueOf = lookup.unreflect(m);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return s;
    }

}
