package com.niciel.superduperitems.cfg;

import org.apache.commons.lang.ClassUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigApi {


    public static final String MARKER = "||";
    public static  HashMap<String , FieldSerializer> nameToSerializer = new HashMap<>();
    public static Map<String , ? > BukkitSerializationMap;

    static {
        try {
            Field f = ConfigurationSerialization.class.getDeclaredField("aliases");
            f.setAccessible(true);
            BukkitSerializationMap = (Map) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        register(List.class , new ListBoxSerialization());
        register(ArrayList.class , new ListBoxSerialization());

        register(Material.class , MaterialSerializer.createSerializer(Material.class , "material"));
        register(PotionEffectType.class , new PotionEffectTypeSerializer());
        register(PotionEffectTypeWrapper.class , new PotionEffectTypeSerializer());
    }


    /**
     *
     * @param o object to serialize
     * @param path path to section where will be serialized object if not exists create
     * @param section config file or ConfigurationSection
     * @return false if there is no Serializer for object
     */
    public static boolean serialize(Object o, String path , ConfigurationSection section ) {
        if (ClassUtils.wrapperToPrimitive(o.getClass()) != null) {
            section.set(path , o);
            return true;
        }
        if (o instanceof String) {
//            TODO
            section.set(path, o);
            return true;
        }
        if (o instanceof ConfigurationSerializable) {
            section.set(path , o);
        }
        if (BukkitSerializationMap.containsKey(o.getClass())) {
            section.set(path , o);
            return true;
        }

        if (o instanceof List) {
            List l = (List) o;
            if (! l.isEmpty() ) {
                Object toSerialize = l.get(0);
                if (toSerialize instanceof  String) {
                    section.set(path  , o);
                    return true;
                }
            }

        }
        FieldSerializer ser = getSerializer(o.getClass());
        if (ser == null)
            return false;

        ConfigurationSection newSection = section.createSection(path);
        newSection.set(MARKER , ser.type());
        ser.serialize(o , newSection);
        return true;
    }

    /**
     *
     * @param path path to section
     * @param section config file or ConfigurationSection if you wont to deserialize specific section from config
     * @return deserialized object
     */
    public static Object deserialize(String path ,ConfigurationSection section) {

        if (section.isConfigurationSection(path)) {
            ConfigurationSection innerSection = section.getConfigurationSection(path);
            if (!innerSection.contains(MARKER)) {
                if (innerSection.contains("==")) {
                    if (innerSection.isString("==")) {
                        return section.get(path);
                    }
                }
                return null;
            }
            if (!innerSection.isString(MARKER))
                return null;
            String type = innerSection.getString(MARKER);
            FieldSerializer ser = getSerializer(type);
            if (ser == null)
                return null;
            return ser.deserialize(innerSection);
        }
        return section.get(path);
    }

    public static FieldSerializer getSerializer(String name) {
        FieldSerializer ser = nameToSerializer.get(name);
        return ser;
    }


    public static FieldSerializer getSerializer(Class clazz) {
        FieldSerializer ser = nameToSerializer.get(clazz.getName());
        if (ser == null) {
            ser = new ObjectCfgSerializer(clazz, clazz.getName());
            register(clazz , ser);
        }
        return ser;
    }

    /**
     *
     * @param clazz type of serializable class
     * @param ser custom serializer
     */
    public static void register(Class clazz , FieldSerializer ser) {
        nameToSerializer.put(clazz.getName() , ser);
        nameToSerializer.put(ser.type() , ser);
    }


}
