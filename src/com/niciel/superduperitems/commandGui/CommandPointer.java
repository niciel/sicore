package com.niciel.superduperitems.commandGui;

import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;


public class CommandPointer {


    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    public final GuiCommandArgs guiCommand;
    public IGuiTabCompliter compliter;
    public final String stringID;
    public final long createTime;



    protected CommandPointer(String command , GuiCommandArgs args) {
        createTime = System.currentTimeMillis();
        stringID = command;
        this.guiCommand = args;
    }

    public void onCommand(Player sender ,String[] args , int deep) {
        guiCommand.onCommand(sender,args,deep);
    }

    @Override
    public String toString() {
        return GuiCommandManager.getCommand() + " " + stringID;
    }

    public String getCommand() {
        return "/" + GuiCommandManager.getCommand() + " " + stringID;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  CommandPointer ) {
            return stringID.contentEquals( ((CommandPointer) o).stringID);
        }
        if ( o instanceof String)
            return stringID.contentEquals((String) o);
        return false;
    }


    @Override
    public int hashCode() {
        return stringID.hashCode();
    }

}
