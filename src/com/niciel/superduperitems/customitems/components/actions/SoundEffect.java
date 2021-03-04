package com.niciel.superduperitems.customitems.components.actions;

import com.niciel.superduperitems.cfg.Cfg;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SoundEffect implements IHumanAction {



    @Cfg(path = "sound")
    public Sound soundEffect = Sound.BLOCK_ANVIL_HIT;




    @Override
    public void accept(Player p) {
        p.sendMessage("jeszcze nie dziala :* , " +soundEffect);
    }
}
