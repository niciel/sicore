package com.niciel.superduperitems.fakeArmorstands;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.chunkdatastorage.*;
import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;

import org.bukkit.GameMode;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class ArmorStandChunkModel implements IChunkObject , ICustomBlock , IChunkTicking {



    protected ArmorStandModel model;

    @GsonSimpleSerialize
    protected Vector inWorldPosition;
    @GsonSimpleSerialize
    protected String orginalModelID;
    @GsonSimpleSerialize
    protected UUID uuid;
    @GsonSimpleSerialize
    protected float rotation;

    private WeakReference<FakeArmorStandManager> manager ;

    @GsonSimpleSerialize
    protected UUID playerOwner;
    @GsonSimpleSerialize
    protected long spawnTime;

    private boolean loaded;
    private static String permissionDestroyBlock = "modelmanager.destroymodel";




    @Override
    public void enable(ChunkData d) {
        manager = new WeakReference<>(SDIPlugin.instance.getManager(FakeArmorStandManager.class));
        ArmorStandModel orginalmodel = manager.get().getModel(orginalModelID);
        if (orginalmodel == null) {
            loaded = false;
            return;
        }
        model = manager.get().copyModel(orginalmodel , inWorldPosition.clone() , rotation);
        loaded=true;
        if (model.behaviour != null) {
            if (this.model.behaviour.onEnable(this) == false) {
                return;
            }
        }

        for (DataModelBlock v : model.blocks) {
            d.getManager().addBlock(getUUID() , v.position.clone().add(inWorldPosition) , this);
        }
        manager.get().register(model.armorstands);
        model.setPlayers(d.playersInRange);
        WeakReference<ArmorStandChunkModel> _instance = new WeakReference<>(this);
        for (FakeArmorStand_v1_15_R1 f : model.armorstands) {
            f.setClickCallBack(a -> {
                if (a.player.hasPermission(permissionDestroyBlock)) {
//              TODO niszczenie blokow wszystkich :D
                    if (SDIPlugin.instance.getManager(ChunkManager.class).getWorldManager(a.player.getWorld()).removeIChunkObject(_instance.get().getUUID())) {
                        a.player.sendMessage("poprawnie usunieto model");
                    }
                    else {
                        a.player.getPlayer().sendMessage("niepoprawnie usunietbo model !");
                    }
                }
            });
        }
        loaded=true;
    }

    @Override
    public void disable(ChunkData d) {
        if (loaded) {
            manager.get().unregister(model.armorstands);
            model.unregister();
            model.setPlayers(null);
            if (this.model.behaviour != null)
                this.model.behaviour.onDisable(this);
        }
        else {
        }
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }


    @Override
    public void onBreak(BlockBreakEvent e) {
        if (e.getPlayer().hasPermission(permissionDestroyBlock)) {
//            TODO niszczenie blokow wszystkich :D
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (SDIPlugin.instance.getManager(ChunkManager.class).getWorldManager(e.getPlayer().getWorld()).removeIChunkObject(this.getUUID())) {
                    e.getPlayer().sendMessage("poprawnie usunieto model");
                }
                else {
                    e.getPlayer().sendMessage("niepoprawnie usunietbo model !");
                }
                return ;
            }
        }

        if (this.model.behaviour != null) {
            this.model.behaviour.onBreak(e);
        }
        else {
            e.setCancelled(true);
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent e) {

    }

    @Override
    public void onBlockInteract(PlayerInteractEvent e) {

    }

    @Override
    public void onTick() {
        if (this.model.behaviour != null)
            this.model.behaviour.onTick();
    }
}
