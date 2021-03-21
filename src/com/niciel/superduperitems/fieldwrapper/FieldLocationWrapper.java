package com.niciel.superduperitems.fieldwrapper;

import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.IObjectSelfEditable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.inGameEditor.editors.object.EditorChatObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
