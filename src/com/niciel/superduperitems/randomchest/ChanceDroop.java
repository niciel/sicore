package com.niciel.superduperitems.randomchest;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class ChanceDroop implements IRandomBlock {

    private static Random random = new Random();


    @ChatEditable
    @GsonSimpleSerialize
    private ItemStack item;
    @ChatEditable
    @GsonSimpleSerialize
    private float chance;


    @Override
    public void generate( float luck, List<ItemStack> output) {
        if (chance >= random.nextFloat())
            output.add(item.clone());
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject o) {

    }
}
