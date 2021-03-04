package com.niciel.superduperitems.inventory;

import com.niciel.superduperitems.utils.IManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryManager implements IManager , Listener {


    private HashMap<UUID, CustomInventory> playerUuidToIn;

    public InventoryManager() {
        playerUuidToIn = new HashMap<>();
    }

    public CustomInventory getInventory(Player p) {
        return playerUuidToIn.get(p.getUniqueId());
    }

    public void ifExistInventory(Player p , Consumer<CustomInventory> c) {
        CustomInventory hol = getInventory(p);
        if (hol == null)
            return ;
        c.accept(hol);
    }

    public void ifExistInventory(HumanEntity p , Consumer<CustomInventory> c) {
        CustomInventory hol = getInventory((Player) p);
        if (hol == null)
            return ;
        c.accept(hol);
    }

    @EventHandler
    public void onDragItem(InventoryDragEvent e) {
        ifExistInventory(e.getWhoClicked() , c -> c.onDrag(e));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        ifExistInventory(e.getWhoClicked() , c->
          c.onClick(e));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpenInventory(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory) {
            playerUuidToIn.put(e.getPlayer().getUniqueId() , (CustomInventory) e.getInventory().getHolder());
            ((CustomInventory) e.getInventory().getHolder()).onOpen((Player) e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpenInventory(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory) {
            ((CustomInventory) e.getInventory().getHolder()).onClose((Player) e.getPlayer());
            playerUuidToIn.remove(e.getPlayer().getUniqueId());
        }
    }




}
