package com.niciel.superduperitems.utils.scheduler.cooldowns;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.UUID;

public class SimpleCooldownChecker {

    private long waitTill;
    private int safeCapacity;

    private HashSet<UUID> uuid;
    private Queue<Long> uuidExpiredTime;
    private Queue<UUID> uuidToRemove;


    public SimpleCooldownChecker(int waitTill) {
        this(waitTill , 10);
    }


    public SimpleCooldownChecker(int waitSec, int safeCapacity) {
        this.waitTill = 1000*waitSec;
        this.safeCapacity = safeCapacity;
        this.uuidExpiredTime = new ArrayDeque<>();
        this.uuidToRemove = new ArrayDeque<>();
    }

    public boolean isWaiting(UUID uuid) {
        long time = System.currentTimeMillis();
        if (this.uuid.contains(uuid)) {
            check(time);
            return this.uuid.contains(uuid);
        }
        if (this.uuid.size() >= safeCapacity)
            check(time);
        return false;
    }


    public boolean add(UUID uuid) {
        if (isWaiting(uuid))
            return false;
        this.uuid.add(uuid);
        this.uuidToRemove.add(uuid);
        this.uuidExpiredTime.add(System.currentTimeMillis() + waitTill);
        return true;
    }

    public void check(long time) {
        long timer = uuidExpiredTime.peek();
        while (timer <= time) {
            this.uuid.remove(uuidToRemove.poll());
            uuidExpiredTime.remove();
            if (this.uuid.isEmpty())
                timer = Long.MAX_VALUE;
        }
    }

}
