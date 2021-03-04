package com.niciel.superduperitems.customitems.components;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.cfg.Cfg;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;
import com.niciel.superduperitems.customitems.components.actions.ActionList;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class BlockDamageComponent implements ItemComponent {

    @ChatEditable(name = "actionSuccess")
    @Cfg(path = "actionSuccess")
    public ActionList success = new ActionList();

    @ChatEditable(name = "actionDeny")
    @Cfg(path = "actionDeny")
    public ActionList deny = new ActionList();

    @ChatEditable(name = "whitelist")
    @Cfg(path =  "isWhitelist")
    public boolean whiteListed = true;


    private EnumSet<Material> set;

    @Cfg(path = "materials")
    public List<Material> materials = new ArrayList<>();


    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        if (whiteListed) {
            if (! set.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                denyPass(e.getPlayer() , e.getBlock());
            }
        }
        else if (set.contains(e.getBlock().getType())) {
            e.setCancelled(true);
            denyPass(e.getPlayer() , e.getBlock());
        }
        successPass(e.getPlayer() , e.getBlock());
    }

    public void successPass(Player p , Block b) {
        if (success != null)
            success.pass(p,b);
    }


    public void denyPass(Player p , Block b) {
        if (deny != null)
            deny.pass(p,b);
    }



    @Override
    public void onEnable(CustomItem ci) {
        set = EnumSet.allOf(Material.class);

        for (Material m : materials)
            set.add(m);


        if (success != null)
            success.enable(Block.class , Player.class);
        if (deny != null)
            deny.enable(Block.class , Player.class);
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject e) {

    }


}
