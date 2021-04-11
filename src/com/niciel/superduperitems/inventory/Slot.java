package com.niciel.superduperitems.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface Slot<T> {


    void onClick(T dat , InventoryClickEvent e);

}
