package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;

public class NewInstanceData {

    public final String Clazz;
    public final String name;
    public final String description;

    public NewInstanceData(Class clazz) {
        ChatObjectName c = (ChatObjectName) clazz.getAnnotation(ChatObjectName.class);
        if (c == null || c.name().isEmpty())
            this.name = clazz.getSimpleName();
        else
            this.name = c.name();
        description = "TODO";
        this.Clazz = clazz.getName();
    }


    public NewInstanceData(String clazz, String name, String description) {
        Clazz = clazz;
        this.name = name;
        this.description = description;
    }
}
