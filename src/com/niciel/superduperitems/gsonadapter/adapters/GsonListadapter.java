package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonListadapter implements JsonSerializer<List> , JsonDeserializer<List> {


    // not support primitive types support only special types

    @Override
    public List deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonArray();
        List list=  new ArrayList();
        array.forEach(c -> {
            list.add(GsonManager.getInstance().fromJson(c.getAsJsonObject()));
        });
        return list;
    }

    @Override
    public JsonElement serialize(List list, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray array = new JsonArray();
        list.forEach(c-> {
            array.add(GsonManager.getInstance().toJson(c));
        });
        return array;
    }




}
