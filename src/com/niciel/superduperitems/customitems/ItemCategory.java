package com.niciel.superduperitems.customitems;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.utils.itemstack.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemCategory implements GsonSerializable {


    @GsonSimpleSerialize
    @ChatEditable(name = "categoryName" , excludeInEdit = true)
    public String name = "nameOfCategory";

    @GsonSimpleSerialize
    @ChatEditable(name = "displayItem")
    public ItemStack represent = new ItemBuilder(Material.STONE_AXE).setName("test").addLoreLine("lore").get();

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject o) {

    }
}
