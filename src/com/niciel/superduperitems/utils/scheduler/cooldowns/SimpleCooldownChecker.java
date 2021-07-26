package com.niciel.superduperitems.utils.scheduler.cooldowns;

import java.util.*;

public class SimpleCooldownChecker {

    private long waitTill;
    private int safeCapacity;

    private HashMap<UUID, Long> uuid;
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
        this.uuid = new HashMap<>();
    }

    public boolean isWaiting(UUID uuid) {
        long time = System.currentTimeMillis();
        if (this.uuid.containsKey(uuid)) {
            check(time);
            return this.uuid.containsKey(uuid);
        }
        if (this.uuid.size() >= safeCapacity)
            check(time);
        return false;
    }

    public long inQue(UUID uuid) {
        Long l = this.uuid.get(uuid);
        if (l != null)
            return  l;
        return 0;
    }


    public boolean add(UUID uuid) {
        if (isWaiting(uuid))
            return false;
        this.uuid.put(uuid ,System.currentTimeMillis() );
        this.uuidToRemove.add(uuid);
        this.uuidExpiredTime.add(System.currentTimeMillis() + waitTill);
        return true;
    }

    public void check(long time) {
        long timer = uuidExpiredTime.peek();
        if (uuid.isEmpty())
            return ;
        while (timer <= time) {
            this.uuid.remove(uuidToRemove.poll());
            uuidExpiredTime.remove();
            if (this.uuid.isEmpty())
                timer = Long.MAX_VALUE;
        }
    }
}
