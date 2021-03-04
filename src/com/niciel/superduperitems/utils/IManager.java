package com.niciel.superduperitems.utils;

import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public interface IManager  {


    default void onEnable(){}

    default void onDisable(){}

    default void onLateEnable(){}

    default SDIPlugin getPlugin() {
        return SDIPlugin.instance;
    }

}
