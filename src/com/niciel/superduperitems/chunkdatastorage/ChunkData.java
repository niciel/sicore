package com.niciel.superduperitems.chunkdatastorage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.niciel.superduperitems.PlayerIterator;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.Tickable;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.utils.Dual;
import com.niciel.superduperitems.utils.Vector2int;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkData implements GsonSerializable {


    public final PlayerIterator playersInRange = new PlayerIterator();

    private WeakReference<ChunkWorldManager> manager;

    private Vector2int position;
    private ArrayList<IChunkTicking> ticking;
    private ArrayList<IChunkObject> objects;
    private boolean changed;

    private boolean enabled = false;

    public ChunkData() {
        ticking = new ArrayList<>();
        objects = new ArrayList<>();
    }

    public ChunkData(Vector2int v) {
        position = v;
        ticking = new ArrayList<>();
        objects = new ArrayList<>();
    }

    private  BukkitTask task;

    public void addTickListener(IChunkTicking o) {

        if (task == null)
            this.task = Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance , () -> {
                ticking.forEach(c -> c.onTick());
        } ,1l,1l);
    }

    protected void enableChunk(ChunkWorldManager manager) {
        this.manager = new WeakReference<>(manager);
        objects.forEach(c-> {
            c.enable(this);
            if (c instanceof IChunkTicking)
                ticking.add((IChunkTicking) c);
        });
        enabled = true;
    }

    public void addElement(IChunkObject o) {
        if (contains(o.getUUID()))
            return;
        objects.add(o);
        if (enabled) {
            o.enable(this);
            if (o instanceof IChunkTicking)
                ticking.add((IChunkTicking) o);
        }
        setChanged();
    }

    protected void removeTicking(UUID uuid) {
        for (int i = 0 ; i < objects.size() ; i ++) {
            if (((IChunkObject) ticking.get(i)).getUUID().equals(uuid)){
                ticking.remove(i);
                return;
            }
        }
    }

    public boolean remove(UUID uuid) {
        for (int i = 0 ; i < objects.size() ; i ++) {
            if (objects.get(i).getUUID().compareTo(uuid) == 0) {
                IChunkObject o = objects.get(i);
                removeTicking(o.getUUID());
                o.disable(this);
                getManager().removeBlocks(o.getUUID());
                objects.remove(i);
                setChanged();
                return true;
            }
        }
        return false;
    }

    public ChunkWorldManager getManager() {
        return this.manager.get();
    }

    public boolean contains(UUID uuid) {
        for (IChunkObject o : objects) {
            if (o.getUUID().compareTo(uuid) == 0)
                return true;
        }
        return false;
    }


    public void setChanged() {
        this.changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public void enterViewDistance(Player p , IChunkPlayerData d) {
        playersInRange.add(p);
    }

    public void exitDistance(Player p , IChunkPlayerData d) {
        playersInRange.remove(p);
    }

    public <T extends IChunkObject> T getChunkObject(Class<T> clazz) {
        for (IChunkObject c : objects) {
            if (clazz.isAssignableFrom(c.getClass()))
                return (T) c;
        }
        return null;
    }

    public List<IChunkObject> getObjects() {
        return objects;
    }

    public void disableChunk() {
        for (IChunkObject o : objects)
            o.disable(this);
        if (task != null) {
            if (task.isCancelled() == false)
                task.cancel();
        }
        enabled = false;
    }

    public Vector2int getPosition() {
        return position;
    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("class" , ChunkData.class.getName());
        o.addProperty("x" , position.x);
        o.addProperty("y" , position.y);
        JsonArray array = new JsonArray();
        for (IChunkObject cd : objects) {
            array.add(GsonManager.toJsonTree(cd));
        }
        o.add("objects" , array);
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        int x,y;
        x = o.get("x").getAsInt();
        y = o.get("y").getAsInt();
        this.position = new Vector2int(x,y);
        JsonArray array = o.get("objects").getAsJsonArray();
        objects = new ArrayList<>();
        array.forEach( e-> {
            objects.add((IChunkObject) SDIPlugin.instance.getGson().fromJson(e , GsonSerializable.class));
        });

    }
}
