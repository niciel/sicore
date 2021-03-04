package com.niciel.superduperitems;

import org.bukkit.entity.Player;

public interface IPlayerIteratorConsumer {

    public void accept(Player p);

    public boolean validate();

}
