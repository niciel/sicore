package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.Ref;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.UUID;

public abstract class IChatEditorMenu<T extends Object> extends  IChatEditor<T> {


    private WeakReference<IBaseObjectEditor> owner;


    public IChatEditorMenu(IBaseObjectEditor owner ,String name, String description) {
        super(name ,description );
        this.owner = new WeakReference<>(owner);
    }

    public IBaseObjectEditor getTreeRoot() {
        return owner.get();
    }

    public abstract void onSelect(IChatEditorMenu menu) ;
    public abstract void onDeselect();
}
