package com.niciel.superduperitems.utils;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.bukkit.Chunk;

import java.util.Objects;

public class Vector2int  implements GsonSerializable {

    public int x,y;


    public Vector2int() {

    }

    public Vector2int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2int(Chunk c) {
        this.x = c.getX();
        this.y = c.getZ();
    }

    @Override
    public String toString() {
        return "Vector2int{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void add(Vector2int v) {
        x += v.x;
        y += v.y;
    }

    public Vector2int copy() {
        return new Vector2int(x,y);
    }

    public void subtract(Vector2int v) {
        x -= v.x;
        y -= v.y;
    }

    public boolean equals(Vector2int v) {
        if (v.x == x && v.y == y)
            return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2int that = (Vector2int) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("x" , x);
        o.addProperty("y" , y);
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        this.x = o.get("x").getAsInt();
        this.y = o.get("y").getAsInt();
    }
}
