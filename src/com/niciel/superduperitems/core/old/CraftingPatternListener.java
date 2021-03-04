package com.niciel.superduperitems.core.old;

import com.niciel.superduperitems.utils.SpigotUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class CraftingPatternListener implements Listener {

    private EnumMap<Material, CraftingPattern> patterns;


    public CraftingPatternListener() {
        patterns = new EnumMap<Material, CraftingPattern>(Material.class);
        List<CraftingPatternResult> list;
        CraftingPattern pattern ;
        CraftingPatternResult result;
        result = new CraftingPatternResult();
        list = new ArrayList<>();

        result.makr = "x";
        result.patter = Arrays.asList(new String[]{
           "xoooox",
           "oooooo",
           "oooooo",
           "oooooo",
           "oooooo",
           "xoooox"
        });
        list.add(result);
        result.enable();
        pattern = new CraftingPattern(list , new ItemStack(Material.STONE) , new ItemStack(Material.COBBLESTONE) , false);
        patterns.put(Material.COBBLESTONE , pattern);

    }

    @EventHandler
    public void onPPM(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        PlayerInventory inv = e.getPlayer().getInventory();
        if (inv.getItemInOffHand() == null || inv.getItemInMainHand() == null)
            return;
        if (inv.getItemInMainHand().getType() == inv.getItemInOffHand().getType()) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().isInteractable() == false) ) {
                CraftingPattern pattern = patterns.get(inv.getItemInMainHand().getType());
                if (pattern == null)
                    return;
                e.setCancelled(true);

                if (SpigotUtils.removeItemExactStack(inv , inv.getItemInMainHand() , 6)) {
//                    TODO
                }
                else
                    return;
                CraftingInventoryPattern crafting = new CraftingInventoryPattern(e.getPlayer() , pattern);
                e.getPlayer().openInventory(crafting.getInventory());
            }
        }
    }



}
