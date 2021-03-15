package com.niciel.superduperitems.commandGui.helpers;

import com.niciel.superduperitems.commandGui.GuiCommandArgs;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SimpleButtonGui implements GuiCommandArgs {

    private Consumer<Player> consumer;

    public SimpleButtonGui(Consumer<Player> pl ) {
        this.consumer = pl;
    }


    @Override
    public void onCommand(Player p, String[] args , int deep) {
        consumer.accept(p);
    }


}
