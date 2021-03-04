package com.niciel.superduperitems.core.old;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftingPattern {

    @ChatEditable
    public List<CraftingPatternResult> patters;
    @ChatEditable
    public ItemStack blankPattern;
    @ChatEditable
    public ItemStack pickedPatter;

    @ChatEditable
    public boolean canChangeMind;


    public CraftingPattern(List<CraftingPatternResult> patters, ItemStack blankPattern, ItemStack pickedPatter, boolean canChangeMind) {
        this.patters = patters;
        this.blankPattern = blankPattern;
        this.pickedPatter = pickedPatter;
        this.canChangeMind = canChangeMind;
    }

    public boolean check(CraftingInventoryPattern inventory) {
        for (CraftingPatternResult p : patters) {
            if (p.match(inventory.selected)) {
                p.doResult(inventory);
                return true;
            }
        }
        return false;
//        TODO
    }




}
