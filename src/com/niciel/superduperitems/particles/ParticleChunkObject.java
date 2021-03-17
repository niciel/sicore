package com.niciel.superduperitems.particles;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.*;
import com.niciel.superduperitems.chunkdatastorage.*;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticleChunkObject implements IChunkObject , IChunkTicking {


    @ChatEditable
    public List<ParticleData> particles;
    private UUID uuid;

    private PlayerIterator players;

    int tick=0;

    public ParticleChunkObject() {
        uuid = UUID.randomUUID();
        particles = new ArrayList<>();
    }


    @Override
    public void enable(ChunkData d) {
        players = d.playersInRange;
    }

    @Override
    public void disable(ChunkData d) {
    }

    @Override
    public void onTick() {
        for (ParticleData d : particles) {
            if (tick%d.timer==0)
                players.forEach(p ->d.send(p));
        }
        tick++;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public JsonObject serialize() {
        JsonArray array = new JsonArray();
        for (ParticleData d : particles)
            array.add(GsonManager.getInstance().toJson(d));
        JsonObject o = new JsonObject();
        o.addProperty("class" , ParticleChunkObject.class.getName());
        o.addProperty("uuid" , uuid.toString());
        o.add("particles" , array);
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        JsonArray array = o.get("particles").getAsJsonArray();
        uuid = UUID.fromString(o.get("uuid").getAsString());
        array.forEach(a-> particles.add((ParticleData) SDIPlugin.instance.getGson().fromJson(a , GsonSerializable.class)));
    }

}
