package com.niciel.superduperitems.gsonadapter;

import com.google.gson.*;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.RefCallBack;
import com.sun.xml.internal.bind.v2.TODO;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.function.Consumer;

public final class GsonManager implements IManager {

    protected  HashMap<String , GsonSimpleSerializer> serializers = new HashMap<>();
    private HashSet<String> gsonAdapters = new HashSet<>();
    private  Gson gson;
    private  GsonBuilder builder;

    private static GsonManager instance;

    public static final  String pathToClassName = "-=-type-=-";



    public GsonManager(GsonBuilder builder) {
        this.builder = builder;
    }

    public void build(Consumer<GsonBuilder> cons) {
        if (cons != null)
            cons.accept(builder);
        builder.disableHtmlEscaping();
        gson = builder.create();
        builder = null;
    }


    public  Object fromJson(String o) {
        return fromJson(gson.fromJson( o , JsonObject.class));
    }

    public Object fromFile(File f) {
        try {
            Scanner scaner = new Scanner(f);
            StringBuilder sb = new StringBuilder();
            while (scaner.hasNext())
                sb.append(scaner.nextLine());
            return fromJson(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * automatyczny system wczytania
     * @param o
     * @return
     */
    public  Object fromJson(JsonObject o) {
        if (o == null)
            return null;
        if (o.has(pathToClassName)) {
            String clazzName = o.get(pathToClassName).getAsString();
            GsonSimpleSerializer ser = getSerializer(clazzName);
            if (ser != null) {
                RefCallBack ref = new RefCallBack();
                 ser.deserialize(ref , o);
                 return ref.getValue();
            }
            Class clazz ;
            try {
                clazz = Class.forName(clazzName);
                if (GsonSerializable.class.isAssignableFrom(clazz)) {
                    return loadFromGsonSerializable(clazz,o);
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                SDIPlugin.instance.logWarning(this,"class file not found: " + clazzName);
                return null;
            }
            if (gsonAdapters.contains(clazzName))
                return fromJson(o , clazz);
            else {
                ser = getSerializer(clazz);
                RefCallBack ref = new RefCallBack();
                ser.deserialize(ref , o);
                return ref.getValue();
            }
        }
        return null;
    }

    public static GsonManager getInstance() {
        if (instance == null)
            instance = IManager.getManager(GsonManager.class);
        return instance;
    }

    private Object loadFromGsonSerializable(Class c , JsonObject o) throws IllegalAccessException, InstantiationException {
        Object t = c.newInstance();
        ((GsonSerializable)t).deserialize(o);
        return t;
    }



    public GsonSimpleSerializer getSerializer(String clazzName) {
        return serializers.get(clazzName);
    }

    public GsonSimpleSerializer getSerializer(Class clazz) {
        String clazzName = clazz.getName();
        if (serializers.containsKey(clazzName))
            return getSerializer(clazzName);
        ClassTemple t = new ClassTemple(clazz);
        serializers.put(clazzName , t);
        return t;
    }



    /**
     * wczytuje ze zwyklego gson'a
     * @param e
     * @param type
     * @param <T>
     * @return
     */
    public  <T> T fromJson(JsonElement e , Class<T> type) {
        return gson.fromJson(e , type);
    }


    public void toFile(File f ,Object o) {
        try {
            Writer writer = new FileWriter(f);
            writer.write(gson.toJson(o).toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  JsonElement toJson(Object o) {
        String clazzName = o.getClass().getName();
        JsonElement e = null;
        if (gsonAdapters.contains(clazzName)){
            try {
                Class clazz = Class.forName(clazzName);
                e = gson.toJsonTree(o , clazz);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        else {
            GsonSimpleSerializer ser = getSerializer(o.getClass());
            if (ser != null) {
                e = ser.serialize(o);
            }
            else
                return null;
        }
        if (e.isJsonObject()) {
            e.getAsJsonObject().addProperty(pathToClassName,clazzName);
        }
        return e;
    }

    /**
     *
     * @param clazz
     * @param object tylko {@link JsonSerializer} lub {@link JsonDeserializer}
     */
    public void registerTypeAdapter(Class clazz, Object object) {
        if (gson != null)
            return;
        if (object instanceof JsonSerializer || object instanceof JsonDeserializer) {
            builder.registerTypeAdapter(clazz,object);
            gsonAdapters.add(clazz.getName());
        }
    }

    public void registerSimpleSerializer(Class clazz , GsonSimpleSerializer ser) {
        serializers.put(clazz.getName() , ser);
    }

    public Gson getGson() {
        return gson;
    }




}
