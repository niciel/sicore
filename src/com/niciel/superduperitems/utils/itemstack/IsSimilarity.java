package com.niciel.superduperitems.utils.itemstack;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IsSimilarity implements ItemSimilarity {


    @ChatEditable(name = "similar")
    public List<ItemSimilarity> similar;




    public IsSimilarity() {
        similar = new ArrayList<>();
    }

    @Override
    public boolean isSimilar(ItemStack is) {
        for (ItemSimilarity s : similar)
            if (! s.isSimilar(is))
                return false;
        return true;
    }

    public IsSimilarity addType(Material mat) {
        similar.add( s-> {
            if (s.getType() == mat)
                return true;
            return false;
        });
        return this;
    }

    public IsSimilarity addModelID(int modelId) {
        similar.add( s-> {
            if (s.hasItemMeta()) {
                ItemMeta im = s.getItemMeta();
                if (im.hasCustomModelData()) {
                    if (im.getCustomModelData() == modelId)
                        return true;
                }

            }
            return false;
        });
        return this;
    }


}
