package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class GsonUUIDAdapter implements JsonSerializer<UUID> , JsonDeserializer<UUID> {



    @Override
    public UUID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return UUID.fromString(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(uuid.toString());
    }
}
