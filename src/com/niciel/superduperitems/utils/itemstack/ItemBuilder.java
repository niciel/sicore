package com.niciel.superduperitems.utils.itemstack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack is;
    private ItemMeta im;



    public ItemBuilder(ItemStack is) {
        this.is = is;
        this.im = is.getItemMeta();
    }


    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }


    public ItemBuilder setName(String name) {
        im.setDisplayName(name);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        List<String> list = im.getLore();
        if (list == null)
            list = new ArrayList<>();
        list.add(line);
        im.setLore(list);
        return this;
    }

    public ItemStack get() {
        is.setItemMeta(im);
        return is;
    }

}
