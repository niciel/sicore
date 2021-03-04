package com.niciel.superduperitems.regions;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.Tickable;
import com.sun.org.apache.bcel.internal.generic.IREM;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegionEngine   {



    private HashMap<String , IRegion> nameToRegion;
    private List<IRegion> boundings;

    public void addRegion(IRegion r) {
        boundings.add(r);
    }


}
