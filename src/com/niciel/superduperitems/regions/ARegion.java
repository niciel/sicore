package com.niciel.superduperitems.regions;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;

import java.util.UUID;

public abstract class ARegion implements IRegion {


    @GsonSimpleSerialize
    @ChatEditable
    private UUID uuid;

    @Override
    public UUID getID() {
        return uuid;
    }

}
