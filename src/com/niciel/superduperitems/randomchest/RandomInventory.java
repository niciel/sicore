package com.niciel.superduperitems.randomchest;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomInventory {



    private static Random random = new Random();

    private IRandomBlock block;

    public ItemStack[] generateContest(Player p) {
        float rand = random.nextFloat();
        List<ItemStack> list =  new ArrayList<>();
        block.generate(rand ,list);
        return null;
    }



}
