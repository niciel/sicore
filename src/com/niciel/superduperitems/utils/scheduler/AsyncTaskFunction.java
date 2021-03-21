package com.niciel.superduperitems.utils.scheduler;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncTaskFunction<AsyncInput , SyncOutput >  implements Runnable {


    private Function<AsyncInput , SyncOutput> asyncfunction;
    private Consumer<SyncOutput> syncConsumer;
    private AsyncInput input;
    private WeakReference<JavaPlugin> owner;
    private TaskWatcher watcher;

    public AsyncTaskFunction(JavaPlugin pl , AsyncInput input , Function<AsyncInput,SyncOutput> asyncProces, Consumer<SyncOutput> out) {
        this.owner = new WeakReference<>(pl);
        this.input = input;
        this.asyncfunction = asyncProces;
        this.syncConsumer = out;
    }


    @Override
    public void run() {
        SyncOutput out = asyncfunction.apply(input);
        WeakReference<JavaPlugin> pl = this.owner;
        Consumer<SyncOutput> c = syncConsumer;
        SyncConsumer<SyncOutput> sync = new SyncConsumer(watcher ,c , out);
        Bukkit.getServer().getScheduler().runTask(pl.get() ,sync);
    }

    public TaskOperation execute() {
        if (this.watcher == null) {
            watcher = new TaskWatcher();
            watcher.start();
            Bukkit.getServer().getScheduler().runTaskAsynchronously(this.owner.get() , this);
        }
        return watcher;
    }

}
