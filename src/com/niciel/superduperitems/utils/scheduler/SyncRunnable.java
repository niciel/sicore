package com.niciel.superduperitems.utils.scheduler;

public class SyncRunnable implements Runnable {

    private Runnable sync;
    private TaskWatcher watcher;

    public SyncRunnable(Runnable sync, TaskWatcher watcher) {
        this.sync = sync;
        this.watcher = watcher;
    }

    @Override
    public void run() {
        sync.run();
        watcher.setCompleted(false);
    }
}
