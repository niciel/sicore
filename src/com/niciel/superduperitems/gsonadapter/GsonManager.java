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
    private HashSet<String> gsonAdapters = new HashSet<>();
    protected  Gson gson;
    private  GsonBuilder builder;

    private static GsonManager instance;

    public final  String pathToClassName = "-=-type-=-";

    protected  HashSet<String> registeredGsonProvider = new HashSet<>();


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

    public void registerSimpleSerializer(Class clazz) {
//        serializers.put(clazz.getName() , TODO);
    }


    /* old
    protected static HashMap<String , Serializer<?>> serializers = new HashMap<>();
    protected static HashMap<String , ClassTemple> objects = new HashMap<>();

    protected static Gson gson;
    private static GsonBuilder builder;

    protected static HashSet<String> registeredGsonProvider = new HashSet<>();


    public GsonManager(GsonBuilder builder) {
        this.builder = builder;
    }

    public void build(Consumer<GsonBuilder> cons) {
        if (cons != null)
            cons.accept(builder);
        gson = builder.create();
        builder = null;
    }

    public static <T> T fromJson(JsonObject o ) {
        if (o.has("class")) {
            return (T) fromJson(o , GsonSerializable.class);
        }
        return null;
    }

    public static <T> T fromJson(JsonElement e , Class<T> type) {
        if (GsonSerializable.class.isAssignableFrom(type))
            return (T) gson.fromJson(e  , GsonSerializable.class);
        else
            return gson.fromJson(e , type);
    }

    public static <T> T fromJson(String e , Class<T> type) {
        if (GsonSerializable.class.isAssignableFrom(type))
            return (T) gson.fromJson(e  , GsonSerializable.class);
        else
            return gson.fromJson(e , type);
    }


    public static ClassTemple getTemple(Class clazz) {
        ClassTemple temple = objects.get(clazz.getName());
        if (temple != null) {
            return temple;
        }
        temple = new ClassTemple(clazz);
        objects.put(clazz.getName() , temple);
        return temple;
    }

    public static JsonElement serializeObject(Object o) {
        ClassTemple t = getTemple(o.getClass());
        JsonElement ret = t.serializeObject(o);
        return ret;
    }

    public static void deserializeObject(Object o,  JsonObject gson) {
        ClassTemple t = getTemple(o.getClass());
        t.deserializeObject(o , gson);
    }

    public  static JsonElement toJsonTree(Object ser , Class type) {
        if (GsonSerializable.class.isAssignableFrom(type))
            return gson.toJsonTree(ser , GsonSerializable.class);
        return gson.toJsonTree(ser , type);
    }

    public  static JsonElement toJsonTree(Object ser) {
        if (GsonSerializable.class.isAssignableFrom(ser.getClass()))
            return gson.toJsonTree(ser , GsonSerializable.class);
        else
            return gson.toJsonTree(ser);
    }

    public static  String toJson(JsonElement e) {
        return gson.toJson(e);
    }

    public static  String toJson(Object o) {
        return toJsonTree(o).toString();
    }

    public void registerTypeAdapter(Class clazz , JsonSerializer serializer) {
        registerTypeAdapterUnsafe(clazz,serializer);
    }

    private void registerTypeAdapterUnsafe(Class clazz,Object serializer) {
        if (builder == null)
            return;
        registeredGsonProvider.add(clazz.getName());
        builder.registerTypeAdapter(clazz , serializer);
    }


    static {
        Serializer<Integer> i = new Serializer<Integer>() {
            @Override
            public JsonElement serialize(Integer o) {
                return new JsonPrimitive(o);
            }
            @Override
            public Integer deserialize(JsonElement object) {
                return object.getAsInt();
            }
        };
        serializers.put(Integer.class.getName() ,  i);
        serializers.put(int.class.getName() ,  i);

        Serializer<String> s = new Serializer<String>() {
            @Override
            public JsonElement serialize(String o) {
                return new JsonPrimitive(o);
            }

            @Override
            public String deserialize(JsonElement object) {
                return object.getAsString();
            }
        };
        serializers.put(String.class.getName() , s);

        Serializer<Double> d = new Serializer<Double>() {

            @Override
            public JsonElement serialize(Double o) {
                return new JsonPrimitive(o);
            }

            @Override
            public Double deserialize(JsonElement object) {
                return object.getAsDouble();
            }
        };
        serializers.put(Double.class.getName() , d);
        serializers.put(double.class.getName() , d);

        Serializer<Float> f = new Serializer<Float>() {
            @Override
            public JsonElement serialize(Float o) {
                return new JsonPrimitive(o);
            }

            @Override
            public Float deserialize(JsonElement object) {
                return object.getAsFloat();
            }

        };
        serializers.put(float.class.getName() , f);
        serializers.put(Float.class.getName() , f);

        Serializer<Long> l = new Serializer<Long>() {
            @Override
            public JsonElement serialize(Long o) {
                return new JsonPrimitive(o);
            }
            @Override
            public Long deserialize(JsonElement object) {
                return object.getAsLong();
            }

        };
        serializers.put(long.class.getName() ,l);
        serializers.put(Long.class.getName() , l);

    }



    public static <Y extends GsonSerializable> Serializer getSerializer(Class<Y> clazz) {
        Serializer ser = serializers.get(clazz.getName());
        if (ser != null) {
            return ser;
        }
//        TODO twoerzenie serializera !
        return null;
    }

    public interface Serializer<T> {

        JsonElement serialize(T o);
        T deserialize(JsonElement object);

    }

     */
}
