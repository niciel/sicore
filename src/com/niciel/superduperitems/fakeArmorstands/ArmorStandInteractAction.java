package com.niciel.superduperitems.fakeArmorstands;

import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import org.bukkit.entity.Player;

public class ArmorStandInteractAction {


    public final Player player;
    public final FakeArmorStand_v1_15_R1 armorstand;

    public ArmorStandInteractAction(Player player, FakeArmorStand_v1_15_R1 armorstand) {
        this.player = player;
        this.armorstand = armorstand;
    }
}
