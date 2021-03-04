package com.niciel.superduperitems.core.old;

import com.niciel.superduperitems.inventory.CustomInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public class CraftingInventoryPattern  extends CustomInventory {

    public HashSet<Integer> selected;

    public final WeakReference<Player> player;
    public final CraftingPattern pattern;

    public ItemStack toRemove;

    public boolean active = true;


    public CraftingInventoryPattern(Player p , CraftingPattern pattern) {
        super(6, "crafting" , true);
        this.pattern = pattern;
        this.player = new WeakReference<>(p);
        selected = new HashSet<>();
        defaultSlot = si -> {
            si.setCancelled(true);
            si.setResult(Event.Result.DENY);
        };
    }


    @Override
    public void onOpen(Player p) {
        super.onOpen(p);
        WeakReference<CraftingInventoryPattern> _instance = new WeakReference(this);
        for (int row = 0 ; row < 6 ; row ++) {
            for (int column = 0 ; column < 6 ; column++) {
                int slot = row*9 + column;
                set(slot , _instance.get().pattern.blankPattern , e -> {
                    e.setCancelled(true);
                    e.setResult(Event.Result.DENY);
                    if (! _instance.get().active)
                        return;
                    if (_instance.get().selected.contains(slot)) {
                        if (_instance.get().pattern.canChangeMind) {
                            _instance.get().selected.remove(slot);
                            _instance.get().getInventory().setItem(slot , _instance.get().pattern.blankPattern);
                        }
                        else
                            return;
                    }
                    else {
                        _instance.get().selected.add(slot);
                        _instance.get().getInventory().setItem(slot , _instance.get().pattern.pickedPatter);
                    }
                    if (_instance.get().pattern.check(this)) {
                        active = false;
                    }
                });
            }
        }
    }

}
