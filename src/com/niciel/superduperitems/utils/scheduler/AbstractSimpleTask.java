package com.niciel.superduperitems.utils.scheduler;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.WeakReference;

public abstract class AbstractSimpleTask implements Runnable{

    private TaskWatcher watcher;
    private WeakReference<JavaPlugin> owner;


    public AbstractSimpleTask(JavaPlugin owner) {
        this.owner = new WeakReference<>(owner);
        this.watcher = new TaskWatcher();
    }

    protected TaskWatcher getWatcher(){
        return this.watcher;
    }

    public JavaPlugin getOwner() {
        return owner.get();
    }

    public abstract TaskOperation execute();

}
