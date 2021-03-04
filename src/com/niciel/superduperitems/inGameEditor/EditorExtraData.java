package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.utils.Dual;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditorExtraData {

    private List<Dual<String , Object>> list;

    public EditorExtraData() {
        list = new ArrayList<>();
    }

    public void add(Object o) {
        add(o.getClass().getName() , o);
    }

    public void add(String s, Object o) {
        list.add(new Dual<>(s , o));
    }


    public boolean add(Type type, Object o) {
        if (type.clazzType.isAssignableFrom(o.getClass())) {
            list.add(new Dual<>(type.id , o));
            return true;
        }
        return false;
    }


    public <T> T get(Class<T> type) {
        for (Dual<String , Object> dual : list) {
            if (type.isAssignableFrom(dual.second.getClass()))
                return (T) dual.second;
        }
        return null;
    }


    public <T> List<T> getAll(Class<T> type) {
        List<T> array = new ArrayList<>();
        for (Dual<String , Object> dual : list) {
            if (type.isAssignableFrom(dual.second.getClass()))
                array.add((T) dual.second);
        }
        return array;
    }

    public Object get(Type t) {
        return get(t.id);
    }

    public Object get(String id) {
        for (Dual<String , Object> dual : list) {
            if (dual.first.contentEquals(id))
                return dual.second;
        }

        return null;
    }

    public <T> boolean ifExists(Class<T> clazz , Type t, Consumer<T> consumer) {
        if (t.clazzType.getName().contentEquals(clazz.getName())) {
            return ifExists(clazz , t.id , consumer);
        }
        SDIPlugin.instance.logWarning(this , "niepoprawny type");
        return false;
    }


        public <T> boolean ifExists(Class<T> clazz , String id , Consumer<T> consumer) {
        Object o = get(id);
        if (o == null)
            return false;
        if (clazz.isAssignableFrom(o.getClass())) {
            consumer.accept((T) o);
            return true;
        }
            return false;
    }


    public enum Type {

        FIELD("objectField" , Field.class),
        TYPE("objectType" , Class.class),
        NAME("objectName" , String.class);

        public final String id;
        public final Class clazzType;

        Type(String id, Class clazzType) {
            this.id = id;
            this.clazzType = clazzType;
        }
    }

}
