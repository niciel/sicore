package com.niciel.superduperitems.fakeArmorstands;

import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class DefaultModelBehaviour implements IModelBehaviour {


    @Override
    public void onClick(Player p, FakeArmorStand_v1_15_R1 clicked) {

    }

    @Override
    public IModelBehaviour clone() {
        return new DefaultModelBehaviour();
    }



    @Override
    public void onBreak(BlockBreakEvent e) {

    }

    @Override
    public void onTick() {}

    @Override
    public boolean onEnable(ArmorStandChunkModel model) {
        return false;
    }

    @Override
    public void onDisable(ArmorStandChunkModel model) {

    }


}
