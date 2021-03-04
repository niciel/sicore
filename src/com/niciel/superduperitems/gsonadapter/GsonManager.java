package com.niciel.superduperitems.gsonadapter;

import com.google.gson.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public final class GsonManager {

    protected static HashMap<String , Serializer<?>> serializers = new HashMap<>();
    protected static HashMap<String , ClassTemple> objects = new HashMap<>();
    protected static Gson gson;
    private static GsonBuilder builder;

    protected   static HashSet<String> registeredGsonProvider = new HashSet<>();


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

    public void registerTypeAdapter(Class clazz , Object serializer) {
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

        public JsonElement serialize(T o);
        public T deserialize(JsonElement object);

    }

}
