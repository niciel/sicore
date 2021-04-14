package com.niciel.superduperitems.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public abstract class AInventoryHolder implements IInventoryHolder{

    private Inventory inventory;
    private UUID uuid;

    public AInventoryHolder(int labels, String title) {
        this.inventory = Bukkit.createInventory(this, labels*9 , title);
        uuid = UUID.randomUUID();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }



}
