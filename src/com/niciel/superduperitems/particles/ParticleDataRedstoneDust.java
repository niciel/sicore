package com.niciel.superduperitems.particles;

import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleDataRedstoneDust extends ParticleData {

    @ChatEditable
    @GsonSimpleSerialize
    private Color color;
    @ChatEditable
    @GsonSimpleSerialize
    private float size;


    @Override
    public void send(Player p) {
        p.spawnParticle(particle  ,x,y,z,count,offsetX,offsetY,offsetZ ,extra , new Particle.DustOptions(color,size));
    }
}
