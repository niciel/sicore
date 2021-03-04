package com.niciel.superduperitems.utils;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.bukkit.block.Block;

import java.util.Objects;

public class Vector3int implements GsonSerializable {


    int x,y,z;


    public Vector3int(int x , int y, int z) {
        this.x =x;
        this.z = z;
        this.y = y;
    }


    public Vector3int(Block b) {
        this.x = b.getX();
        this.y = b.getY();
        this.z = b.getZ();
    }


    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("x" ,x);
        o.addProperty("y" , y);
        o.addProperty("z" ,z);
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        this.x = o.get("x").getAsInt();
        this.y = o.get("y").getAsInt();
        this.z = o.get("z").getAsInt();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3int that = (Vector3int) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
