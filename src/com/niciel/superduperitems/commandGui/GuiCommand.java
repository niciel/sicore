package com.niciel.superduperitems.commandGui;

import com.niciel.superduperitems.utils.SpigotUtils;
import org.bukkit.entity.Player;

public abstract class GuiCommand implements GuiCommandArgs {

    public abstract void execute(Player p, String left);

    @Override
    public void onCommand(Player p, String[] args, int deep) {
        execute(p , SpigotUtils.connectStringArgs(args , deep));
    }
}
