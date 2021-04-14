package com.niciel.superduperitems.managers;

import com.niciel.superduperitems.SDIPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public interface IManager  {


    default void onEnable(){}

    default void onDisable(){}

    /**
     * called after all managers onEnable()
     */
    default void onLateEnable(){}

    default SiJavaPlugin getPlugin() {
        return ManagersHandle.getRegisteredOwner((Class<IManager>) getClass());
    }

    /**
     *  UNSAFE could return null if manager was not registered
     * @param manager
     * @param <T>
     * @return
     */


    static <T extends IManager> T getManager(Class<T> manager)  {
        return (T) ManagersHandle.getManager(manager);
    }

}
