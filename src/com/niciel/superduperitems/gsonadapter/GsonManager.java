package com.niciel.superduperitems.gsonadapter;

import com.google.gson.*;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.RefCallBack;
import com.sun.xml.internal.bind.v2.TODO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public final class GsonManager implements IManager {

    protected  HashMap<String , GsonSimpleSerializer> serializers = new HashMap<>();
    //private HashSet<String> gsonAdapters = new HashSet<>();
    protected  Gson gson;
    private  GsonBuilder builder;

    private static GsonManager instance;

    public final  String pathToClassName = "-=-type-=-";

    //protected  HashSet<String> registeredGsonProvider = new HashSet<>();


    public GsonManager(GsonBuilder builder) {
        this.builder = builder;
    }

    public void build(Consumer<GsonBuilder> cons) {
        if (cons != null)
            cons.accept(builder);
        gson = builder.create();
        builder = null;
    }


    public  Object fromJson(String o) {
        return fromJson(gson.fromJson( o , JsonObject.class));
    }


        /**
         * automatyczny system wczytania
         * @param o
         * @return
         */
    public  Object fromJson(JsonObject o) {
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
                return null;
            }
            return fromJson(o , clazz);
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


    public  JsonElement toJson(Object o) {
        String clazzName = o.getClass().getName();
        GsonSimpleSerializer ser = getSerializer(clazzName);
        if (ser != null) {
            return ser.serialize( o);
        }
        JsonElement e = gson.toJsonTree(o);
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
        }
    }

    public void registerSimpleSerializer(Class clazz , GsonSimpleSerializer ser) {
        serializers.put(clazz.getName() , ser);
    }


    @Deprecated
    public void registerSimpleSerializer(Class clazz) {
    }


}
