package com.niciel.superduperitems.fakeArmorstands;

import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public interface IModelBehaviour extends GsonSerializable , Cloneable {


    public void onClick(Player p , FakeArmorStand_v1_15_R1 clicked );

    public IModelBehaviour clone();

    public void onBreak(BlockBreakEvent e) ;

    public void onTick();

    public boolean onEnable(ArmorStandChunkModel model) ;
    public void onDisable(ArmorStandChunkModel model);

}

