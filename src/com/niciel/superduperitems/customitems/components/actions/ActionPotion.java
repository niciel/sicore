package com.niciel.superduperitems.customitems.components.actions;

import com.niciel.superduperitems.cfg.Cfg;
import com.niciel.superduperitems.cfg.SerializationCallBack;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ActionPotion implements IHumanAction , SerializationCallBack {


    @Cfg(path = "removeEffect")
    public boolean removeEffect = false;

    @Cfg(path = "type")
    public PotionEffectType type = PotionEffectType.NIGHT_VISION;
    @Cfg(path = "duration")
    public int duration = 10;
    @Cfg(path = "power")
    public int level = 1;
    @Cfg(path = "ambient")
    public boolean ambient = false;
    @Cfg(path = "particles")
    public boolean particles = false;



    public PotionEffect effect;

    @Override
    public void accept(Player p) {
        if (removeEffect)
            p.removePotionEffect(type);
        else
            effect.apply(p);
    }


    @Override
    public void onSerializeEnd() {
        if (! removeEffect)
            effect = new PotionEffect(type,  duration , level , ambient , particles);
    }
}
