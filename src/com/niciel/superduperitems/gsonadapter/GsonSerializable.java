package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonManager;

public interface GsonSerializable {


    JsonObject serialize();

    void deserialize(JsonObject o);

}
