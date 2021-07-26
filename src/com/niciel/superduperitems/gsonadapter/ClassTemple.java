package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
        this.fields = new ArrayList<>();
        while (tyoe.getName().contentEquals(Object.class.getName()) == false) {
            ulomnageneracja(tyoe);
            tyoe = tyoe.getSuperclass();
        }
    }


    protected void ulomnageneracja(Class clazz) {
        MethodHandles.Lookup look = MethodHandles.lookup();
        String path;
        FieldTemple temple;
        GsonSimpleSerialize annotation;
        for (Field f : clazz.getDeclaredFields()) {
            annotation = f.getDeclaredAnnotation(GsonSimpleSerialize.class);
            if (annotation == null) {
                continue;
            }
            if (! f.isAccessible())
                f.setAccessible(true);
            path = annotation.name();
            if (path.isEmpty())
                path = f.getName();
            temple = new FieldTemple();
            temple.path = path;
            if (f.getType().isPrimitive() || f.getType().getSimpleName().contentEquals(String.class.getSimpleName())){
                temple.isPrimitive = true;
                temple.type = FieldTemple.PrimitiveWrapper.wrapper(f.getType().getName());
            }
            else {
                temple.isPrimitive = false;
            }
            temple.fieldType = f.getType();
            if (f.getType().isEnum())
                temple.enumType = f.getType();

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
            else if (t.isPrimitive) {
                jo.add(t.path ,t.type.serializer.apply(in));
            }
            else if (t.enumType != null) {
                jo.addProperty(t.path,in.toString());
            }
            else
                jo.add(t.path ,manager.toJson(in));
        }
        jo.addProperty(GsonManager.pathToClassName , o.getClass().getName());
        return jo;
    }

    public void deserializeObject(Object o , JsonElement object) {
        JsonObject obj = object.getAsJsonObject();
        Object in = null;
        JsonElement e ;
        for (FieldTemple t : fields) {
            if (! obj.has(t.path)) {
                if (t.isPrimitive)
                    in = t.type.defaultPrimitive;
                else
                    in = null;
            }
            else {
                e = obj.get(t.path);
                if (e == null || e.isJsonNull()) {
                    in = null;
                }
                else if (t.enumType != null) {
                    in = Enum.valueOf(t.enumType,e.getAsString());
                }
                else if (e.isJsonObject()) {
                    in = manager.fromJson(e.getAsJsonObject());
                }
                else if (t.isPrimitive) {
                    in = t.type.deserializer.apply(e);
                }
            }


            try {
                t.setter.invoke(o , in);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (o instanceof IAfterGsonDeserialize)
            ((IAfterGsonDeserialize) o).afterGsonSerialization(obj);
    }

    @Override
    public JsonElement serialize(Object o) {
        return serializeObject(o);
    }

    @Override
    public void deserialize(Ref ref, JsonElement object) {
        Object o = ref.getValue();
        if (o== null && type.isEnum() == false)
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

}

