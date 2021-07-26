package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonListadapter implements JsonSerializer<List> , JsonDeserializer<List> {


    // not support primitive types support only special types

    @Override
    public List deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonArray() == false)
            return new ArrayList();
        JsonArray array = jsonElement.getAsJsonArray();
        List list=  new ArrayList();
        if (array == null) {
            SDIPlugin.instance.logWarning(this , " deserializacja listy sie nie powiodla: " +jsonElement);
            return null;
        }
        array.forEach(c -> {
            if (c.isJsonNull()) {
                return;
            }
            Object o = GsonManager.getInstance().fromJson(c.getAsJsonObject());
            if (o != null)
                list.add(o);
            else {
                SDIPlugin.instance.logWarning(this , " deserializacja elementu listy sie nie powiodla: " +c);
            }
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
