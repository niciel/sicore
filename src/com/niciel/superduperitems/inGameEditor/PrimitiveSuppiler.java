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
            construcotr = look.unreflectConstructor(clazz.getDeclaredConstructor(String.class , String.class , Class.class));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IChatEditor get(IBaseObjectEditor editor ,Class clazz) {
        String name;
        String description;
        if (clazz.isAnnotationPresent(ChatEditable.class)) {
            ChatEditable editable = (ChatEditable) clazz.getAnnotation(ChatEditable.class);
            name = editable.name();
            if (name.isEmpty())
                name = clazz.getSimpleName();
            description = editable.description();
        }
        else {
            name = clazz.getSimpleName();
            description = "?base?";
        }
        try {
            return (IChatEditor) construcotr.invoke(name , description , clazz);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
