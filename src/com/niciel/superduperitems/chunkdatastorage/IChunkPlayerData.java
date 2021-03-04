package com.niciel.superduperitems.chunkdatastorage;

import com.niciel.superduperitems.utils.Vector2int;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface IChunkPlayerData  {


    public Location getLocation();
    public Vector2int getPreviusChunk();
    public void move(Vector2int newPosition);

    public int viewDistance();

}
