package com.niciel.superduperitems.regions;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.bukkit.util.Vector;

public interface IRegion extends GsonSerializable {



    public boolean isInside(Vector isInside);
    public String getID();



}
