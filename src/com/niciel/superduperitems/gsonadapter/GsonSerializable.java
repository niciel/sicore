package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonManager;

public interface GsonSerializable {


    public default JsonObject serialize() {
        return GsonManager.serializeObject(this).getAsJsonObject();
    }

    public default void deserialize(JsonObject o) {
        GsonManager.deserializeObject(this , o);
    }

}
