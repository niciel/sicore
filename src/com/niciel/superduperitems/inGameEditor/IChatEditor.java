package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.Ref;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;

public abstract class IChatEditor<T extends Object> {


    private String name;
    private String description;
    private Class baseType;

    public IChatEditor(String name, String description, Class baseType) {
        this.name = name;
        this.description = description;
        this.baseType = baseType;
    }

    public abstract void enableEditor(IChatEditorMenu owner , Ref<T> ref);
    public abstract void disableEditor(IChatEditorMenu owner);

    public abstract void sendItem(Player p);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Class getBaseType() {
        return baseType;
    }
}
