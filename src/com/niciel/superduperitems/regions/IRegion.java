package com.niciel.superduperitems.regions;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.bukkit.util.Vector;

import java.util.UUID;

public interface IRegion {



    public boolean isInside(Vector isInside);
    public UUID getID();





}
