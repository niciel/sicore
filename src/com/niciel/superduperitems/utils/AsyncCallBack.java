package com.niciel.superduperitems.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.ref.WeakReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AsyncCallBack<T> implements Runnable {




    protected Function<T  , Boolean> runAsync;
    protected BukkitTask asyncTask;
    protected boolean success;
    protected boolean asyncEnds;
    protected WeakReference<JavaPlugin> owner;
    protected Consumer<T> runSync;


    public AsyncCallBack(JavaPlugin owner ,Function<T  , Boolean>  async ,Consumer<T> sync ) {
        this.runAsync = async;
        this.owner = new WeakReference<>(owner);
        this.runSync = sync;
        this.success = false;
        this.asyncEnds = false;
    }

    public void execute() {
        asyncTask = Bukkit.getScheduler().runTaskAsynchronously(getOwner() , this);
    }

    public JavaPlugin getOwner() {
        return owner.get();
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isAsyncEnds() {
        return asyncEnds;
    }

    @Override
    public abstract void run() ;


}
