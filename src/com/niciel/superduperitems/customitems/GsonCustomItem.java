package com.niciel.superduperitems.customitems;

import com.google.gson.*;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class GsonCustomItem implements JsonSerializer<CustomItem> , JsonDeserializer<CustomItem> {


    @Override
    public CustomItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext gson) throws JsonParseException {
        JsonObject o = jsonElement.getAsJsonObject();
        CustomItem item = new CustomItem();
        item.nameID = o.get("nameid").getAsString();
        item.customDataID = o.get("modeid").getAsInt();
        item.category = o.get("category").getAsString();
        item.material = Material.valueOf(o.get("material").getAsString());
        item.allComponents = new ArrayList<>();
        JsonArray array = o.get("components").getAsJsonArray();
        Iterator<JsonElement> itr = array.iterator();
        JsonElement element;
        while (itr.hasNext()) {
            element = itr.next();
            item.allComponents.add(gson.deserialize(element.getAsJsonObject() , ItemComponent.class));
        }
        Material m;
        return item;
    }

    @Override
    public JsonElement serialize(CustomItem customItem, Type type, JsonSerializationContext gson) {
        JsonObject o = new JsonObject();
        o.addProperty("modeid" , customItem.customDataID);
        o.addProperty("nameid" , customItem.nameID);
        o.addProperty("material" , customItem.material.name());
        o.addProperty("category" , customItem.category);
        JsonArray array = new JsonArray();
        for (ItemComponent c : customItem.allComponents) {
            if (c == null) {
                SDIPlugin.instance.logWarning(this , "jakim cudem null");
                continue;
            }
            array.add(gson.serialize(c , ItemComponent.class));
        }
        o.add("components" , array);
        return o;
    }


}
