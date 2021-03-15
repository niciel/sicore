package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.Ref;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public abstract class IChatEditor<T extends Object> {


    private String name;
    private String description;
    private Ref<T> reference;

    /**
     *
     * @param name
     * @param description
     * @param clazz class of object or if field ist null field type
     */
    public IChatEditor(String name, String description, Class clazz ) {
        this.name = name;
        this.description = description;
    }

    public abstract void enableEditor(IChatEditorMenu owner);
    public abstract void disableEditor();


    public void initialize(Ref<T> reference) {
        if (this.reference == null)
            this.reference = reference;
    }

    public Ref<T> getReference() {
        return this.reference;
    }

    public abstract void sendItem(Player p);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
