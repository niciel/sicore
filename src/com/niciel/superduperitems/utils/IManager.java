package com.niciel.superduperitems.utils;

import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public interface IManager  {


    default void onEnable(){}

    default void onDisable(){}

    /**
     * called after all managers onEnable()
     */
    default void onLateEnable(){}


    default SDIPlugin getPlugin() {
        return SDIPlugin.instance;
    }

    /**
     *  UNSAFE could return null if manager was not registered
     * @param manager
     * @param <T>
     * @return
     */
    static <T extends IManager> T getManager(Class<T> manager)  {
        SDIPlugin pl = SDIPlugin.instance;
        if (pl ==null)
            return null;
        return pl.getManager(manager);
    }

}
