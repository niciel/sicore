package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.Ref;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.UUID;

public abstract class IChatEditorMenu<T extends Object> extends  IChatEditor<T> {


    private WeakReference<IBaseObjectEditor> owner;



    public IChatEditorMenu(IBaseObjectEditor owner ,String name, String description, Class clazz  ) {
        super(name ,description ,clazz);
        this.owner = new WeakReference<>(owner);
    }


    public IBaseObjectEditor getTreeRoot() {
        return owner.get();
    }

    public abstract void onSelect(Ref<T> ref) ;
    public abstract void onDeselect();
}
