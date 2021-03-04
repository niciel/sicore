package com.niciel.superduperitems.customitems.components;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeldItemChange implements ItemComponent {





    public void accept(ItemStack is , Player p) {

    }


    @Override
    public void onEnable(CustomItem ci) {

    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject e) {

    }
}
