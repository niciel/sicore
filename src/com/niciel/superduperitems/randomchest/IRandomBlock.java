package com.niciel.superduperitems.randomchest;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IRandomBlock extends GsonSerializable {

    public void generate(float luck , List<ItemStack> output);

}
