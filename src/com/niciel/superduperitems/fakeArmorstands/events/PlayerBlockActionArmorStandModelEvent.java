package com.niciel.superduperitems.fakeArmorstands.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBlockActionArmorStandModelEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    public PlayerBlockActionArmorStandModelEvent(Player player, Block block, Type eventType, Event eventHandled) {
        this.player = player;
        this.block = block;
        this.eventType = eventType;
        this.eventHandled = eventHandled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public final Player player;
    public final Block block;
    public final Type eventType;
    public final Event eventHandled;




    public enum Type {
        BREAK,
        DAMAGE,
        INTERACT;
    }
}
