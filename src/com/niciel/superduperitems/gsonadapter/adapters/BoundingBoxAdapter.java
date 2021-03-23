package com.niciel.superduperitems.gsonadapter.adapters;

import com.google.gson.*;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

public class BoundingBoxAdapter implements JsonSerializer<BoundingBox> , JsonDeserializer<BoundingBox> {

    @Override
    public BoundingBox deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext des) throws JsonParseException {
        Vector a,b;
        JsonObject o = jsonElement.getAsJsonObject();
        a = GsonManager.getInstance().fromJson(o.get("max"),Vector.class);
        b = GsonManager.getInstance().fromJson(o.get("min"),Vector.class);
        return BoundingBox.of(a,b);
    }

    @Override
    public JsonElement serialize(BoundingBox boundingBox, Type type, JsonSerializationContext des) {
        JsonObject ob = new JsonObject();
        ob.add("min" ,GsonManager.getInstance().toJson(boundingBox.getMin()));
        ob.add("max" , GsonManager.getInstance().toJson(boundingBox.getMax()));
        return ob;
    }


}
