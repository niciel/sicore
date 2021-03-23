package com.niciel.superduperitems.regions;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.IFieldUpdateCallBack;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class AABBRegion extends ARegion {


    @GsonSimpleSerialize
    @ChatEditable
    private BoundingBox box = new BoundingBox();



    private BoundingBox getBoundingBox(){
        return box;
    }

    @Override
    public boolean isInside(Vector isInside) {
        return this.box.contains(isInside);
    }




}
