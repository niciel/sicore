package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IAfterGsonDeserialize {




    void afterGsonSerialization(JsonElement serializedFrom) ;
}
