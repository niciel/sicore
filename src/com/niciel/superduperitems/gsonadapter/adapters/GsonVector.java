package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

public class GsonVector implements JsonSerializer<Vector> , JsonDeserializer<Vector> {

    @Override
    public Vector deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = jsonElement.getAsJsonObject();
        double x,y,z;
        x = o.get("x").getAsDouble();
        y = o.get("y").getAsDouble();
        z = o.get("z").getAsDouble();
        return new Vector(x,y,z);
    }

    @Override
    public JsonElement serialize(Vector v, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = new JsonObject();
        o.addProperty("x",v.getX());
        o.addProperty("y",v.getY());
        o.addProperty("z",v.getZ());
        return o;
    }
}
