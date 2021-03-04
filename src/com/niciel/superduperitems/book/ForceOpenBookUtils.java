package com.niciel.superduperitems.book;

import com.niciel.superduperitems.utils.ReflectionUtils;

import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ForceOpenBookUtils {


// linia 19 znakow


    private static Object NMS_ENUM_MAIN_HAND;
    private static Object NMS_ENUM_OFF_HAND;
    private static Constructor NMS_PACKET_SEND_OPEN_BOOK;

    private static Class CraftPlayer;


    private static Class EntityPlayer;
    private static MethodHandle GetHandle;
    private static MethodHandle GetPlayerConnectionField;
    private static MethodHandle SendPacket;


    static {
        Class NMSPacketClass = ReflectionUtils.getNmsClass("PacketPlayOutOpenBook");
        Class NMSEnumHandClass = ReflectionUtils.getNmsClass("EnumHand");
        Class NMSPlayerConnection = ReflectionUtils.getNmsClass("PlayerConnection");


        EntityPlayer = ReflectionUtils.getNmsClass("EntityPlayer");
        CraftPlayer = ReflectionUtils.getBukkitClass("entity.CraftPlayer");


        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class Packet = ReflectionUtils.getNmsClass("Packet");

        try {
            GetHandle = lookup.unreflect(CraftPlayer.getDeclaredMethod("getHandle"));
            GetPlayerConnectionField = lookup.unreflectGetter(EntityPlayer.getField("playerConnection"));

            SendPacket = lookup.unreflect(NMSPlayerConnection.getMethod("sendPacket", Packet));
            NMS_PACKET_SEND_OPEN_BOOK = NMSPacketClass.getDeclaredConstructor(NMSEnumHandClass);

        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        NMS_ENUM_MAIN_HAND = Enum.valueOf(NMSEnumHandClass , "MAIN_HAND");
        NMS_ENUM_OFF_HAND = Enum.valueOf(NMSEnumHandClass , "OFF_HAND");
    }

    public static void forceOpenBook(Player p , boolean mainHand ) {


        Object packet;
        try {
            if (mainHand)
                packet = NMS_PACKET_SEND_OPEN_BOOK.newInstance(NMS_ENUM_MAIN_HAND);
            else
                packet = NMS_PACKET_SEND_OPEN_BOOK.newInstance(NMS_ENUM_OFF_HAND);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return ;
        }
        sendPacket(p , packet);
    }




    public static void sendPacket(Player p , Object o) {
        Object playerConnection;
        Object entityPlayer = null;

        try {
            entityPlayer = GetHandle.invoke(p);
            playerConnection = GetPlayerConnectionField.invoke(entityPlayer);
            SendPacket.invoke(playerConnection , o);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
