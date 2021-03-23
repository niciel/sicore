package com.niciel.superduperitems.managers;

import com.niciel.superduperitems.utils.Dual;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public final class ManagersHandle {


    private static List<SiJavaPlugin> list = new ArrayList<>();


    public static <T extends  IManager> IManager getManager(Class<T> clazz) {
        T mag;
        for (SiJavaPlugin p : list) {
           mag = p.getManager(clazz);
           if (mag != null)
               return mag;
        }
        return null;
    }

    public static void register(SiJavaPlugin plugin) {
        if (list.stream().filter(p-> p.getName().contentEquals(plugin.getName())).findAny().isPresent())
            return;
        list.add(plugin);
    }



    public static void forEachPlugin(Consumer<SiJavaPlugin> plugin) {
        for (SiJavaPlugin p : list) {
            plugin.accept(p);
        }
    }



}
