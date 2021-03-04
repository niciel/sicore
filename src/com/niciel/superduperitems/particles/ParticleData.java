package com.niciel.superduperitems.particles;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleData implements GsonSerializable {

    @ChatEditable
    public Particle particle = Particle.BARRIER;
    @ChatEditable
    public double x;
    @ChatEditable
    public  double y;
    @ChatEditable
    public double z;
    @ChatEditable
    public int count = 1;
    @ChatEditable
    public double offsetX = 0;
    @ChatEditable
    public double offsetY = 0;
    @ChatEditable
    public double offsetZ = 0;
    @ChatEditable
    public double extra = 0;
    @ChatEditable
    public Color color = Color.WHITE;
    @ChatEditable
    public float size = 1;
    @ChatEditable
    public int timer =20;


    public void send(Player p) {
        if (particle == Particle.REDSTONE)
            p.spawnParticle(particle , x, y, z, count , offsetX ,offsetY , offsetZ , extra , new Particle.DustOptions(color, size));
        else
            p.spawnParticle(particle , x, y, z, count , offsetX ,offsetY , offsetZ , extra);

    }

    public ParticleData clone() {
        ParticleData data = new ParticleData();
        data.particle = this.particle;
        data.x = this.x;
        data.y = this.y;
        data.z = this.z;
        data.offsetX = this.offsetX;
        data.offsetY = this.offsetY;
        data.offsetZ = this.offsetZ;
        data.extra = this.extra;
        data.color = this.color;
        data.size = this.size;
        data.timer = this.timer;
        return data;
    }


    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("x" , x);
        o.addProperty("y" , y);
        o.addProperty("z" , z);

        o.addProperty("count" , count);

        o.addProperty("ox" , offsetX);
        o.addProperty("oy" , offsetY);
        o.addProperty("oz" , offsetZ);

        o.addProperty("extra" , extra);

        o.addProperty("color" , color.asRGB());
        o.addProperty("size" , size);
        o.addProperty("timer" , timer);
        o.addProperty("particle" , particle.name());
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        x = o.get("x").getAsDouble();
        y = o.get("y").getAsDouble();
        z = o.get("z").getAsDouble();

        count = o.get("count").getAsInt();

        offsetX = o.get("ox").getAsDouble();
        offsetY = o.get("oy").getAsDouble();
        offsetZ = o.get("oz").getAsDouble();

        timer = o.get("timer").getAsInt();
        extra = o.get("extra").getAsDouble();

        color = Color.fromRGB(o.get("color").getAsInt());
        size = o.get("size").getAsFloat();
        particle = Particle.valueOf(o.get("particle").getAsString());
    }
}
