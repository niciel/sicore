package com.niciel.superduperitems.utils.itemstack;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SItemStack implements  ItemSimilarity {

    @ChatEditable
    private Material material;
    @ChatEditable
    private String name;

    @Override
    public boolean isSimilar(ItemStack is) {
        if (material != null) {
            if (material != is.getType())
                return false;
        }
        if (name != null) {
            if (is.hasItemMeta()) {
                ItemMeta im = is.getItemMeta();
                if (im.hasDisplayName()) {
                    if (im.getDisplayName().contentEquals(name))
                        return true;
                }
            }
            return false;
        }
        return true;
    }
}
