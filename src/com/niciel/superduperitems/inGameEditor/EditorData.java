package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.Ref;

import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;

public class EditorData {


    public MethodHandle getter;
    public MethodHandle setter;
    public IChatEditor editor;

    public final WeakReference<Object> owner;

    public EditorData(Object owner) {
        this.owner = new WeakReference<>(owner);
    }

    public void set( Object o) {
        try {
            setter.invoke(owner.get() , o);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    public Object get() {
        try {
            Object o = getter.invoke(owner.get());
            return o;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

}