package com.niciel.superduperitems.chunkdatastorage;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface ICustomBlock {


    public void onBreak(BlockBreakEvent e ) ;

    public void onBlockDamage(BlockDamageEvent e );

    public void onBlockInteract(PlayerInteractEvent e );


}
