package com.niciel.superduperitems.customitems;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;

public class ItemComponentBox implements Serializable {

    public ItemComponent component;
    public MethodHandle handle;

    public ItemComponentBox( ItemComponent ic , MethodHandle handle) {
        this.handle = handle;
        component = ic;
    }
}
