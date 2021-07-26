package com.niciel.superduperitems.utils;


import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SimpleCommand extends BukkitCommand {

    private CommandExecutor executor;
    private TabCompleter tabCompliter;
    public static SimpleCommandMap simpleCommandMap;

    static {

        Object craftServe ;
        Class craftServeClass;
        Method getSimpleCommandMap;

        try {
            //import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
            craftServeClass = ReflectionUtils.getRefClass("{cb}.CraftServer").getRealClass();
            craftServe = craftServeClass.cast(Bukkit.getServer());

            getSimpleCommandMap = craftServeClass.getDeclaredMethod("getCommandMap");
            simpleCommandMap = (SimpleCommandMap) getSimpleCommandMap.invoke(craftServe);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
//        TODO
    }

    public SimpleCommand(CommandExecutor ex ,String command, String description, String usageMessage, List<String> aliases) {
        super(command, description, usageMessage, aliases);
        executor = ex;
        if (ex instanceof  TabCompleter)
            tabCompliter = (TabCompleter) ex;
        simpleCommandMap.register(command, this);
        SDIPlugin.instance.logInfo("zarejestrowano komende " + command  + " klasa : " + ex.getClass().getName());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return executor.onCommand(commandSender,this , s, strings);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (tabCompliter != null)
            return tabCompliter.onTabComplete(sender , this , alias , args);
        return super.tabComplete(sender, alias, args);
    }

}
