package com.niciel.superduperitems.managers;

import com.niciel.superduperitems.utils.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class SiJavaPlugin extends JavaPlugin {

    private ArrayList<MethodHandle> managersHandles;
    private ArrayList<String> managersTypes;


    @Override
    public void onLoad() {
        managersHandles = new ArrayList<MethodHandle>();
        managersTypes = new ArrayList<>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle handle;
        for (Field f : this.getClass().getDeclaredFields()) {

            if (IManager.class.isAssignableFrom(f.getType())) {
                if (f.isAccessible() == false)
                    f.setAccessible(true);

                try {
                    handle = lookup.unreflectGetter(f);
                    managersHandles.add(handle);
                    managersTypes.add(f.getType().getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        ManagersHandle.register(this);
    }

    public <T extends IManager> T getManager(Class<T> clazz) {
        String name = clazz.getName();
        for(int i = 0 ; i< managersHandles.size() ;i++) {
            if (managersTypes.get(i).contentEquals(name)) {
                try {
                    return (T) managersHandles.get(i).invoke(this);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public void forEachManagers(Consumer<IManager> c) {
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
                    SimpleCommand command =  new SimpleCommand((CommandExecutor) c , sc.command() , sc.description() , sc.usage() , Arrays.asList(sc.aliases()));
                }
            }
            logInfo("zarejestrowano manager: " + c.getClass().getName());
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
