package com.niciel.superduperitems.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuInventory extends AInventoryHolder {

    public Consumer<InventoryClickEvent>[] buttons;

    private int page;
    private MenuInventory previewPage;
    private MenuInventory nexPage;
    protected int labels;

    public MenuInventory(int label , String title) {
        super(label, title);
        this.labels = label;
        this.page = 0;
        buttons = new Consumer[label*9];
    }

    protected MenuInventory(int label , String title , int page) {
        super(label, title);
        this.labels = label;
        this.page = page;
        buttons = new Consumer[label*9];
    }

    public void addElement(ItemStack item , Consumer<InventoryClickEvent> button ) {
        int empty = getInventory().firstEmpty();
        if (empty/9 >= labels-1) {
//            TODO
//            za wiele elementow nie dodaje LD
            return;
        }
        getInventory().setItem(empty , item);
        buttons[empty] = button;
    }


    @Override
    public void onClose(Player p) {

    }

    @Override
    public void onOpen(Player p) {

    }

    @Override
    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null) {
            if (isHolderInventory(e.getClickedInventory())) {
                int slot = e.getSlot();
                if (slot >= 0 && slot < getInventory().getSize()) {
                    if (buttons[slot] != null) {
                        buttons[slot].accept(e);

                    }
                }
            }
        }
        e.setCancelled(true);
    }
}
