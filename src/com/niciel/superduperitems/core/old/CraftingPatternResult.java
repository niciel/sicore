package com.niciel.superduperitems.core.old;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CraftingPatternResult {


    @ChatEditable(name = "znak")
    public String makr;
    @ChatEditable(name = "lista :D")
    public List<String> patter;
    @ChatEditable(name = "tocodropi")
    public ItemStack result;

    private List<Integer> checkList;



    public void doResult(CraftingInventoryPattern inventory) {
        inventory.player.get().sendMessage("LD dzialo sie co ?");
//        TODO
    }

    public boolean match(Set<Integer> collection) {
        if (checkList.size() == collection.size()) {
            return collection.containsAll(checkList);
        }
        return false;
    }


    public void enable() {
        String s;
        checkList = new ArrayList<>();
        int max = patter.size();
        if (max > 6)
            max = 6;
        for (int i = 0 ; i < max ; i++) {
            s = patter.get(i);
            if (s.length() != 6)
                continue;
            for (int x = 0 ; x < 6 ; x++) {
                if (makr.charAt(0) == s.charAt(x))
                    checkList.add(i*9 + x);
            }
        }
    }

}
