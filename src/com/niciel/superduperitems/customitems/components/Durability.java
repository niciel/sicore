package com.niciel.superduperitems.customitems.components;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.cfg.Cfg;
import com.niciel.superduperitems.customitems.ComponentDataPersistent;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;
import com.niciel.superduperitems.customitems.event.EventCreateItem;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.persistentHelper.PersistentData;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Durability implements ItemComponent {

    public static final ComponentDataPersistent<Durability> namespaceKey = new ComponentDataPersistent<Durability>(Durability.class , "durability");

    @ChatEditable(name = "durability")
    @PersistentData
    @Cfg(path = "durability")
    public double durability = 1;


    @ChatEditable(name = "maxDurability")
    @PersistentData
    @Cfg(path = "maxdurability")
    public int maxDurability = 10;


    public Durability getDurability(ItemMeta im ) {
        return namespaceKey.get(im);
    }


    public void setDurability(ItemStack is ,  ItemMeta im , Durability data) {
        if (im instanceof Damageable) {
            int damage = (int) (data.maxDurability-data.durability);
            double percent = (double) damage/data.maxDurability;
            int newDurability = (short) (percent*is.getType().getMaxDurability());
            ((Damageable) im).setDamage(newDurability);
        }
        namespaceKey.set(im , data);
        is.setItemMeta(im);
        Durability d = getDurability(im);
    }


    @EventHandler
    public void onItemCreate(EventCreateItem e) {
        setDurability(e.item , e.itemMeta , this);
    }


    @Override
    public void onEnable(CustomItem ci) {

    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("maxDurability" , maxDurability);
        o.addProperty("durability" , durability);
        return o;
    }

    @Override
    public void deserialize(JsonObject e) {
        maxDurability = e.get("maxDurability" ).getAsInt();
        durability = e.get("durability").getAsDouble();
    }

}
