package com.niciel.superduperitems;


import com.google.gson.GsonBuilder;
import com.niciel.superduperitems.chunkdatastorage.ChunkManager;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
//import com.niciel.superduperitems.core.old.CraftingPatternListener;
//import com.niciel.superduperitems.core.zombie.ZombieGameManager;
import com.niciel.superduperitems.customitems.ItemManager;
//import com.niciel.superduperitems.fakeArmorstands.*;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.gsonadapter.adapters.GsonVector;
import com.niciel.superduperitems.gsonadapter.adapters.GsonBlockSerializer;
import com.niciel.superduperitems.gsonadapter.adapters.GsonItemStackAdapter;
import com.niciel.superduperitems.gsonadapter.adapters.GsonSerializableAdapter;
import com.niciel.superduperitems.gsonadapter.adapters.GsonUUIDAdapter;
import com.niciel.superduperitems.inGameEditor.ChatEditorManager;
import com.niciel.superduperitems.inventory.InventoryManager;
import com.niciel.superduperitems.managers.SiJavaPlugin;
import com.niciel.superduperitems.particles.ParticleUtility;
import com.niciel.superduperitems.persistentHelper.PersistentSerializerManager;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;


public class SDIPlugin extends SiJavaPlugin implements Listener , CommandExecutor {

    public static SDIPlugin instance;
    private ItemManager itemManager ;
    private InventoryManager invManager ;
    private PersistentSerializerManager serializiationManager ;
    private GuiCommandManager guiCommandManager;
    //private MiningManager miningManager;
    //private ZombieGameManager zombieManager;
    //private FakeArmorStandManager fakeArmorStandManager;
    private ParticleUtility particleUtility;

    private ChatEditorManager editorManager;



    private ChunkManager chunkManager;

    public int tick = 0;

    private GsonManager gson;


    @Override
    public void onLoad() {
        super.onLoad();
        gson = new GsonManager(new GsonBuilder());
        gson.registerTypeAdapter(BlockData.class , new GsonBlockSerializer());
        gson.registerTypeAdapter(GsonSerializable.class , new GsonSerializableAdapter());
        gson.registerTypeAdapter(ItemStack.class , new GsonItemStackAdapter());
        gson.registerTypeAdapter(Vector.class , new GsonVector());
        gson.registerTypeAdapter(UUID.class , new GsonUUIDAdapter());
    }

    @Override
    public void onPluginEnable() {
        instance = this;
        itemManager = new ItemManager();
        invManager = new InventoryManager();
        serializiationManager = new PersistentSerializerManager();
        guiCommandManager = new GuiCommandManager();
        //miningManager = new MiningManager();
        //this.chunkManager = new ChunkManager(this , p-> {
            //return zombieManager.getZPlayer(p);
        //});
        //zombieManager = new ZombieGameManager();
        particleUtility = new ParticleUtility();
       // fakeArmorStandManager = new FakeArmorStandManager();
        this.editorManager = new ChatEditorManager();


        //getServer().getPluginManager().registerEvents(new CraftingPatternListener() , this);
        getServer().getPluginManager().registerEvents(this , this);

        Bukkit.getScheduler().runTaskTimer(this , r-> tick++ , 1l ,1l);
    }


    @Override
    public void onPluginLateEnable() {
        super.onPluginLateEnable();
    }

    @EventHandler
    public void test(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if (e.getAction() == Action.LEFT_CLICK_AIR) {

        }
    }



    protected void onLateEnable() {
        gson.build(null);
        super.onLateEnable();
    }

    public GsonManager getGson() {
        return gson;
    }

}
