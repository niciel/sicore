package com.niciel.superduperitems.inGameEditor.fieldwrapper;

import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FieldLocationWrapper  {

    @GsonSimpleSerialize
    @ChatEditable
    private FieldWorldWrapper world = new FieldWorldWrapper();

    @GsonSimpleSerialize
    @ChatEditable
    private Vector v;


    public Location get() {
        if (world.get() == null || v == null) {
            return null;
        }
        return new Location(world.get(),v.getX(), v.getY() , v.getZ());
    }
}
