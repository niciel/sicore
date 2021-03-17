package com.niciel.superduperitems.randomchest;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class RandomItem implements GsonSerializable , IRandomBlock{

    private static Random random = new Random();

    @ChatEditable
    @GsonSimpleSerialize
    private String customItem;

    @ChatEditable
    @GsonSimpleSerialize
    private ItemStack item;
    @ChatEditable
    @GsonSimpleSerialize
    private int min;
    @ChatEditable
    @GsonSimpleSerialize
    private  int max;


    @Override
    public void generate(float luck, List<ItemStack> output) {
        ItemStack is = null;
        if (customItem != null || ! customItem.isEmpty()) {
            CustomItem item = SDIPlugin.instance.getManager(ItemManager.class).getCustomItem(customItem);
            is = item.createItem(1);
        }
        else if (item != null) {
            is = item.clone();
        }
        if (is == null)
            return;
        fixCount(is);
        output.add(is);
    }


    public void fixCount(ItemStack is) {
        if (min == max) {
            is.setAmount(min);
        }
        else {
            is.setAmount(min + random.nextInt((max-min)));
        }
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject o) {

    }
}
