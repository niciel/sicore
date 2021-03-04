package com.niciel.superduperitems.regions;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;

public abstract class ARegion implements IRegion {


    @GsonSimpleSerialize
    @ChatEditable
    private String id;


    @Override
    public String getID() {
        return id;
    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("id" , id);
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        id = o.get("id").getAsString();
    }
}
