package com.niciel.superduperitems.customitems;

import com.niciel.superduperitems.serialization.PersistentDataHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class ComponentDataPersistent<T> extends PersistentDataHelper<T> {


    public ComponentDataPersistent(Class<T> clazz, String key) {
        super(clazz, key);
    }




    public void T (ItemStack is , T data) {
        ItemMeta im = is.getItemMeta();
        set(im.getPersistentDataContainer() , data);
        is.setItemMeta(im);
    }


    public T get(ItemMeta im) {
        return get(im.getPersistentDataContainer());
    }

    public void set(ItemMeta im , T value) {
        set(im.getPersistentDataContainer() , value);
    }

}
