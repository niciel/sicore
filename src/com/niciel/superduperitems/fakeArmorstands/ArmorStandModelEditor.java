package com.niciel.superduperitems.fakeArmorstands;

import com.niciel.superduperitems.EventListenerHelper.CustomRegisteredListener;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.inGameEditor.editors.EditorChatObject;
import com.niciel.superduperitems.inGameEditor.editors.EnumEditor;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.RefCallBack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArmorStandModelEditor extends IChatEditorMenu<ArmorStandModel> {


    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private ArmorStandModel model;
    private BukkitTask task;

    private WeakReference<ChatCommandEditor> editor;

    private List<FakeArmorStand_v1_15_R1> selected;

    private CommandPointer deselect;
    private CommandPointer select;
    private VectorAxisManipulator axis;
    private CommandPointer openFakeMenu;
    private CommandPointer addComand;
    private CommandPointer removeArmorStand;
    private CommandPointer propertiesPointer;
    public EnumEditor enumEditor;

    public void _initSelection() {
        WeakReference<ArmorStandModelEditor> _instance = new WeakReference<>(this);


        select = manager.registerGuiCommand( (p,a) -> {
            UUID i ;
            i = UUID.fromString(a);
            if (i == null) {
                p.sendMessage("nieprawidlowy id");
            }
            else if (_instance.get().select(i) == false) {
                p.sendMessage("cos poszllo nie tal");
            }
            else {
                _instance.get().select(i);
            }
            _instance.get().editor.get().send();
        },this.getClass() ,SDIPlugin.instance);


        deselect = manager.registerGuiCommand( (p,a) ->{
            UUID i ;
            i = UUID.fromString(a);
            if (i == null) {
                p.sendMessage("nieprawidlowy id");
            }
            else if (_instance.get().select(i) == false) {
                p.sendMessage("cos poszllo nie tal");
            }
            else {
                _instance.get().deselect(i);
            }
            _instance.get().editor.get().send();
        },this.getClass() , SDIPlugin.instance);


        openFakeMenu= manager.registerGuiCommand((p,a) -> {
            _instance.get().editor.get().send();
        },this.getClass() , SDIPlugin.instance);




        addComand = manager.registerGuiCommand((p,a) -> {
            FakeArmorStand_v1_15_R1 armorStand = new FakeArmorStand_v1_15_R1(_instance.get().model.getPosition());
            _instance.get().model.armorstands.add(armorStand);
            _instance.get().model.orgin.add( new Vector(0,0,0) );
            armorStand.setPlayerCollection(_instance.get().model.getPlayers());
            _instance.get().select(armorStand.getUUID());
            _instance.get().editor.get().send();
        },this.getClass() , SDIPlugin.instance);



        removeArmorStand = manager.registerGuiCommand((p,a) -> {
            if (a == null || a.isEmpty())
                return;
            UUID uuid = UUID.fromString(a);
            if (uuid == null) {
                p.sendMessage("niepoprawny modelid");
            }
            else if (! _instance.get().remove(uuid)) {
                p.sendMessage("cos poszlo nie tak prawdopodobnie nie ma modelu z tym id");
            }
            _instance.get().editor.get().send();
            return;
        },this.getClass() , SDIPlugin.instance);


        propertiesPointer = manager.registerGuiCommand((p, a) ->{
            if (a.length < 3) {
                p.sendMessage("niepoprawna ilosc argumentow");
                return;
            }
            if (a[1].contentEquals("name")) {
                _instance.get().selected.forEach(c -> {
                    c.setCustomName(a[2]);
                    c.sendEntityMetaData(false);
                });
                return;
            }
            Boolean flag;
            flag = Boolean.parseBoolean(a[2]);

            if (flag != null) {
                if (a[1].contentEquals("baseplate")) {
                    _instance.get().selected.forEach(c-> {
                        c.setBasePlate(flag);
                        c.sendEntityMetaData(false);
                    });
                    _instance.get().editor.get().send();
                    return;
                }
                else if (a[1].contentEquals("marker")) {
                    _instance.get().selected.forEach(c-> {
                        c.setMarker(flag);
                        c.sendEntityMetaData(false);
                    });
                    _instance.get().editor.get().send();
                    return;
                }
                else if (a[1].contentEquals("invisible")) {
                    _instance.get().selected.forEach(c-> {
                        c.setInvisible(flag);
                        c.sendEntityMetaData(false);
                    });
                    _instance.get().editor.get().send();
                    return;
                }
                else if (a[1].contentEquals("setitem")) {
                    ItemStack is = p.getInventory().getItemInMainHand();
                    _instance.get().selected.forEach(armor -> armor.setItem(EquipmentSlot.HEAD , is));
                    return;
                }
            }
            p.sendMessage("niepoprawny argument");
        },this.getClass() , SDIPlugin.instance);

    }

    public boolean select(UUID i) {
        if (isSelected(i))
            return true;
        for (FakeArmorStand_v1_15_R1 f : model.armorstands) {
            if (f.getUUID().equals(i)){
                if (selected.isEmpty()) {
                    this.headRotation = f.getHeadPose().clone();
                    this.rotationManipulator.vector = f.getHeadPose().clone();
                }
                selected.add(f);
                return true;
            }
        }
        return false;
    }

    public void deselect(UUID uuid) {
        if (isSelected(uuid) ) {
            for (int i = 0 ; i < selected.size() ; i ++) {
                if (selected.get(i).getUUID().equals(uuid))
                {
                    selected.remove(i);
                    return;
                }
            }
        }
    }

    private EditorChatObject<IModelBehaviour> behaviourEditor;

    public void sendFake(Player p) {
        sendSelectMode(p);
        p.sendMessage("move");
        sendMove(p);
        p.sendMessage("rotation:");
        sendRotation(p);
        sendAdd(p);
        sendProperties(p);
        behaviourEditor.sendItem(p);
    }

    public void sendRotation(Player p) {
        if (this.rotationManipulator.vector != null)
            this.rotationManipulator.send(p);
    }


    public void sendAdd(Player p) {
        TextComponent tc = new TextComponent("[dodaj]");
        tc.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND , addComand.getCommand()));
        tc.setColor(ChatColor.GREEN);
        p.spigot().sendMessage(tc);
    }


    public boolean remove(UUID uuid) {
        FakeArmorStand_v1_15_R1 stand = null;
        for (FakeArmorStand_v1_15_R1 f : model.armorstands) {
            if (f.getUUID().equals(uuid)) {
                stand = f;
                break;
            }
        }
        if (stand == null)
            return false;
        int id = getListIndex(stand);
        model.armorstands.remove(id);
        model.orgin.remove(id);
        stand.kill();
        for (int i = 0 ; i < selected.size() ; i++) {
            if (selected.get(i).getUUID().equals(uuid)) {
                selected.remove(i);
                break;
            }
        }
        return true;
    }

    public void sendSelectMode(Player p) {
        TextComponent tc ;
        TextComponent in;
        FakeArmorStand_v1_15_R1 stand;
        for (int i = 0 ; i < model.armorstands.size() ; i ++) {
            tc = new TextComponent("");
            stand = model.armorstands.get(i);
            UUID uuid = stand.getUUID();
            tc.addExtra(" " +i +":");
            if (isSelected(stand.getUUID())) {
                in = new TextComponent("[deselect]");
                in.setColor(ChatColor.GOLD);
                in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , deselect.getCommand() + " " +uuid));
            }
            else {
                in = new TextComponent("[select]   ");
                in.setColor(ChatColor.GREEN);
                in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , select.getCommand() + " " +uuid));
            }
            tc.addExtra(in);
            tc.addExtra("|");
            in = new TextComponent("[remove]");
            in.setColor(ChatColor.RED);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , removeArmorStand.getCommand() + " " + uuid));
            tc.addExtra(in);
            p.spigot().sendMessage(tc);
        }
    }

    private Vector orgin;

    public void sendMove(Player p) {
        Vector v = null;
        if (selected.size() >= 1) {
            int i = getListIndex(selected.get(0));
            v = this.model.orgin.get(i).clone();
        }
        else if (selected.isEmpty())
        {
            p.sendMessage("trza cos zaznaczyc :l");
            return;
        }
        axis.vector = v;
        orgin = v.clone();
        axis.send(p);
    }



    public int getListIndex(FakeArmorStand_v1_15_R1 f) {
        for (int i = 0 ; i < model.armorstands.size() ; i ++) {
            if (model.armorstands.get(i).getUUID().equals(f.getUUID()))
                return i;
        }
        return -1;
    }

    public boolean isSelected(UUID uuid ) {
        for (FakeArmorStand_v1_15_R1 f : selected) {
            if (f.getUUID().equals(uuid))
                return true;
        }
        return false;
    }


    public void sendProperties(Player p) {
        TextComponent tc;
        TextComponent in;

        tc = new TextComponent("wartosci: ");
        tc.setColor(ChatColor.BLUE);
        if (selected.isEmpty())
            return;
        FakeArmorStand_v1_15_R1 fake = selected.get(0);
        boolean flag;
        ChatColor color;

        in = new TextComponent("[baseplate]");
        flag = fake.hasBasePlate();
        if (flag)
            color = ChatColor.GREEN;
        else
            color = ChatColor.RED;
        flag = !flag;
        in.setColor(color);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , propertiesPointer.getCommand() + " baseplate " + flag));
        tc.addExtra(in);
        tc.addExtra(" ");

        in = new TextComponent("[marker]");
        flag = fake.isMarker();
        if (flag)
            color = ChatColor.GREEN;
        else
            color = ChatColor.RED;
        flag = !flag;
        in.setColor(color);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , propertiesPointer.getCommand() + " marker " + flag));
        tc.addExtra(in);
        tc.addExtra(" ");

        in = new TextComponent("[invisible]");
        flag = fake.isInvisible();
        if (flag)
            color = ChatColor.GREEN;
        else
            color = ChatColor.RED;
        flag = !flag;
        in.setColor(color);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , propertiesPointer.getCommand() + " invisible " + flag));
        tc.addExtra(in);


        in = new TextComponent("[setItem]");
        in.setColor(ChatColor.BLUE);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , propertiesPointer.getCommand() + " setitem " + true));
        tc.addExtra(in);

        in = new TextComponent("[name]");
        in.setColor(ChatColor.BLUE);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , propertiesPointer.getCommand() + " name "));
        tc.addExtra(in);

        p.spigot().sendMessage(tc);
    }


    @Override
    protected void finalize() throws Throwable {
        if (task != null) {
            if (!task.isCancelled())
                task.cancel();
            task = null;
        }
        super.finalize();
    }


    private String[] fill = new String[]{"","","","",""};
    @Override
    public void sendMenu(Player p) {
        p.sendMessage(fill);
        sendFake(p);
    }

    private CustomRegisteredListener listenerPlace;
    private CustomRegisteredListener listenerBreak;
    @Override
    public void onSelect(ChatCommandEditor editor) {
        WeakReference<ArmorStandModelEditor> _instance = new WeakReference<>(this);
        BukkitRunnable run = new BukkitRunnable() {
            public void run() {
                if ((_instance.isEnqueued() || _instance.get() == null) || _instance.get().editor.get() ==null ) {
                    this.cancel();
                    return;
                }
                if (_instance.get().selected.isEmpty() == false) {

                    World w = _instance.get().editor.get().getPlayer().getWorld();
                    if (w == null) {
                        cancel();
                        return;
                    }
                    _instance.get().selected.forEach(c-> {
                        Vector v = c.getPosition();
                        _instance.get().editor.get().getPlayer().spawnParticle(Particle.REDSTONE, v.getX() , v.getY() , v.getZ() ,
                                4 ,0d,0d,0d, new Particle.DustOptions(Color.GREEN , 1f));
                    });
                    _instance.get().axis.sendAxisParticles(_instance.get().editor.get().getPlayer() , _instance.get().model.getPosition());
                }
            }
        };

        listenerPlace = new CustomRegisteredListener<BlockPlaceEvent>(
                SDIPlugin.instance , EventPriority.MONITOR ,false , BlockPlaceEvent.class , e-> {
                    if (_instance.get() == null ) {
                        return ;
                    }
                    if (! _instance.get().editor.get().getPlayer().getName().contentEquals(e.getPlayer().getName()))
                        return;
                    DataModelBlock b = _instance.get().getBlockData(e.getBlockPlaced().getLocation());
                    if (b == null) {
                        b = new DataModelBlock();
                        b.position = e.getBlockPlaced().getLocation().toVector().subtract(_instance.get().model.getPosition().clone()).add(new Vector(0.5,0,.5));
                        b.data = "";
                        e.getPlayer().sendMessage("dodano blok");
                        _instance.get().model.blocks.add(b);
                    }
                    b.blockData = e.getBlockPlaced().getBlockData().clone();
        } );

        listenerBreak = new CustomRegisteredListener<BlockBreakEvent>(
                SDIPlugin.instance , EventPriority.MONITOR ,false , BlockBreakEvent.class , e-> {
            if (_instance.get() == null ) {
                return ;
            }
            if (! _instance.get().editor.get().getPlayer().getName().contentEquals(e.getPlayer().getName()))
                return;

            DataModelBlock b = _instance.get().getBlockData(e.getBlock().getLocation());
            if (b != null) {
                _instance.get().removeBlock(e.getBlock().getLocation());
                e.getPlayer().sendMessage("usunieto block");
            }
        } );

        listenerPlace.register();
        listenerBreak.register();

        task = run.runTaskTimer(SDIPlugin.instance , 1,10);
        this.model.getPlayers().add(editor.getPlayer());
    }

    @Override
    public void onDeselect(ChatCommandEditor editor) {
        task.cancel();
        editor.getPlayer().sendMessage("deselect !!");
        model.armorstands.forEach(p-> p.kill());
        this.model.getPlayers().remove(editor.getPlayer());
        listenerPlace.remove();
        listenerBreak.remove();
    }


    public DataModelBlock getBlockData(Location position) {
        Vector v = position.toVector().subtract(model.getPosition());
        for (DataModelBlock b : model.blocks) {
            if (b.position.equals(v)) {
                return b;
            }
        }
        return null;
    }


    public boolean removeBlock(Location loc) {
        Vector v = loc.toVector().subtract(model.getPosition());
        DataModelBlock b ;
        for (int i = 0 ; i < model.blocks.size() ; i++) {
            b = model.blocks.get(i);
            if (b.position.equals(v)) {
                model.blocks.remove(i);
                return true;
            }
        }
        return false;
    }

    public void updateAxis() {
        Vector przesuniecie = axis.vector.subtract(orgin);
        int pos;
        Vector or;
        for (FakeArmorStand_v1_15_R1 s : selected) {
            pos = getListIndex(s);
            or = model.orgin.get(pos);
            or.add(przesuniecie);
            s.setPosition(model.getPosition().add(or));
        }
        this.editor.get().send();
    }

    private Vector headRotation;
    private VectorAxisManipulator rotationManipulator;

    public void updateRotation() {
        Vector dif = this.rotationManipulator.vector.clone().subtract(headRotation);
        for (FakeArmorStand_v1_15_R1 f : selected) {
            f.setHeadPose(f.getHeadPose().add(dif));
            f.sendEntityMetaData(false);
        }
        headRotation = this.rotationManipulator.vector.clone();
    }

    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<ArmorStandModel> refToObject) {
        this.selected = new ArrayList<>();
        this.axis = new VectorAxisManipulator();
        this.axis.enable();
        this.model = refToObject.getValue();
        WeakReference<ArmorStandModelEditor> _instance = new WeakReference<>(this);
        this.editor = editor;
        this.axis.update = v -> {
            _instance.get().updateAxis();
        };
        _initSelection();
        this.headRotation = new Vector(0,0,0);
        this.rotationManipulator = new VectorAxisManipulator();
        this.rotationManipulator.a = 90;
        this.rotationManipulator.b = 22.5;
        this.rotationManipulator.c = 5.625;
        this.rotationManipulator.update = v-> {
            _instance.get().updateRotation();
        };
        this.rotationManipulator.enable();
        refToObject.getValue().setBlocks(editor.get().getPlayer().getWorld());

        this.behaviourEditor = new EditorChatObject<>();
        RefCallBack<IModelBehaviour> c = new RefCallBack(null);
        c.addCallBack(r -> {
            _instance.get().model.behaviour = r.getValue();
        });
        this.behaviourEditor.enable(editor , "nazwa" , " cus" , IModelBehaviour.class , c);
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[edytuj model]");
        tc.setColor(ChatColor.GREEN);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , openFakeMenu.getCommand() ));
        p.spigot().sendMessage(tc);
    }
}
