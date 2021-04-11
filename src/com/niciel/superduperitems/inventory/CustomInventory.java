package com.niciel.superduperitems.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class CustomInventory<T> extends AInventoryHolder<T> {


    private Slot<T>[] slots;
    public Slot<T> defaultSlot;

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


    public void add(int slot , ItemStack is , Slot<T> s) {
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
    public void fillEmpty(Slot<T> si) {
        for (int i  = 0 ; i < slots.length ; i++) {
            if (slots[i] == null)
                slots[i] = si;
        }
    }

    /**
     *
     * @param slot
     * @param a beginning inslusive
     * @param b end inclusive
     */
    public void fill(Slot<T> slot , int a , int b) {
        fill(a,b, i -> set(i,slot));
    }

    public void fill(ItemStack is , int a, int b) {
        fill(a,b,i-> set(i,is));
    }

    private void fill(ItemStack is , Slot<T> s, int a,int b) {
        fill(a,b, i -> set(i,is,s));
    }

    private void fill(int a, int b,Consumer<Integer> id ) {
        if (a >=0 || b < this.slots.length) {
            for (int i = a ; i <= b ; i++) {
                id.accept(i);
            }
        }
    }

    public void set(int slot , ItemStack is , Slot<T> s) {
        getInventory().setItem(slot , is);
        slots[slot] = s;
    }

    public void set(int slot , Slot<T> s) {
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
                defaultSlot.onClick(getCData() ,e);
            return;
        }
        slots[i].onClick(getCData() ,e);
    }


    public void cancelEvent(InventoryClickEvent e) {
        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
    }



}
