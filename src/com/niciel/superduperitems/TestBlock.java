package com.niciel.superduperitems;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.EventListenerHelper.CustomRegisteredListener;
import com.niciel.superduperitems.EventListenerHelper.RegisteredEventPointer;
import com.niciel.superduperitems.chunkdatastorage.*;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
//import com.niciel.superduperitems.fakeArmorstands.ArmorStandModel;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditableMethod;
//import com.niciel.superduperitems.inGameEditor.editors.EditorChatMethod;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestBlock implements IChunkObject , ICustomBlock {

    private UUID uuid = UUID.randomUUID();

    @ChatEditable
    private String modelID;
    @ChatEditable
    private Vector position;

    private List<Vector> blocks;

    //private ArmorStandModel model;

    private PlayerIterator players;


    public TestBlock() {
        this.blocks = new ArrayList<>();
    }

    @Override
    public void enable(ChunkData d) {
        players = d.playersInRange;
    }

    @Override
    public void disable(ChunkData d) {

    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public JsonObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonObject o) {

    }


//    @ChatEditableMethod
//    public void testEdytor(EditorChatMethod<RegisteredEventPointer<BlockPlaceEvent>> d) {
//        WeakReference<TestBlock> _instance = new WeakReference<>(this);
//        if (d.data == null) {
//            d.data = new RegisteredEventPointer<BlockPlaceEvent>(BlockPlaceEvent.class, SDIPlugin.instance,
//                    EventPriority.LOWEST, false) {
//
//                @Override
//                public void onEvent(BlockPlaceEvent e) {
//                    e.getPlayer().sendMessage("tada");
//                }
//            };
//            d.data.register();
//            WeakReference<EditorChatMethod<CustomRegisteredListener>> _method = new WeakReference(d);
//            CommandPointer pointer = SDIPlugin.instance.getManager(GuiCommandManager.class).registerGuiCommand((p,a) -> {
//                _method.get().data.setSuspended(!_method.get().data.isSuspended());
////                _method.get().editor.get().send();
//            }, this.getClass() , SDIPlugin.instance) ;
//            //d.setData("change" , pointer);
//
//        }
//        TextComponent tc = new TextComponent(" listener: " + !d.data.isSuspended());
//       // tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , d.getData("change"  , CommandPointer.class).getCommand()));
////        d.player.get().spigot().sendMessage(tc);
//    }


    @Override
    public void onBreak(BlockBreakEvent e) {

    }

    @Override
    public void onBlockDamage(BlockDamageEvent e) {

    }

    @Override
    public void onBlockInteract(PlayerInteractEvent e) {

    }
}
