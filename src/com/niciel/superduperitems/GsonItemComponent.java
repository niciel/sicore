package com.niciel.superduperitems;

import com.google.gson.*;
import com.niciel.superduperitems.customitems.ItemComponent;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

public class GsonItemComponent implements JsonDeserializer<ItemComponent> , JsonSerializer<ItemComponent> {

    @Override
    public ItemComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = (JsonObject) jsonElement;
        String clazz = o.get("class").getAsString();
        ItemComponent ret = null;
        Class t;
        try {
            t = Class.forName(clazz);
            ret = (ItemComponent) t.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ret.deserialize(o);
        return ret;
    }

    @Override
    public JsonElement serialize(ItemComponent itemComponent, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject o = itemComponent.serialize();
        if (o == null)
            o = new JsonObject();
        o.addProperty("class" , itemComponent.getClass().getName());
        return o;
    }




}
