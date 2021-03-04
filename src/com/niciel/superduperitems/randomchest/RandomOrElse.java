package com.niciel.superduperitems.randomchest;

import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class RandomOrElse implements IRandomBlock {


    private static Random random = new Random();

    @ChatEditable
    @GsonSimpleSerialize
    private ItemStack ontrue;
    @ChatEditable
    @GsonSimpleSerialize
    private float minimumLuck;
    @ChatEditable
    @GsonSimpleSerialize
    private ItemStack onfalse;

    @Override
    public void generate( float luck, List<ItemStack> output) {


    }



}
