package com.niciel.superduperitems.regions;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class AABBRegion extends ARegion {


    @GsonSimpleSerialize
    @ChatEditable
    private BoundingBox box = new BoundingBox();

    @Override
    public boolean isInside(Vector isInside) {
        return this.box.contains(isInside);
    }


    @Override
    public JsonObject serialize() {
        JsonObject o = super.serialize();
//        TODO


        return o;
    }


    @Override
    public void deserialize(JsonObject o) {
        super.deserialize(o);
    }
}
