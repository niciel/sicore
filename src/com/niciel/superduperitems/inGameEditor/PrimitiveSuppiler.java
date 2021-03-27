package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class PrimitiveSuppiler implements IChatEditorSuppiler {

    private MethodHandle construcotr;

    public PrimitiveSuppiler(Class<? extends IChatEditor> clazz) {
        MethodHandles.Lookup look = MethodHandles.lookup();
        try {
            construcotr = look.unreflectConstructor(clazz.getDeclaredConstructor(String.class , String.class));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IChatEditor get(IBaseObjectEditor editor ,Class clazz,String name , String description) {
        System.out.println(" suppiled with name " + name);
        try {
            return (IChatEditor) construcotr.invoke(name , description );
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
