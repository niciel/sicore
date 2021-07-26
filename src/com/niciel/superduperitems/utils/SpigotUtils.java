package com.niciel.superduperitems.utils;

import com.niciel.superduperitems.SDIPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Location;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.rmi.UnexpectedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SpigotUtils {


    private static Random random = new Random();

    private static Field ClassLoader_classes;
    private static java.util.Vector<Class> classes;
    private static int lastSize;
    private static List<Class> listClasses ;

    private HashMap<String , List<Class>> classAssignable;

    private static HashMap<String , Class> BUKKIT_Classes = new HashMap<>();
    private static HashMap<String , Class> NMS_Classes = new HashMap<>();
    public final static String NMS_prefix = "net.minecraft.server";
    public final static String BUKKIT_prefix = "org.bukkit.craftbukkit";

    private static String NMS_Version;
    private static String NMS_Class_Prefix;
    private static String BUKKIT_Class_Prefix;

    static {
        try {
            ClassLoader_classes = ClassLoader.class.getDeclaredField("classes");
            ClassLoader_classes.setAccessible(true);
            classes = (java.util.Vector<Class>) ClassLoader_classes.get(SDIPlugin.instance.getClass().getClassLoader());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        listClasses = new ArrayList<>();


        Class bukkitServerClass = Bukkit.getServer().getClass();

        try {

            Method getHandle = null;
            getHandle = bukkitServerClass.getDeclaredMethod("getHandle");
            Object handle = getHandle.invoke(Bukkit.getServer());
            Class nms_class = handle.getClass();
            String name = nms_class.getName();
            name = name.replaceAll("." +  nms_class.getSimpleName() , "");
            name = name.replaceAll(NMS_prefix + ".","");
            NMS_Version = name;
            NMS_Class_Prefix = NMS_prefix + "." + NMS_Version ;
            BUKKIT_Class_Prefix = BUKKIT_prefix + "." + NMS_Version;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static List<Class> getPluginClasses() {
        if (lastSize != classes.size()) {
            lastSize = classes.size();
            listClasses.clear();
            listClasses.addAll(classes);
        }
        return listClasses;
    }



    public static MethodHandle getMethodHandleGetter(Class clazz , String name) {
        try {
            Field f = getField(clazz,name);
            MethodHandles.Lookup look = MethodHandles.lookup();
            return look.unreflectGetter(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public static File getPluginJArFile(String name , File f) {
        try {
            ZipFile zip = new ZipFile(f);
            Enumeration<? extends ZipEntry> enumeration = zip.entries();
            ZipEntry entry;
            PluginDescriptionFile desc;
            while (enumeration.hasMoreElements()) {
                entry = enumeration.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith("plugin.yml")) {
                    desc = new PluginDescriptionFile(zip.getInputStream(entry));
                    if (desc.getName().contentEquals(name) || desc.getFullName().contentEquals(name))
                        return f;
                }
            }
        }
        catch (IOException | InvalidDescriptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File getPluginJArFile(String name) {
        File file = SDIPlugin.instance.getDataFolder().getParentFile();
        File out;
        for (File f : file.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                out = getPluginJArFile(name, f);
                if (out != null)
                    return out;
            }
        }
        return null;
    }

    private static Class NMS_CraftItemStack;
    private static Class NMS_ItemStack;
    private static Class NMS_NBTTagCompound;


    private static MethodHandle NMS_CraftItemStack_asNMSCopy;
    private static MethodHandle NMS_ItemStack_save;

    static {
        NMS_CraftItemStack = getBukkitClass("inventory.CraftItemStack");
        NMS_ItemStack = getNMSClass("ItemStack");
        NMS_NBTTagCompound = getNMSClass("NBTTagCompound");

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            Method m = NMS_CraftItemStack.getDeclaredMethod("asNMSCopy" , ItemStack.class);
            m.setAccessible(true);
            NMS_CraftItemStack_asNMSCopy = lookup.unreflect(m);
            m = NMS_ItemStack.getDeclaredMethod("save" , NMS_NBTTagCompound);
            m.setAccessible(true);
            NMS_ItemStack_save = lookup.unreflect(m);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object NMSfromItemStack(ItemStack is) {
        try {
            return NMS_CraftItemStack_asNMSCopy.invoke(is);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static BaseComponent[] fromItemStack(ItemStack is) {
        String s = null;
        //HoverEvent he=new HoverEvent(HoverEvent.Action.SHOW_ITEM,
        // new BaseComponent[]{
        // new TextComponent(CraftItemStack.asNMSCopy(ItemStack).save(new NBTTagCompound()).toString())}
        // );
        try {
            Object nms_Item = NMS_CraftItemStack_asNMSCopy.invoke(is);
            Object nbt = NMS_NBTTagCompound.newInstance();
            Object out = NMS_ItemStack_save.invoke(nms_Item , nbt);
            s = out.toString();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
        return new BaseComponent[] {
                new TextComponent(s)
        };

    }

    public static final double DegToRand = Math.PI/180;

    public static void rotateVectorDeg(Vector toRotate , Vector rotation) {
        toRotate.rotateAroundX(rotation.getX()*DegToRand);
        toRotate.rotateAroundY(-rotation.getY()*DegToRand);
        toRotate.rotateAroundZ(rotation.getZ()*DegToRand);
    }


    private static Class class_CraftPlayer = getBukkitClass("entity.CraftPlayer");
    private static Method method_CraftPlayer_getHandle;
    private static Class class_EntityPlayer = getNMSClass("EntityPlayer");
    private static Field field_EntityPlayer_playerConnection;
    private static Class class_PlayerConnection = getNMSClass("PlayerConnection");
    private static Method method_PlayerConnection_sendPacket;
    private static Class class_Packet = getNMSClass("Packet");


    static {
        try {
            method_CraftPlayer_getHandle = class_CraftPlayer.getDeclaredMethod("getHandle");
            field_EntityPlayer_playerConnection = class_EntityPlayer.getDeclaredField("playerConnection");
            method_PlayerConnection_sendPacket = class_PlayerConnection.getDeclaredMethod("sendPacket" , class_Packet);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static Method getMethod(Class clazz , String name , Class... type)  {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(name , type);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        m.setAccessible(true);
        return m;
    }

    public static Field getField(Class clazz , String name)  {
        Field f = null;
        try {
            f = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        return f;
    }

    public static void sendPacket(Player p , Object packet) {
        try {
            Object entity = method_CraftPlayer_getHandle.invoke(p);
            Object connection = field_EntityPlayer_playerConnection.get(entity);
            method_PlayerConnection_sendPacket.invoke(connection , packet);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // unused
    /*
        public static void sendBlockCrackAnimation(Player p, int ids , BlockVector vec , int level) {
        sendPacket(p , new PacketPlayOutBlockBreakAnimation(ids, new BlockPosition(vec.getBlockX() , vec.getBlockY() , vec.getBlockZ()) , level));
    }
     */


    public static Connection connectToDatabase(File file) throws UnexpectedException {
        String databaseFile = file.getAbsolutePath().replace("\\","/");
        if (! file.getName().endsWith(".db")) {
            throw new UnexpectedException("nazwa nie konczy sie na .db");
        }
        File parent = file.getParentFile();
        if (! parent.exists()) {
            throw new  UnexpectedException("najpierw stworz folder zawierajacy baze");
        }
        String url = "jdbc:sqlite:" + databaseFile;
        Connection con;
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnexpectedException("nie udalo sie :(");
        }
        return con;
    }

    public static String connectStringArgs(String[] args , int starting) {
        StringBuilder sb = new StringBuilder();
        for (int i = starting ; i < args.length ; i++) {
            sb.append(args[i]);
            if (i+1 < args.length)
                sb.append(" ");
        }
        return sb.toString();
    }

    public static Class getNMSClass(String className) {
        Class clazz;
        clazz = NMS_Classes.get(className);
        if (clazz != null)
            return clazz;
        try {
            clazz = Class.forName(NMS_Class_Prefix + "." + className);
            NMS_Classes.put(className , clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static Class getBukkitClass(String className) {
        Class clazz;
        clazz = BUKKIT_Classes.get(className);
        if (clazz != null)
            return clazz;
        try {
            clazz = Class.forName(BUKKIT_Class_Prefix + "." + className);
            BUKKIT_Classes.put(className , clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Material[] materials = Material.values();


    public static List<String> findClosest(String material , int max) {
        String[] list = new String[max];
        int power[] = new int[max];
        for (int i = 0 ; i < power.length ; i ++)
            power[i] = Integer.MAX_VALUE;
        int currentDistance;

        int bigestPos = 0;
        int bigest = Integer.MAX_VALUE;

        for (Material m : materials) {
            currentDistance = StringUtils.getLevenshteinDistance(m.name().toLowerCase() , material);
            if (currentDistance < bigest) {
                list[bigestPos] = m.name().toLowerCase();
                power[bigestPos] = currentDistance;
                bigestPos = bigest(power);
                bigest = power[bigestPos];
            }
        }
        return Arrays.asList(list);
    }

    public static String fixStringLength(String input , int maxSize) {
        return StringUtils.left(input ,maxSize);
    }

    public static List<String> findClosest(Collection<String> collection , String name , int max) {
        String[] list = new String[max];
        int power[] = new int[max];
        for (int i = 0 ; i < power.length ; i ++)
            power[i] = Integer.MAX_VALUE;
        int currentDistance;

        int bigestPos = 0;
        int bigest = Integer.MAX_VALUE;

        for (String m : collection) {
            currentDistance = StringUtils.getLevenshteinDistance(m , name);
            if (currentDistance < bigest) {
                list[bigestPos] = m;
                power[bigestPos] = currentDistance;
                bigestPos = bigest(power);
                bigest = power[bigestPos];
            }
        }
        return Arrays.asList(list);
    }

    public static void forNerbayPlayers(Location loc ,double radius , Consumer<Player> p) {
        loc.getWorld().getPlayers().forEach(c -> {
            if (loc.distance(c.getLocation()) <= radius)
                p.accept(c);
        });
    }

    public static Player getFirstNerbayPlayer(Location loc , double d ) {
        for (Player p : loc.getWorld().getPlayers()) {
            if (loc.distance(p.getLocation()) <= d)
                return p;
        }
        return null;
    }

    public static int bigest(int[] array ) {
        int pos = 0;
        int value = Integer.MIN_VALUE;
        for (int i = 0 ; i < array.length ; i ++) {
            if (array[i] > value) {
                value = array[i];
                pos = i;
            }
        }
        return pos;
    }


    public static int getItemStackCount(Inventory inv , ItemStack is) {
        return getItemStackCount(inv , p -> p.isSimilar(is));
    }


    public static int getItemStackCount(Inventory inv , Predicate<ItemStack> is ) {
        int c = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null)
                continue;
            if (is.test(i))
                c += i.getAmount();
        }
        return c;
    }

    public static int removeItem(Inventory inv , Predicate<ItemStack> p , int max) {
        ItemStack[] cont = inv.getContents();
        int left = max;
        int x;
        int removed = 0;
        for (int i = 0 ; i < cont.length ; i++) {
            if (cont[i] == null)
                continue;
            if (p.test(cont[i]) == false) {
                continue;
            }
            x = cont[i].getAmount();
            if (x < left) {
                inv.setItem(i , null);
                left -= x;
                removed += x;
            }
            else if (x == left) {
                inv.setItem(i , null);
                removed += x;
                break;
            }
            else {
                removed += left;
                ItemStack is = cont[i];
                is.setAmount(x-left);
                inv.setItem(i , is);
                break;
            }
        }
        return removed;
    }

    public static boolean removeItemExactStack(Inventory inv , Predicate<ItemStack> p, int toRemove) {
        ItemStack[] cont = inv.getContents();
        int left = toRemove;
        if (getItemStackCount(inv , p) < toRemove)
            return false;
        int x;
        for (int i = 0 ; i < cont.length ; i++) {
            if (cont[i] == null)
                continue;
            if (p.test(cont[i])) {
                x = left - cont[i].getAmount();
                if (x > 0) {
                    left -= cont[i].getAmount();
                    cont[i] = null;
                }
                else if (x < 0) {
                    cont[i].setAmount( cont[i].getAmount() - left);
                    inv.setContents(cont);
                    return true;
                }
                else {
                    cont[i] = null;
                    inv.setContents(cont);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeItemExactStack(Inventory inv , ItemStack is , int toRemove ) {
        return removeItemExactStack(inv , p-> p.isSimilar(is) , toRemove);
    }


    /**
     *
     * @param b wartosc bitowa
     * @param position pozycja bita od 0 (liczac od prawej)
     * @return prawda jesli bit na pozycji @position jest w wartosci wysokiej (1) false gdy bit jest rowny 0
     */
    public static boolean bitValue(byte b , int position) {
        byte out = getByte(position);
        b = (byte) (b & out);
        return (out == b ? true :false);


    }

    /**
     *
     * @param i numer pozycji po przecinku (dla i=1: 123.X23456 , dla i = 2: 123.1X3456)
     * @param d
     * @return
     */
    public static byte numerAt(int i , double d) {
        double value = Math.abs(((double )(int )(d*Math.pow(10 ,i))));
        double upper = ((double) ((int) value/10))*10;
        d = value-upper;
        return (byte) d;
    }

    public static byte numerAt(int i , float d) {
        float value = Math.abs(((float )(int )(d*Math.pow(10 ,i))));
        float upper = ((float) ((int) value/10))*10;
        d = value-upper;
        return (byte) d;
    }

    public enum RoundNumber {

        HALF_UP {

            @Override
            public double round(int places, double value) {
                double move = Math.pow(10,places);
                double v = value*move;
                v = (int) v;
                byte position = numerAt(places+1 , value);
                if (position >=5) {
                    v += Math.signum(value);
                }
                v = v/move;
                return v;
            }
        };

        public double round(int places , double value) {
            return 0;
        }

    }

    /**
     *
     * @param i poycja bitu ktory ma byc w pozycji wysokiej
     * @return zwraca wartsc byte dla ktorej wszystkie bity sa w stanie niskim poza bitem i
     */
    public static byte getByte(int i) {
        if (i > 8)
            return (byte) 0xff;
        return (byte) (0x1 << i);
    }

//import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;

}
