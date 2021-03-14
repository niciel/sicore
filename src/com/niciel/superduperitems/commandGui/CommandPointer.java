package com.niciel.superduperitems.commandGui;

import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;


public class CommandPointer {


    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private GuiCommandArgs guiCommand;
    public IGuiTabCompliter compliter;
    public final String stringID;
    public final long createTime;



    protected CommandPointer(String command ) {
        createTime = System.currentTimeMillis();
        stringID = command;
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

    public GuiCommandArgs getGuiCommand() {
        return this.guiCommand;
    }

    public void setGuiCommand(GuiCommandArgs a) {
        this.guiCommand = a;
        if (a != null && a instanceof IGuiCommandObject)
            ((IGuiCommandObject) a).init(getCommand());
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
