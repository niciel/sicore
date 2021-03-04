package com.niciel.superduperitems.commandGui;

import org.bukkit.entity.Player;

import java.util.List;

public interface IGuiTabCompliter {

    public List<String> onTabComplite(Player sender , String[] args , int deep);

}
