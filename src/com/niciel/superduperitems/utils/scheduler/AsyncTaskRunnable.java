package com.niciel.superduperitems.utils.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.WeakReference;

public class AsyncTaskRunnable extends AbstractSimpleTask {

    
    private Runnable async;
    private Runnable sync;

    public AsyncTaskRunnable(JavaPlugin owner , Runnable async , Runnable sync) {
        super(owner);
        this.async = async;
        this.sync = sync;
    }

    public TaskOperation execute() {
        getWatcher().start();
        Bukkit.getScheduler().runTaskAsynchronously(getOwner(),this);
        return getWatcher();
    }

    @Override
    public void run() {
        async.run();
        Bukkit.getScheduler().scheduleSyncDelayedTask(getOwner(), new SyncRunnable(sync , getWatcher()));
    }
}
