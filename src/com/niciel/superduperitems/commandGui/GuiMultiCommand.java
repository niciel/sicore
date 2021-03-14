package com.niciel.superduperitems.commandGui;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.SpigotCharTableUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class GuiMultiCommand implements GuiCommandArgs , IGuiTabCompliter , IGuiCommandObject {

    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);
    private HashMap<String , GuiCommandArgs> commands = new HashMap<>();
    private HashMap<String , IGuiTabCompliter> tabCompiter = new HashMap<>();

    public GuiCommandArgs defaultCommand;
    public IGuiTabCompliter defaultTabCompleter;
    private HashSet<String> used = new HashSet<String>();

    private String command;

    /**
     * @param args
     * @return  full command id
     */
    public String register(GuiCommandArgs args) {
        String id = SpigotCharTableUtils.getNextRandomID(used);
        used.add(id);
        commands.put(id , args);
        if (args instanceof IGuiTabCompliter)
            tabCompiter.put(id , (IGuiTabCompliter) args);
        if (args instanceof IGuiCommandObject)
            ((IGuiCommandObject) args).init(command + " " + id);
        return command + " " +id;
    }

    public void register(String id , IGuiTabCompliter compliter) {
        if (commands.containsKey(id)) {
            tabCompiter.put(id,compliter);
        }
    }


    public void clear(){
        commands.clear();
        tabCompiter.clear();
    }

    public String getCommand() {
        return command;
    }

    public void remove(String id) {
        commands.remove(id);
        tabCompiter.remove(id);
        used.remove(id);
    }

    @Override
    public void onCommand(Player p, String[] args , int deep) {
        if (args.length > deep) {
            GuiCommandArgs a = commands.get((args[deep]));
            if (a != null) {
                a.onCommand(p,args,deep+1);
                return;
            }
            else {
//                TODO jesli nie znalazlo co wyslac
            }
        }
        if (defaultCommand != null)
            defaultCommand.onCommand(p,args,deep);
    }


    @Override
    public List<String> onTabComplite(Player sender, String[] args, int deep) {
        if (args.length > deep) {
            IGuiTabCompliter a = tabCompiter.get((args[deep]));
            if (a != null) {
                return a.onTabComplite(sender, args, deep + 1);
            }
        }
        if (defaultTabCompleter != null)
            return defaultTabCompleter.onTabComplite(sender, args ,deep);
        return null;
    }

    @Override
    public void init(String command) {
        this.command = command;
    }


}
