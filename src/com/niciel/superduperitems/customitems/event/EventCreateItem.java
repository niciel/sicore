package com.niciel.superduperitems.customitems.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCreateItem extends Event {

    private static final HandlerList handler = new HandlerList();

    public EventCreateItem(ItemStack is , ItemMeta im) {
        this.item = is;
        this.itemMeta = im;
    }

    public ItemStack item;
    public ItemMeta itemMeta;


    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList () {
        return handler;
    }


}
