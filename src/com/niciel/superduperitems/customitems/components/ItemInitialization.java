package com.niciel.superduperitems.customitems.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.cfg.Cfg;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;
import com.niciel.superduperitems.customitems.ItemComponentScheme;
import com.niciel.superduperitems.customitems.event.EventCreateItem;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItemInitialization implements ItemComponent {

    private static ItemComponentScheme scheme = new ItemComponentScheme(ItemInitialization.class);


    @ChatEditable(name = "lore")
    @Cfg(path = "lore")
    public List<String> lore = Arrays.asList("kurwa to jest lista" , "LISTA");

    @ChatEditable(name = "name")
    @Cfg(path = "name")
    public String name = "name";


    @ChatEditable(name = "enchantments")
    public List<EnchantmentData> enchants ;


    @ChatEditable(name = "flags")
    public List<ItemFlag> itemFlags;


    @EventHandler
    public void onCreateItem(EventCreateItem e) {
        if (lore != null && ! lore.isEmpty()) {
            List list;
            if (e.itemMeta.hasLore())
                list = e.itemMeta.getLore();
            else
                list = new ArrayList();
            list.addAll(lore);
            e.itemMeta.setLore(list);
        }

        if (name != null && ! name.isEmpty())
            e.itemMeta.setDisplayName(name);
        if (enchants != null) {
            for (EnchantmentData d : enchants) {
                e.item.addEnchantment(d.enchantment , d.level);
            }
        }

        if (itemFlags != null) {
            ItemFlag array[] = new ItemFlag[itemFlags.size()];
            for (int i = 0 ; i < itemFlags.size() ; i++)
                array[i] = itemFlags.get(i);
            e.itemMeta.addItemFlags(array);
        }
    }



    @Override
    public void onEnable(CustomItem ci) {

    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("name" , name);
        JsonArray array = new JsonArray();
        for (String s : lore) {
            array.add(s);
        }
        o.add("lore" , array);

        array = new JsonArray();
        for (ItemFlag i : itemFlags)
            array.add(i.name());
        o.add("flags", array);
        return o;
    }

    @Override
    public void deserialize(JsonObject e) {
        name = e.get("name").getAsString();
        JsonArray array = e.get("lore").getAsJsonArray();
        lore = new ArrayList<>();
        array.forEach( c -> {
            lore.add(c.getAsString());
        });

        itemFlags = new ArrayList<>();
        array = e.get("flags").getAsJsonArray();
        array.forEach(c -> {
            itemFlags.add(ItemFlag.valueOf(c.getAsString()));
        });
    }


    public class EnchantmentData {

        @ChatEditable(name =  "level")
        public int level;
        @ChatEditable(name = "type")
        public Enchantment enchantment;


        @Override
        public String toString() {
            return "enchant " + enchantment + " level " + level;
        }
    }

}
