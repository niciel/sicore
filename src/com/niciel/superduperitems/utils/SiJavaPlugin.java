package com.niciel.superduperitems.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.rmi.UnexpectedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class SiJavaPlugin extends JavaPlugin {

    private ArrayList<MethodHandle> managersHandles;



    protected void onLateEnable() {
        forEachManagers(c -> {
            c.onLateEnable();
        });
        onPluginLateEnable();
    }

    @Override
    public void onLoad() {

        managersHandles = new ArrayList<MethodHandle>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Field f : this.getClass().getDeclaredFields()) {

            if (IManager.class.isAssignableFrom(f.getType())) {
                if (f.isAccessible() == false)
                    f.setAccessible(true);

                try {
                    managersHandles.add(lookup.unreflectGetter(f));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    
    public <T extends IManager> T getManager(Class<T> clazz) {
        AtomicReference<IManager> m = new AtomicReference<>();
        forEachManagersIfPass(c-> {
            if (c.getClass().getName().contentEquals(clazz.getName())) {
                m.set(c);
                return false;
            }
            return true;
        });
        return (T) m.get();
    }
    
    private void forEachManagers(Consumer<IManager> c) {
        IManager m = null;

        for(MethodHandle h : managersHandles) {
            try {
                m = (IManager) h.invoke(this);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            if (m == null)
                continue;
            c.accept(m);
        }
    }
    private void forEachManagersIfPass(Predicate<IManager> c) {
        IManager m =null;
        for(MethodHandle h : managersHandles) {
            try {
                m = (IManager) h.invoke(this);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (m == null)
                continue;
            if (c.test(m))
                continue;
            break;
        }
    }

    public void onPluginEnable() {}
    public void onPluginDisable(){}
    public void onPluginLateEnable() {}

    @Override
    public void onEnable() {

        onPluginEnable();
        forEachManagers(c-> c.onEnable());
        forEachManagers(c -> {
            if (c instanceof Listener)
                Bukkit.getServer().getPluginManager().registerEvents((Listener) c, this);
            if (c instanceof CommandExecutor) {
                SimpleCommandInfo sc = c.getClass().getAnnotation(SimpleCommandInfo.class);
                if (sc != null) {
                    new SimpleCommand((CommandExecutor) c , sc.command() , sc.description() , sc.usage() , Arrays.asList(sc.aliases()));
                }
            }
            logInfo("zarejestrowano manager: " + c.getClass().getName());
        });

        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                onLateEnable();
            }
        });
    }

    public void logWarning(Object obj ,String msg) {
        String message = obj.getClass().getName() +  ": " + msg;
        getLogger().log(Level.WARNING , message);
        Bukkit.getServer().getOnlinePlayers().forEach(c -> {
            if (c.isOp())
                c.sendMessage(message);
        });
    }

    public void logInfo(String msg) {
        getLogger().log(Level.INFO ,msg);
    }

    @Override
    public void onDisable() {
        forEachManagers(c-> c.onDisable());
        onPluginDisable();
    }



}
