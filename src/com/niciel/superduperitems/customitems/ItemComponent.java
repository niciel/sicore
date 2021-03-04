package com.niciel.superduperitems.customitems;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface ItemComponent {

    void onEnable(CustomItem ci);

    JsonObject serialize();

    void deserialize(JsonObject e);


}
