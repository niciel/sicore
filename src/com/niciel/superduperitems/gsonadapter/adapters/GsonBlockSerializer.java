package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.Type;

public class GsonBlockSerializer implements JsonSerializer<BlockData> , JsonDeserializer<BlockData> {


    @Override
    public BlockData deserialize(JsonElement e, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = e.getAsJsonObject();
        BlockData ret = Bukkit.createBlockData(o.get("data").getAsString());
        return ret;
    }

    @Override
    public JsonElement serialize(BlockData b, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = new JsonObject();
        o.addProperty("class" , b.getClass().getName());
        o.addProperty("data" , b.getAsString());
        return o;
    }
}
