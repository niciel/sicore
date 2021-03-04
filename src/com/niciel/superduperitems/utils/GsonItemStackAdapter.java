package com.niciel.superduperitems.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GsonItemStackAdapter implements JsonSerializer<ItemStack> , JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement e, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject o = e.getAsJsonObject();
        String item = o.get("item").getAsString();
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.loadFromString(item);
            return yml.getItemStack("item");
        } catch (InvalidConfigurationException ex) {
            throw  new JsonParseException(ex.getCause());
        }
    }


    @Override
    public JsonElement serialize(ItemStack is, Type type, JsonSerializationContext jsonSerializationContext) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item" , is);
        JsonObject o = new JsonObject();
        o.addProperty("class" , ItemStack.class.getName());
        o.addProperty("item" , yml.saveToString());
        return o;
    }




}
