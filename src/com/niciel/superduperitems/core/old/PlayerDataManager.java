package com.niciel.superduperitems.core.old;

import com.niciel.superduperitems.utils.IManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.HashMap;

public class PlayerDataManager implements IManager , Listener {


    private HashMap<String , PlayerData> players = new HashMap<String , PlayerData>();





    @EventHandler
    public void onStartDig(BlockDamageEvent e) {

    }

}
