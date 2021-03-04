package com.niciel.superduperitems.fakeArmorstands;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public class DataModelBlock implements GsonSerializable {

    public BlockData blockData;
    public String data;
    public Vector position;

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("data" , data);
        o.add("blockdata" , GsonManager.toJsonTree(blockData , BlockData.class));
        o.add("position" , GsonManager.toJsonTree(position , Vector.class));
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        this.blockData = (BlockData) SDIPlugin.instance.getGson().fromJson(o.get("blockdata") , BlockData.class);
        this.position = SDIPlugin.instance.getGson().fromJson(o.get("position") , Vector.class);
        this.data = o.get("data").getAsString();
    }

    public DataModelBlock clone() {
        DataModelBlock r = new DataModelBlock();
        r.data = data;
        r.position = position.clone();
        r.blockData = blockData.clone();
        return r;
    }
}
