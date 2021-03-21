package com.niciel.superduperitems.utils.scheduler;

import com.niciel.superduperitems.managers.SiJavaPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

class SyncConsumer<Sync> implements  Runnable{

    private Consumer<Sync> syncFunction;
    private Sync syncConsumer;
    private TaskWatcher watcher;

    public SyncConsumer(TaskWatcher watcher,Consumer<Sync> syncFunction, Sync syncConsumer) {
        this.syncFunction = syncFunction;
        this.syncConsumer = syncConsumer;
        this.watcher = watcher;
    }

    @Override
    public void run() {
        syncFunction.accept(syncConsumer);
        watcher.setCompleted(true);
        watcher = null;
        syncConsumer = null;
    }
}
