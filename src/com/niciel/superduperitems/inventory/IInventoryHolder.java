package com.niciel.superduperitems.inventory;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface IInventoryHolder extends InventoryHolder {

    UUID getUUID();

    void onClose(Player p) ;

    void onOpen(Player p) ;


    void onDrag(InventoryDragEvent e) ;

    void onClick(InventoryClickEvent e) ;

    default boolean isHolderInventory(@Nullable Inventory inv) {
        if (inv == null)
            return false;
        if (inv.getHolder() instanceof IInventoryHolder) {
            return ((IInventoryHolder) inv.getHolder()).getUUID().equals(getUUID());
        }
        return false;
    }

    public static boolean isHolderInventory(Inventory inv1, Inventory inv2) {
        if (inv1 == null)
            return false;
        if (inv1.getHolder() instanceof IInventoryHolder) {
            if (inv2.getHolder() instanceof IInventoryHolder) {
                return ((IInventoryHolder) inv1.getHolder()).getUUID().equals(((IInventoryHolder) inv2.getHolder()).getUUID());
            }
        }
        return false;
    }

}
