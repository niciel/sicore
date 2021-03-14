package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;

import java.lang.reflect.Type;

public class GsonSerializableAdapter implements JsonSerializer<GsonSerializable> , JsonDeserializer<GsonSerializable> {


    @Override
    public GsonSerializable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = jsonElement.getAsJsonObject();
        Class clazz = null;
        GsonSerializable g;
        try {
            clazz = Class.forName(o.get("class").getAsString());
            g = (GsonSerializable) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new JsonParseException("wtf");
        }
        g.deserialize(o);
        return g;
    }

    @Override
    public JsonElement serialize(GsonSerializable g, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = g.serialize();
        o.addProperty("class" , g.getClass().getName());
        return o;
    }
}
