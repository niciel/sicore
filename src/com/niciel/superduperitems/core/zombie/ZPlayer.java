package com.niciel.superduperitems.core.zombie;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.Tickable;
import com.niciel.superduperitems.chunkdatastorage.IChunkPlayerData;
import com.niciel.superduperitems.utils.Vector2int;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZPlayer implements Tickable , IGunPlayer , IChunkPlayerData {

    private static SDIPlugin plugin = SDIPlugin.instance;

    private UUID uuid;
    public int killedZombie;
    public int killedPlayers;


    private boolean isReloading;
    private int reloadTick;
    private int takenAmmunition;
    private GunComponent reloadCallBack;

    public boolean drop;

    public WeakReference<Player> player;
    private List<Tickable> ticking = new ArrayList<>();


    public ZPlayer(UUID uuid) {
        this.uuid = uuid;
        Player p = Bukkit.getPlayer(uuid);
        if (p != null)
            player = new WeakReference<>(p);
    }


    public boolean isReloading() {
        return isReloading;
    }

    public void beginReload(GunComponent gc , int ammunition) {
        if (isReloading())
            return;
        reloadCallBack = gc;
        reloadTick = 0;
        isReloading = true;
        takenAmmunition = ammunition;
    }

    public void cancelReloading() {
        isReloading = false;
    }

    int shootTick=0;

    @Override
    public int getLastShootTick() {
        return shootTick;
    }

    @Override
    public void setLastShootTick(int i) {
        shootTick = i;
    }

    public void removeTickable(UUID uuid) {
        for (int i  = 0 ; i < ticking.size() ; i++) {
            if (ticking.get(i).getUUID().equals(uuid)) {
                ticking.remove(i);
                return ;
            }
        }
    }

    public Tickable getTicking(UUID uuid) {
        for (int i  = 0 ; i < ticking.size() ; i++) {
            if (ticking.get(i).getUUID().equals(uuid)) {
                return ticking.get(i);
            }
        }
        return null;
    }

    public void addTickable(Tickable t) {
        ticking.add(t);
    }

    private int reloadTickTimer = 10;

    @Override
    public void onTick() {
        if (isReloading()) {
            if (plugin.tick%reloadTickTimer == 0) {
                reloadCallBack.reloadTick(this , takenAmmunition , reloadTick);
                reloadTick += reloadTickTimer;
            }
        }
        for (Tickable t : ticking)
            t.onTick();
        LivingEntity e ;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }


    public Player getPlayer() {
        return player.get();
    }

    @Override
    public Location getLocation() {
        return player.get().getLocation();
    }

    private Vector2int previus;

    @Override
    public Vector2int getPreviusChunk() {
        return previus;
    }

    @Override
    public void move(Vector2int newPosition) {
        previus = newPosition;
    }

    @Override
    public int viewDistance() {
        return 3;
    }
}
