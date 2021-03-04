package com.niciel.superduperitems.core.zombie;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.randomchest.IRandomBlock;
import com.niciel.superduperitems.utils.Vector2int;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public class ChestDropState implements GsonSerializable {

    @GsonSimpleSerialize
    public Vector2int chunkpos;
    @GsonSimpleSerialize
    public Vector position;
    @GsonSimpleSerialize
    public long lastOpenTime;
    @GsonSimpleSerialize
    public String chestScheme;

    @GsonSimpleSerialize
    public BlockData block;

    public boolean generated = false;

}
