package com.niciel.superduperitems.randomchest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RandomList implements IRandomBlock {


    @ChatEditable
    private List<IRandomBlock> randoms;


    @Override
    public void generate(float luck, List<ItemStack> output) {
        randoms.forEach( a-> a.generate(luck , output)) ;
    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        JsonArray array = new JsonArray();
        for (IRandomBlock r : randoms)
            array.add(GsonManager.getInstance().toJson(r));
        o.add("list" , array);
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        JsonArray a = o.get("list").getAsJsonArray();
        this.randoms = new ArrayList<>();
        a.forEach( c -> this.randoms.add( (IRandomBlock) GsonManager.getInstance().fromJson(c.getAsJsonObject())));
    }
}
