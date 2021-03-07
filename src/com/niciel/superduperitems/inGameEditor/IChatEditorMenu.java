package com.niciel.superduperitems.inGameEditor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.UUID;

public abstract class IChatEditorMenu<T extends Object> extends  IChatEditor<T> {


    public final UUID uuid;
    private WeakReference<IBaseObjectEditor> owner;



    public IChatEditorMenu(IBaseObjectEditor owner ,String name, String description, Class clazz , Field field ) {
        super(name ,description ,clazz,field);
        this.uuid = UUID.randomUUID();
        this.owner = new WeakReference<>(owner);
    }


    public IBaseObjectEditor getTreeRoot() {
        return owner.get();
    }

    public abstract void onSelect() ;
    public abstract void onDeselect();
}
