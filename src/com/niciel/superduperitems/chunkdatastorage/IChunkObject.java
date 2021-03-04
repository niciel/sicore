package com.niciel.superduperitems.chunkdatastorage;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;

import java.util.UUID;

public interface IChunkObject extends GsonSerializable {


    public void enable(ChunkData d);
    public void disable(ChunkData d);
    public UUID getUUID();




}
