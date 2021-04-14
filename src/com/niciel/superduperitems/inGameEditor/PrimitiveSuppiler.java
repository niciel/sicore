package com.niciel.superduperitems.inGameEditor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class PrimitiveSuppiler implements IChatEditorSuppiler {

    private MethodHandle construcotr;

    public PrimitiveSuppiler(Class<? extends ChatEditor> clazz) {
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
    public ChatEditor get(IBaseObjectEditor editor , Class clazz, String name , String description) {
        try {
            return (ChatEditor) construcotr.invoke(name , description );
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
