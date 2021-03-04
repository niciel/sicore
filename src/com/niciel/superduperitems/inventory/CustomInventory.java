package com.niciel.superduperitems.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class CustomInventory extends AInventoryHolder {


    private Slot[] slots;
    public Slot defaultSlot;


    public final boolean onlyCustomInventory;

    public CustomInventory(int labels, String title) {
        super(labels, title);
        slots = new Slot[labels*9];
        onlyCustomInventory = false;
    }

    public CustomInventory(int labels, String title , boolean onlyCustomInventory) {
        super(labels, title);
        slots = new Slot[labels*9];
        this.onlyCustomInventory = onlyCustomInventory;
    }


    public void add(int slot , ItemStack is , Slot s) {
        if (is != null)
            getInventory().setItem(slot , is);
        if (s != null)
            slots[slot] = s;
    }

    public void fillEmpty(ItemStack is) {
        for (int i  = 0 ; i < slots.length ; i++) {
            if (getInventory().getItem(i) == null)
                getInventory().setItem(i , is);
        }
    }
    public void fillEmpty(Slot si) {
        for (int i  = 0 ; i < slots.length ; i++) {
            if (slots[i] == null)
                slots[i] = si;
        }
    }

    public void set(int slot , ItemStack is , Slot s) {
        getInventory().setItem(slot , is);
        slots[slot] = s;
    }

    public void set(int slot , Slot s) {
        slots[slot] = s;
    }

    public void set(int slot , ItemStack is) {
        getInventory().setItem(slot , is);
    }

    public void onClose(Player p ) {

    }

    public void onOpen(Player p) {

    }

    public int getFirstEmptyItemSlot() {
        for (int i = 0 ; i < getInventory().getContents().length ; i++) {
            if (getInventory().getItem(i) == null)
                return i;
        }
        return -1;
    }

    public int getFirstEmptySlot() {
        for (int i = 0 ; i < slots.length ; i++) {
            if (slots[i] == null)
                return i;
        }
        return -1;
    }


    public void onDrag(InventoryDragEvent e) {
        if (isHolderInventory(e.getInventory())) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }

    }

    public void onClick(InventoryClickEvent e ) {
        if (onlyCustomInventory) {
            if (! isHolderInventory(e.getClickedInventory())) {
                cancelEvent(e);
                return;
            }
        }
        int i = e.getSlot();
        if (i < 0 || i >= slots.length) {
            cancelEvent(e);
            return;
        }
        if (slots[i] == null) {
            if (defaultSlot != null)
                defaultSlot.onClick(e);
            return;
        }
        slots[i].onClick(e);
        if (! e.isCancelled()) {

        }
    }


    public void cancelEvent(InventoryClickEvent e) {
        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
    }



}
