package com.niciel.superduperitems.fakeArmorstands;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.PlayerIterator;
import com.niciel.superduperitems.SDIPlugin;

import com.niciel.superduperitems.utils.SpigotUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ArmorStandModel implements GsonSerializable  {

    private static FakeArmorStandManager manager = SDIPlugin.instance.getManager(FakeArmorStandManager.class);
    private UUID uuid;
    protected List<Vector> orgin;

    protected List<DataModelBlock> blocks;
    protected List<FakeArmorStand_v1_15_R1> armorstands;
    protected boolean registered;
    protected Vector position;
    private Consumer<ArmorStandInteractAction> onUse;
    private float yaw ;
    private String world;
    private PlayerIterator players = new PlayerIterator();


    protected IModelBehaviour behaviour;


    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        JsonArray array = new JsonArray();
        for (Vector v : orgin)
            array.add(SDIPlugin.instance.getGson().toJsonTree(v));
        o.add("orgin" , array);
        array = new JsonArray();
        for (FakeArmorStand_v1_15_R1 f : armorstands)
            array.add(GsonManager.toJsonTree(f));
        o.add("armorstands" , array);
        o.addProperty("world" , world);
        o.addProperty("yaw" , yaw);
        if (onUse != null && GsonSerializable.class.isAssignableFrom(onUse.getClass())) {
            o.add("onuse" , GsonManager.toJsonTree(onUse));
        }
        else
            o.add("onuse" , new JsonNull());
        o.add("position" , GsonManager.toJsonTree(position));
        o.addProperty("uuid" , uuid.toString());

        array= new JsonArray();
        for (DataModelBlock b : blocks) {
            array.add(GsonManager.toJsonTree(b));
        }
        o.add("blocks" , array);



        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        JsonArray array = o.get("orgin").getAsJsonArray();
        array.forEach( e-> orgin.add((Vector) SDIPlugin.instance.getGson().fromJson(e , Vector.class)));
        array = o.get("armorstands").getAsJsonArray();
        array.forEach( e-> armorstands.add((FakeArmorStand_v1_15_R1) SDIPlugin.instance.getGson().fromJson(e , GsonSerializable.class)));
        world = o.get("world").getAsString();
        yaw = o.get("yaw").getAsFloat();
        position = (Vector) GsonManager.fromJson(o.get("position") ,Vector.class);
        if (! o.has("uuid"))
            this.uuid = UUID.randomUUID();
        else
            this.uuid = UUID.fromString(o.get("uuid").getAsString());

        array = o.get("blocks").getAsJsonArray();
        array.forEach(c-> blocks.add((DataModelBlock) SDIPlugin.instance.getGson().fromJson(c , GsonSerializable.class)));


//        TODO onuse
    }

    public void setPosition(Vector v) {
        this.position = v;
        for (int i = 0 ; i < armorstands.size() ; i++) {
            armorstands.get(i).setPosition(orgin.get(i).clone().add(v));
        }
    }

    public void setPositionAndRotation(Vector v , float yaw) {
        this.position = v;
        for (int i = 0 ; i < armorstands.size() ; i++) {
            armorstands.get(i).setPosition(orgin.get(i).clone().add(v));
        }
    }

    public ArmorStandModel(List<Vector> orgin, List<FakeArmorStand_v1_15_R1> armorstands, List<DataModelBlock> blocks , Vector position, float yaw, String world) {
        this.orgin = orgin;
        this.armorstands = armorstands;
        this.position = position;
        this.onUse = onUse;
        this.blocks = blocks;
        this.yaw = yaw;
        this.world = world;
        this.registered= false;
        this.players = new PlayerIterator();
        this.blocks = new ArrayList<>();
        this.uuid = UUID.randomUUID();
    }

    public ArmorStandModel() {
        orgin = new ArrayList<>();
        armorstands = new ArrayList<>();
        registered = false;
        this.players = new PlayerIterator();
        position = new Vector(0,0,0);
        yaw = 0;
        this.uuid = UUID.randomUUID();
        world = "";
        this.blocks = new ArrayList<>();
    }


    public float getYaw() {
        return yaw;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void register(Vector position , float yaw ) {
        if (registered)
            return;
        this.position = position;
        armorstands.forEach(a -> a.setPosition(a.getPosition().add(position)));
        armorstands.forEach(a-> {
            if (a.getClickCallBack() == null)
                a.setClickCallBack(onUse);
        });
        registered = true;
        manager.register(armorstands);
    }


    public void proveRegister(boolean flag) {
        if (flag)
            registered = true;
    }

    public void move(Vector position , float yaw ) {
        Vector newPosition;
        if (this.yaw != yaw) {
            float dif = this.yaw - yaw;
            FakeArmorStand_v1_15_R1 f;
            double fYaw;
            for (int i = 0 ; i <orgin.size() ; i++) {
                f = armorstands.get(i);
                newPosition = orgin.get(i).clone().rotateAroundY(SpigotUtils.DegToRand*yaw);
                f.setPosition(newPosition.add(position));
                fYaw = f.getHeadPose().getY();
                fYaw = fYaw+dif;
                f.setHeadPose(f.getHeadPose().clone().setY(fYaw));
                f.sendEntityMetaData(false);
            }
            this.yaw = yaw;
        }
        else {
            for (int i = 0 ; i <orgin.size() ; i++) {
                newPosition = orgin.get(i).clone().add(position);
                armorstands.get(i).setPosition(newPosition.add(position));
            }
        }
        this.position = position;
    }


    public ArmorStandModel clone() {
        ArmorStandModel model = new ArmorStandModel();
        model.uuid = uuid;
        model.position = this.position.clone();
        model.world = this.world;
        model.yaw = this.yaw;
        if (this.behaviour == null)
            model.behaviour = null;
        else
            model.behaviour = this.behaviour.clone();
        this.armorstands.forEach(a -> model.armorstands.add(a.clone()));
        this.orgin.forEach( v -> model.orgin.add(v.clone()));
        this.blocks.forEach( d-> model.blocks.add(d.clone()));
        return model;
    }

    public String getWorldName() {
        return world;
    }

    public Vector getPosition() {
        return position.clone();
    }

    public void unregister() {
        manager.unregister(armorstands);
        registered = false;
    }

    public PlayerIterator getPlayers() {
        return players;
    }

    public void setPlayers(PlayerIterator itr) {
        players = itr;
        for (FakeArmorStand_v1_15_R1 s : armorstands)
            s.setPlayerCollection(itr);
    }


    public void setBlocks(World w) {
        Vector v ;
        for (DataModelBlock d : blocks) {
            v= getPosition().add(d.position);
            Block b = w.getBlockAt(new Location(w ,v.getX() , v.getY() , v.getZ()));
            b.setBlockData(d.blockData);
        }
    }

}
