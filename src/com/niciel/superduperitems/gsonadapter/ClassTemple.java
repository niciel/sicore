package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.Ref;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ClassTemple implements GsonSimpleSerializer {

    private List<FieldTemple> fields;
    private Class type;
    private GsonManager manager;

    public ClassTemple(Class tyoe) {
        manager = IManager.getManager(GsonManager.class);
        this.type = tyoe;
        MethodHandles.Lookup look = MethodHandles.lookup();
        String path;
        FieldTemple temple;
        this.fields = new ArrayList<>();
        GsonSimpleSerialize annotation;
        for (Field f : type.getDeclaredFields()) {
            annotation = f.getDeclaredAnnotation(GsonSimpleSerialize.class);
            if (annotation == null) {
                continue;
            }
            path = annotation.name();
            if (path.isEmpty())
                path = f.getName();

            temple = new FieldTemple();
            temple.path = path;
            if (f.getType().isPrimitive() || f.getType().getSimpleName().contentEquals(String.class.getSimpleName())){
                temple.isPrimitive = true;
                temple.type = FieldTemple.PrimitiveWrapper.wrapper(f.getType().getName());
            }
            else
                temple.isPrimitive = false;
            if (! f.isAccessible())
                f.setAccessible(true);
            try {
                temple.getter = look.unreflectGetter(f);
                temple.setter = look.unreflectSetter(f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            processField(f, temple);
        }
    }


    protected void processField(Field field, FieldTemple temple) {
        if (canAddField(field)) {
            fields.add(temple);
        }
    }

    public boolean canAddField(Field f) {
        return true;
    }

    public JsonElement serializeObject(Object o) {
        Object in;
        JsonObject jo = new JsonObject();
        for (FieldTemple t : fields) {
            try {
                in = t.getter.invoke(o);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                continue;
            }
            if (in == null)
                jo.add(t.path , new JsonNull());
            else
                jo.add(t.path ,manager.toJson(in));
        }
        return jo;
    }

    public void deserializeObject(Object o , JsonElement object) {
        JsonObject obj = object.getAsJsonObject();
        Object in = null;
        JsonElement e ;
        Class type = null;
        for (FieldTemple t : fields) {
            if (! obj.has(t.path)) {
                in = null;
            }
            e = obj.get(t.path);
            if (e.isJsonNull()) {
                in = null;
            }
            if (e.isJsonObject()) {
                in = manager.fromJson(e.getAsJsonObject());
            }
            else if (t.isPrimitive) {
                in = t.type.deserializer.apply(e);
            }
            try {
                t.setter.invoke(o , in);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    @Override
    public JsonElement serialize(Object o) {
        return serializeObject(o);
    }

    @Override
    public void deserialize(Ref ref, JsonElement object) {
        Object o = ref.getValue();
        if (o== null)
        {
            try {
                o = type.newInstance();
                ref.setValue(o);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                ref.setValue(null);
            }
        }
        deserializeObject(o , object);
    }



    /*old
    private List<FieldTemple> fields;
    private Class type;



    public ClassTemple(Class tyoe) {
        this.type = tyoe;
        MethodHandles.Lookup look = MethodHandles.lookup();
        String path;
        FieldTemple temple;
        this.fields = new ArrayList<>();
        GsonSimpleSerialize annotation;
        for (Field f : type.getDeclaredFields()) {
            annotation = f.getDeclaredAnnotation(GsonSimpleSerialize.class);
            if (annotation == null) {
                continue;
            }
            path = annotation.name();
            if (path.isEmpty())
                path = f.getName();
            temple = new FieldTemple();
            temple.type = f.getType();
            temple.path = path;
            if (! f.isAccessible())
                f.setAccessible(true);
            try {
                temple.getter = look.unreflectGetter(f);
                temple.setter = look.unreflectSetter(f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            fields.add(temple);
        }
    }

    public JsonElement serializeObject(Object o) {
        Object in;
        JsonObject jo = new JsonObject();
        for (FieldTemple t : fields) {
            try {
                in = t.getter.invoke(o);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                continue;
            }
            if (in == null) {
                jo.add(t.path , new JsonNull());
            }
            else if (GsonSerializable.class.isAssignableFrom(in.getClass())){
                jo.add(t.path , GsonManager.toJsonTree( in));
            }
            else if (GsonManager.registeredGsonProvider.contains(in.getClass().getName())) {
                jo.add(t.path , GsonManager.toJsonTree(in));
            }
            else if (GsonManager.getSerializer(t.type) != null) {
                jo.add(t.path , GsonManager.getSerializer(t.type).serialize(in));
            }
            else {
                SDIPlugin.instance.logWarning(this, "nieznany typ klasy " + in.getClass().getSimpleName());
            }
        }
        return jo;
    }

    public void deserializeObject(Object o , JsonElement object) {
        JsonObject obj = object.getAsJsonObject();
        Object in;
        JsonElement e ;
        Class type = null;
        for (FieldTemple t : fields) {
            if (! obj.has(t.path)) {
                try {
                    t.setter.invoke(o , null);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                continue;
            }
            e = obj.get(t.path);
            if (e.isJsonNull()) {
                try {
                    t.setter.invoke(o , null);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                continue;
            }
            if (e.isJsonObject()) {
                if (e.getAsJsonObject().has("class")) {
                    String clazzname = e.getAsJsonObject().get("class").getAsString();
                    try {
                        type = Class.forName(clazzname);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
//                            TODO
                        continue;
                    }
                }
                else {
                    type = t.type;
                }
                in = GsonManager.fromJson(e , type);
            }
            else if (GsonManager.registeredGsonProvider.contains(t.type.getName())) {
                in = GsonManager.fromJson(e , t.type);
            }
            else if (GsonManager.serializers.containsKey(t.type.getName())) {
                GsonManager.Serializer ser = GsonManager.getSerializer(t.type);
                if (ser == null) {
                    SDIPlugin.instance.logWarning(this , "obiekt + " + t.type.getSimpleName() + "nie mogl zostac zdeserializowanym brak deserializera 999! path " + t.path + " e " + e);
                    continue;
                }
                in = ser.deserialize(e);
            }
            else {
                SDIPlugin.instance.logWarning(this , "obiekt + " + t.type.getSimpleName() + "nie mogl zostac zdeserializowanym brak deserializera 998! path " + t.path + " e " + e);
                continue;
            }
            try {
                t.setter.invoke(o , in);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    @Override
    public JsonElement serialize(Object o) {
        return serializeObject(o);
    }

    @Override
    public Object deserialize(JsonElement object) {
        Object o =null;
        try {
            o = type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        deserializeObject(o , object);
        return o;
    }

    private class FieldTemple {

        Class type;
        String path;
        MethodHandle getter;
        MethodHandle setter;
    }

     */
}

