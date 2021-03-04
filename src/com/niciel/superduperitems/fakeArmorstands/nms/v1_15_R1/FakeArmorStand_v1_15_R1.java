package com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.IPlayerIteratorConsumer;
import com.niciel.superduperitems.fakeArmorstands.ArmorStandInteractAction;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.PlayerIterator;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.utils.SpigotUtils;
//import net.minecraft.server.v1_15_R1.*;
//import net.minecraft.server.v1_16_R3.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class FakeArmorStand_v1_15_R1 implements GsonSerializable {

    private static Class class_ItemStack = SpigotUtils.getNMSClass("ItemStack");

    private static Class class_World = SpigotUtils.getNMSClass("World");

    private static Field field_entity_dataWatcher;
    private static Method method_datawatcher_set;
    private static Class class_dataWatcher = SpigotUtils.getNMSClass("DataWatcher");
    private static MethodHandle method_IChatSerializer_a ;

    private static Class class_IChatBaseComponent = SpigotUtils.getNMSClass("IChatBaseComponent");
    private static MethodHandle field_get_ChatSerializer ;

    private static Class class_entity = SpigotUtils.getNMSClass("Entity");
    private  static Method method_entity_setPositionRaw = SpigotUtils.getMethod(class_entity , "setPositionRaw" , double.class , double.class , double.class);
    private static Method method_entityArmorStand_setFlag = SpigotUtils.getMethod(class_entity , "setFlag" , int.class , boolean.class);
    private static Object field_entity_az = SpigotUtils.getField(class_entity , "az");
    private static Object entity_aA = SpigotUtils.getField(class_entity , "aA");

    private static Class class_entityLiving = SpigotUtils.getNMSClass("EntityLiving");

    private static Class class_PacketPlayOutEntityMetadata = SpigotUtils.getNMSClass("PacketPlayOutEntityMetadata");

    private static Class class_vector3f = SpigotUtils.getNMSClass("Vector3f");
    private static Method field_vector3f_getX;
    private static Method field_vector3f_getY;
    private static Method field_vector3f_getZ;

    private static Constructor constructor_vector3f;
    private  static  Constructor constructor_PacketPlayOutEntityMetadata;

    private static Class class_packetPlayOutEntityDestroy = SpigotUtils.getNMSClass("PacketPlayOutEntityDestroy");
    private static Constructor constructor_packetPlayOutEntityDestroy;


    private static Class class_entityArmorStand = SpigotUtils.getNMSClass("EntityArmorStand");
    private static Constructor constructor_entityArmorStand;
    private static Method method_entityArmorStand_setHeadPose;
    private static Field field_entityArmorStand_headPose;
    private static Method method_entityArmorStand_getId;
    private static Method getMethod_entityArmorStand_setSlot = SpigotUtils.getMethod(class_entityArmorStand , "setSlot" ,
            SpigotUtils.getNMSClass("EnumItemSlot"),
            SpigotUtils.getNMSClass("ItemStack")
            );
    private static Method method_entityArmorStand_setNasePlate = SpigotUtils.getMethod(class_entityArmorStand,"setBasePlate",boolean.class);
    private static Method method_entityArmorStand_setMarker = SpigotUtils.getMethod(class_entityArmorStand , "setMarker" , boolean.class);

    private static Class class_PacketPlayOutSpawnEntityLiving = SpigotUtils.getNMSClass("PacketPlayOutSpawnEntityLiving");
    private static Constructor constructor_PacketPlayOutSpawnEntityLiving ;

    private static  Class class_PacketPlayOutEntityTeleport = SpigotUtils.getNMSClass("PacketPlayOutEntityTeleport");
    private static Constructor constructor_PacketPlayOutEntityTeleport ;


    private static Class class_PacketPlayOutEntityEquipment = SpigotUtils.getNMSClass("PacketPlayOutEntityEquipment");
    private static Constructor constructor_PacketPlayOutEntityEquipment;

    private static Field field_entity_yaw;

    private static Class class_PacketPlayOutEntity;
    private static Class class_PacketPlayOutEntityLook;
    private static Constructor constructor_PacketPlayOutEntityLook;





    static {

        try {
            field_entity_yaw = class_entity.getDeclaredField("yaw");
            constructor_vector3f = class_vector3f.getDeclaredConstructor(float.class , float.class , float.class);
            field_vector3f_getX = class_vector3f.getDeclaredMethod("getX");
            field_vector3f_getY = class_vector3f.getDeclaredMethod("getY");
            field_vector3f_getZ = class_vector3f.getDeclaredMethod("getZ");

            field_entity_dataWatcher = class_entity.getDeclaredField("datawatcher");
            field_entity_dataWatcher.setAccessible(true);
            constructor_PacketPlayOutEntityMetadata = class_PacketPlayOutEntityMetadata.getDeclaredConstructor(int.class, class_dataWatcher , boolean.class);
            method_datawatcher_set = SpigotUtils.getMethod(class_dataWatcher , "set" );
            field_get_ChatSerializer = SpigotUtils.getMethodHandleGetter(class_IChatBaseComponent , "ChatSerializer");
            method_IChatSerializer_a = MethodHandles.lookup().unreflect(SpigotUtils.getMethod(class_IChatBaseComponent, "a"));

            method_entityArmorStand_setHeadPose = class_entityArmorStand.getDeclaredMethod("setHeadPose" , class_vector3f);
            field_entityArmorStand_headPose = class_entityArmorStand.getDeclaredField("headPose");
            constructor_entityArmorStand = class_entityArmorStand.getDeclaredConstructor(class_World , double.class , double.class , double.class);
            method_entityArmorStand_getId = class_entity.getDeclaredMethod("getId");

            constructor_PacketPlayOutSpawnEntityLiving = class_PacketPlayOutSpawnEntityLiving.getDeclaredConstructor(class_entityLiving);

            constructor_PacketPlayOutEntityTeleport = class_PacketPlayOutEntityTeleport.getDeclaredConstructor(class_entity);

            constructor_PacketPlayOutEntityEquipment = class_PacketPlayOutEntityEquipment.getDeclaredConstructor(int.class , SpigotUtils.getNMSClass("EnumItemSlot") , class_ItemStack);

            constructor_packetPlayOutEntityDestroy = class_packetPlayOutEntityDestroy.getDeclaredConstructor(int[].class);



            class_PacketPlayOutEntity = SpigotUtils.getNMSClass("PacketPlayOutEntity");
            for (Class c : class_PacketPlayOutEntity.getDeclaredClasses()) {
                if (c.getSimpleName().contentEquals("PacketPlayOutEntityLook")) {
                    class_PacketPlayOutEntityLook = c;
                    break;
                }
            }
            constructor_PacketPlayOutEntityLook = class_PacketPlayOutEntityLook.getConstructor(int.class , byte.class,byte.class,int.class , boolean.class);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private UUID uuid;

    private int entityID;
    private Object armorstand;

    private Consumer<ArmorStandInteractAction> onUse;
    private PlayerIterator players = new PlayerIterator();
    private Vector position;
    private ItemStack items[] = new ItemStack[6];

    private boolean invisible;
    private boolean glowing;
    private boolean basePlate;
    private boolean marker;
    private float yaw;

    private String customName;

    public String getCustomName() {
        return customName;
    }


    public void setCustomName(String name ) {
        try {
            Object datawatcher = getDataWatcher();
            Object chatSerializer = field_get_ChatSerializer.invoke(null );
            Object otputString = method_IChatSerializer_a.invoke(chatSerializer , name);
            method_datawatcher_set.invoke(datawatcher , field_entity_az , otputString);
            //((DataWatcher ) getDataWatcher()).set((DataWatcherObject) field_entity_az, IChatBaseComponent.ChatSerializer.a(name));
            customName = customName;
            method_datawatcher_set.invoke(datawatcher , entity_aA , true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //((DataWatcher ) getDataWatcher()).set((DataWatcherObject ) entity_aA , true);
    }

    public FakeArmorStand_v1_15_R1() {

    }

    public FakeArmorStand_v1_15_R1(Vector v) {
        try {
            armorstand = constructor_entityArmorStand.newInstance(null , v.getX() , v.getY() , v.getZ());
            entityID = (int) method_entityArmorStand_getId.invoke(armorstand);
            setYaw(0);
            //TODO ??
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        position = v;
        uuid = UUID.randomUUID();
        setHeadPose(new Vector(0,0,0));
    }


    public boolean isMarker() {
        return marker;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
        try {
            method_entityArmorStand_setMarker.invoke(armorstand , marker);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean hasBasePlate() {
        return basePlate;
    }

    public void setBasePlate(boolean basePlate) {
        this.basePlate = basePlate;
        try {
            method_entityArmorStand_setNasePlate.invoke(armorstand,basePlate);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean flag) {
        glowing = flag;
        setFlag(6 , glowing);
    }

    public void setInvisible(boolean b) {
        invisible = b;
        setFlag(5 , b);
    }

    public void setPlayerCollection(PlayerIterator itr) {

        if (players != null) {
            players.forEach(p -> sendDespawn(p));
            PlayerIterator ptr = players;
            players = null;
            ptr.validate();
        }
        if (itr == null) {
            players = null;
            return;
        }
        players = itr;
        WeakReference<FakeArmorStand_v1_15_R1> _instance = new WeakReference<>(this);
        itr.addListener(new IPlayerIteratorConsumer() {
            @Override
            public void accept(Player p) {
                _instance.get().sendSpawn(p);
            }

            @Override
            public boolean validate() {
                if (_instance.get() == null || _instance.isEnqueued() || _instance.get().players == null)
                    return false;
                return true;
            }
        });
        itr.removeListener(new IPlayerIteratorConsumer() {
            @Override
            public void accept(Player p) {
                _instance.get().sendDespawn(p);
            }

            @Override
            public boolean validate() {
                if (_instance.get() == null || _instance.isEnqueued() || _instance.get().players == null)
                    return false;
                return true;
            }
        });
        itr.forEach(this::sendSpawn);
    }


    public void sendDespawn(Player p) {
        int[] ids = new int[]{entityID};
        Object packet = null;
        try {
            packet = constructor_packetPlayOutEntityDestroy.newInstance(ids);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
        SpigotUtils.sendPacket(p , packet);

    }

    public boolean isInvisible(){
        return invisible;
    }


    public Consumer<ArmorStandInteractAction> getClickCallBack() {
        return onUse;
    }

    public void setClickCallBack(Consumer<ArmorStandInteractAction> call) {
        onUse = call;
    }

    protected void setFlag(int flag , boolean value) {
        try {
            method_entityArmorStand_setFlag.invoke(armorstand, flag , value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public int getID() {
        return entityID;
    }


    public void onUse(Player p) {
        if (onUse != null)
            onUse.accept(new ArmorStandInteractAction(p , this));
    }

    public ItemStack getItem(EquipmentSlot type ) {
        return getItem(ItemSlotType.from(type));
    }

    public void setItem(EquipmentSlot slot , ItemStack is )
    {
        setItem(ItemSlotType.from(slot) , is);
    }

    public ItemStack getItem(ItemSlotType type ) {
        return items[type.slot];

    }

    public void kill() {
        forPlayers(p -> sendDespawn(p));
    }

    public void setItem(ItemSlotType type , ItemStack is) {
        items[type.slot] = is;
        forPlayers(p -> sendEquipment(p , type , is));
    }

    public void sendEquipment(Player p , ItemSlotType type , ItemStack is) {
        try {
            Object item = SpigotUtils.fromItemStack(is);
            Object packet = constructor_PacketPlayOutEntityEquipment.newInstance(entityID ,type.getEnumItemSlot() , SpigotUtils.NMSfromItemStack(is));
            SpigotUtils.sendPacket(p, packet);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



    public Object getDataWatcher() {
        try {
            return field_entity_dataWatcher.get(armorstand);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void forPlayers(Consumer<Player> p) {
        players.forEach( c -> p.accept(c));
    }

    public Vector getHeadPose() {
        try {
            Object o = field_entityArmorStand_headPose.get(armorstand);
            if (o == null)
                return new Vector(0,0,0);
            return to_Vector(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void setPosition(Vector v) {
        position = v;


        try {
            method_entity_setPositionRaw.invoke(armorstand , v.getX() , v.getY() , v.getZ());
            if (! players.isEmpty()) {
                Object packet = constructor_PacketPlayOutEntityTeleport.newInstance(armorstand);
                forPlayers(c -> SpigotUtils.sendPacket(c , packet));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /*class Entity
      public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
    f(d0, d1, d2);
    this.yaw = f;
    this.pitch = f1;
    Z();
  }
     */

    public void setYaw(float yaw) {
        try {
            field_entity_yaw.set(this.armorstand , yaw);
            this.yaw = yaw;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public float getYaw() {
        return yaw;
    }


    /**
     *
     * @param vector set a vector in deg (y yaw)
     */
    public void setHeadPose(Vector vector) {
        try {
            method_entityArmorStand_setHeadPose.invoke(armorstand , to_Vector3f(vector));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public PlayerIterator getPlayers() {
        return players;
    }



    public void sendSpawn(Player p) {
        try {
            Object o = constructor_PacketPlayOutSpawnEntityLiving.newInstance(armorstand);
            SpigotUtils.sendPacket(p ,o);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        for (ItemSlotType t : ItemSlotType.values())
            sendEquipment(p,t ,items[t.slot]);
        sendEntityMetaData(p , true);
    }

    public Vector getPosition() {
        return position;
    }



    public void sendEntityMetaData(Player p , boolean sendAll) {


        try {
            Object packet = constructor_PacketPlayOutEntityMetadata.newInstance(entityID , getDataWatcher() , sendAll);
            SpigotUtils.sendPacket(p , packet);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        float pitch = 90;

        //TODO entitylook
        //PacketPlayOutEntity.PacketPlayOutEntityLook look = new PacketPlayOutEntity.PacketPlayOutEntityLook(getID() ,(byte) ((int)yaw*256f/360) ,
        //        (byte) ( (int)pitch*256f/360) , false);

    }

    public void sendEntityMetaData(boolean sendAll) {
        setCustomName("nazwaaa");
        try {
            Object packet = constructor_PacketPlayOutEntityMetadata.newInstance(entityID , getDataWatcher() , sendAll);
            forPlayers(c -> SpigotUtils.sendPacket(c , packet));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



    public static Vector to_Vector(Object vector3f) {
        try {
            return new Vector(
                    (float) field_vector3f_getX.invoke(vector3f),
                    (float) field_vector3f_getY.invoke(vector3f),
                    (float) field_vector3f_getZ.invoke(vector3f)
        );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static Object to_Vector3f(Vector v) {
        try {
            return constructor_vector3f.newInstance((float) v.getX() , (float) v.getY() , (float) v.getZ());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Object[] enumItemSlot;

    static {
        Class clazz = SpigotUtils.getNMSClass("EnumItemSlot");
        Method m = SpigotUtils.getMethod(clazz , "values");
        try {
            enumItemSlot = (Object[]) m.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public FakeArmorStand_v1_15_R1 clone() {
        FakeArmorStand_v1_15_R1 fake = new FakeArmorStand_v1_15_R1(this.position.clone());
        fake.setInvisible(this.isInvisible());
        fake.setMarker(this.isMarker());
        fake.setGlowing(this.isGlowing());
        fake.setBasePlate(this.hasBasePlate());
        for (ItemSlotType t : ItemSlotType.values()) {
            if (this.getItem(t) == null)
                continue;
            fake.setItem(t , this.getItem(t).clone());
        }
        fake.setHeadPose(this.getHeadPose().clone());
        return fake;
    }

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("uuid" , uuid.toString());
        o.add("position" , SDIPlugin.instance.getGson().toJsonTree(position , Vector.class));
        o.addProperty("invisible" , invisible);
        o.addProperty("glowing" , glowing);
        o.addProperty("basePlate" , basePlate);
        o.addProperty("marker" , marker);
        o.addProperty("name" , getCustomName());
        o.add("headrotation" , SDIPlugin.instance.getGson().toJsonTree(getHeadPose() , Vector.class));
        JsonArray array = new JsonArray();
        for (int i = 0 ; i < items.length ;i++) {
            if (items[i] == null)
                array.add(new JsonNull());
            else
                array.add(SDIPlugin.instance.getGson().toJsonTree(items[i] , ItemStack.class));
        }
        o.add("items" , array);
        o.addProperty("yaw" , yaw );
        return o;
    }

    @Override
    public void deserialize(JsonObject o) {
        this.uuid = UUID.fromString(o.get("uuid").getAsString());
        Vector position = SDIPlugin.instance.getGson().fromJson(o.get("position") , Vector.class);
        float yaw = 0;
        if (o.has("yaw"))
            this.yaw = o.get("yaw").getAsFloat();
        else
            this.yaw = 0;
        try {
            armorstand = constructor_entityArmorStand.newInstance(null , position.getX() , position.getY() , position.getZ());
            entityID = (int) method_entityArmorStand_getId.invoke(armorstand);
            setYaw(yaw);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        this.position = position;

        if (o.has("name")) {
            setCustomName(o.get("name").getAsString());
        }

        setInvisible(o.get("invisible").getAsBoolean());
        setGlowing(o.get("glowing").getAsBoolean());
        setBasePlate( o.get("basePlate").getAsBoolean());
        setMarker(o.get("marker").getAsBoolean());
        JsonArray array = o.get("items").getAsJsonArray();
        setHeadPose(SDIPlugin.instance.getGson().fromJson(o.get("headrotation") , Vector.class));
        items = new ItemStack[6];
        for (int i = 0 ; i < array.size() ; i++) {
            if (array.get(i).isJsonNull())
                items[i] = null;
            else
                items[i] = SDIPlugin.instance.getGson().fromJson(array.get(i) , ItemStack.class);
        }
    }


    public enum ItemSlotType {
        MAIN_HAND (0 , EquipmentSlot.HAND),
        OFF_HAND(1, EquipmentSlot.OFF_HAND) ,
        BOOTS(2, EquipmentSlot.FEET) ,
        LEGGINS(3, EquipmentSlot.LEGS) ,
        CHEST (4, EquipmentSlot.CHEST) ,
        HEAD(5,EquipmentSlot.HEAD);

        private final int slot;
        private EquipmentSlot bukkit;

        ItemSlotType(int slot , EquipmentSlot sl) {
            this.slot = slot;
            this.bukkit = sl;
        }

        public static ItemSlotType from(EquipmentSlot slot ) {
            for (ItemSlotType i : ItemSlotType.values()) {
                if (i.bukkit == slot)
                    return i;
            }
            return ItemSlotType.HEAD;
        }

        public Object getEnumItemSlot() {
            return enumItemSlot[slot];
        }
    }

}
