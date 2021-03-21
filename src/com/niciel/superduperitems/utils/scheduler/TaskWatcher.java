package com.niciel.superduperitems.utils.scheduler;

class TaskWatcher implements  TaskOperation {

    private boolean completed = false;
    private boolean running = false;
    private boolean fail;

    private int stage = -1;

    public void setCompleted(boolean fail) {
        if (stage==0) {
            this.fail = fail;
            this.stage = 1;
            this.completed = true;
            this.running = false;
        }
    }

    public void start() {
        if (this.stage == -1) {
            this.stage = 0;
            this.running = true;
        }
    }



    public boolean isCompleted() {
        return completed;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFail() {
        return fail;
    }
}
