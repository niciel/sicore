package com.niciel.superduperitems.fakeArmorstands.events;

import com.niciel.superduperitems.fakeArmorstands.ArmorStandModel;
import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInteractArmorStandModel extends Event {

    private static HandlerList handlers = new HandlerList();

    public final FakeArmorStand_v1_15_R1 armorstand;
    public final ArmorStandModel model;
    public final Player whoClicked;

    public PlayerInteractArmorStandModel(FakeArmorStand_v1_15_R1 armorstand, ArmorStandModel model, Player whoClicked) {
        this.armorstand = armorstand;
        this.model = model;
        this.whoClicked = whoClicked;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
