package com.niciel.superduperitems.commandGui;

import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.managers.SimpleCommandInfo;
import com.niciel.superduperitems.utils.SpigotCharTableUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.*;


@SimpleCommandInfo(usage = "blabla" , description = "blabla" , aliases = "guicommand" , command = "guicommand")
public class GuiCommandManager implements IManager , CommandExecutor , Listener , TabCompleter {

    private static HashMap<String , WeakReference<CommandPointer>> stringIDtoPointer;

    private static Set<String> usedIDS = new HashSet<String>();
    private static String command = "guicommand";
    private static boolean showStaticPointers = false;
    private static String permissionToForceGC = "forceGcPermission";
    private CommandPointer forceGCCommandPointer;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:M:dd - hh:mm:ss");

    @Override
    public void onEnable() {
        stringIDtoPointer = new HashMap<>();

        forceGCCommandPointer = registerCommandPointer(new SimpleButtonGui(p-> {
            if (p.isOp()) {
                p.sendMessage("starting GC");
                System.gc();
                p.sendMessage("GC ends");
            }
        }));
    }

    public static void forceGCMethod(Player p , String msg) {
        if (p.hasPermission(permissionToForceGC)) {
            System.gc();
            p.sendMessage("forced :(");
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            if (commandSender.isOp() == false) {
                return false;
            }
            commandSender.sendMessage("zarejestrowano: " + stringIDtoPointer.size());
            commandSender.sendMessage("pokaz statyki: " + showStaticPointers);
            TextComponent tc ;
            TextComponent in ;
            CommandPointer pointer;
            for (Map.Entry<String, WeakReference<CommandPointer>> e : stringIDtoPointer.entrySet()) {
                if (commandSender instanceof  Player ) {
                    String key = e.getKey();
                    WeakReference<CommandPointer> wr  = e.getValue();
                    if (wr.get() != null) {
                        tc = new TextComponent("blad jak sie powtarza skontaktuj sie z kims :D");
                        tc.setColor(ChatColor.RED);
                    }
                    else {
                        pointer = wr.get();
                        tc = new TextComponent();
                        in = new TextComponent(pointer.stringID);
                        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , pointer.stringID));
                        tc.addExtra(in);
                        tc.addExtra(String.format(" " + simpleDateFormat.format(new Date(pointer.createTime))));
                        ((Player) commandSender).spigot().sendMessage(tc);
                    }
                }
            }
            if (commandSender instanceof  Player ) {
                tc = new TextComponent("[force GC]");
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , forceGCCommandPointer.getCommand()));
                tc.setColor(ChatColor.GREEN);
                ((Player) commandSender).spigot().sendMessage(tc);
            }
            return true;
        }


        Player p = (Player) commandSender;
        if (strings.length >= 1) {
            String rand = strings[0];
            CommandPointer c = getCommandPointer(rand);
            if (c == null) {
                commandSender.sendMessage("KOMENDA PRZESTARZALA !!! " + s + " stringargument " + rand + " wartosci '" + rand+"'");
                return true;
            }
            c.onCommand(p , strings , 1);
            return true;
        }
        return false;
    }


    public CommandPointer registerCommandPointer() {
        return registerCommandPointer(null);
    }

    public CommandPointer registerCommandPointer(GuiCommandArgs args) {
        String ids  = SpigotCharTableUtils.getNextRandomID(usedIDS);
        CommandPointer c = new CommandPointer(ids);
        stringIDtoPointer.put(ids , new WeakReference<>(c));
        c.setGuiCommand(args);
        return c;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command c, String s, String[] strings) {
        if ( (sender instanceof  Player)==false)
            return null;
        if (strings.length > 1) {
            CommandPointer p = getCommandPointer(strings[0]);
            if (p == null || p.compliter == null)
                return null;
            return p.compliter.onTabComplite((Player) sender , strings , 1);
        }
        return null;
    }

    public static String connectCommandArgs(String[] args , int startingInclude) {
        StringBuilder sb = new StringBuilder();
        if (args.length > startingInclude)
            sb.append(args[1]);
        for (int i = 2; i < args.length ; i++) {
            sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }



    public CommandPointer getCommandPointer(String id) {
        WeakReference<CommandPointer> p = stringIDtoPointer.get(id);;
        if (p != null) {
            return p.get();
        }
        return null;
    }



    public static String getCommand() {
        return command;
    }

    public void remove(@Nonnull  CommandPointer pointer) {
        if (stringIDtoPointer.remove(pointer.stringID) != null) {
            usedIDS.remove(pointer.stringID);
        }
    }




}
