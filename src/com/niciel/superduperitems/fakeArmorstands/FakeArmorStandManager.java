package com.niciel.superduperitems.fakeArmorstands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.fakeArmorstands.nms.v1_15_R1.FakeArmorStand_v1_15_R1;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.PlayerIterator;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.chunkdatastorage.ChunkManager;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorManager;
import com.niciel.superduperitems.utils.IManager;
import com.niciel.superduperitems.utils.SimpleCommandInfo;
import com.niciel.superduperitems.utils.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
@SimpleCommandInfo(command = "modelmanager" , aliases = {"mm"} , description = "description modelmanager" , usage = "/modelmanager")
public class FakeArmorStandManager implements IManager , Listener , CommandExecutor {


    private HashMap<Integer , FakeArmorStand_v1_15_R1> armorstands = new HashMap<>();
    private File dataFolder;
    private HashMap<String , ArmorStandModel> idToModel;



    private ChunkManager chunks;
    private boolean active = true;
    private CommandPointer pointer;


    public ArmorStandModel spawnModel(Vector vec , float yaw , String world, String modelId) {
        ArmorStandModel model = idToModel.get(modelId);
        if (modelId == null)
            return null;
        else {
            List<FakeArmorStand_v1_15_R1> a = new ArrayList<>();
            model.armorstands.forEach( c-> a.add(c.clone()));

            List<Vector> o = new ArrayList<>();
            model.orgin.forEach(c-> o.add(c.clone()));

            List<DataModelBlock> blocks = new ArrayList<>();
            model.blocks.forEach(c -> blocks.add(c.clone()));

            model = new ArmorStandModel(o,a ,blocks , new Vector(0,0,0) , model.getYaw()  , world);
            model.move(vec , yaw);
            return model;
        }
    }


    @Override
    public void onEnable() {
        dataFolder = new File(SDIPlugin.instance.getDataFolder() , "armorstandsmodels");
        if (! dataFolder.exists())
            dataFolder.mkdirs();
    }



    public boolean addModel(String id , ArmorStandModel model) {
        if (idToModel.containsKey(id)) {
            return false;
        }
        model.move(new Vector(0,0,0) , 0);
        idToModel.put(id, model);
        return  true;
    }


    @Override
    public void onLateEnable() {
        PacketListenerAPI.addPacketHandler(new PacketHandler() {

            @Override
            public void onSend(SentPacket sentPacket) {}

            @Override
            public void onReceive(ReceivedPacket r) {
                if (r.hasPlayer()) {
                    if (r.getPacketName().contentEquals("PacketPlayInUseEntity")) {
                        FakeArmorStand_v1_15_R1 fas = armorstands.get(r.getPacketValue(0));
                        if (fas != null) {
                            fas.onUse(r.getPlayer());
                            r.setCancelled(true);
                        }
                    }
                }
            }}

        );

        idToModel = new HashMap<>();
        File f = new File(dataFolder , "models.yml");
        if (f.exists()) {
            loadModels(f);
        }

        WeakReference<FakeArmorStandManager> _instance = new WeakReference<>(this);
        pointer = SDIPlugin.instance.getManager(GuiCommandManager.class).registerGuiCommand((p, a) -> {
            if (a.length==1) {
                _instance.get().sendMenu(p);
                return ;
            }
            else if (a.length == 2) {
                if (a[1].contentEquals("list")) {
                    _instance.get().sendList(p);
                    return;
                }
                else if (a[1].contentEquals("exit")) {
                    SDIPlugin.instance.getManager(ChatEditorManager.class).removeEditor(p);
                    p.sendMessage("removed");
                    return;
                }
                else if (a[1].contentEquals("serialize")) {
                    saveModels(new File(dataFolder , "models.yml"));
                    p.sendMessage("serialized");
                    return;
                }
            }
            else if (a.length == 3) {
                if (a[1].contentEquals("add")) {
                    if (idToModel.containsKey(a[2])) {
                        p.sendMessage("taki modelid JUZ istnieje !!!: " + a[2]);
                    }
                    else {
                        ArmorStandModel model = new ArmorStandModel();
                        this.edit(a[2] , model , p ,false);
                        return;
                    }
                    return;
                }
                else if (a[1].contentEquals("edit")) {
                    ArmorStandModel model = idToModel.get(a[2]);
                    if (model == null) {
                        p.sendMessage("brak modelu o id : " + a[2]);
                        return;
                    }

                    edit(a[2] , model , p , true);


                    return;
                }
                else if (a[1].contentEquals("giveitem")) {
                    ArmorStandModel model = idToModel.get(a[2]);
                    if (model == null) {
                        p.sendMessage("brak modelu o id : " + a[2]);
                        return;
                    }
                    p.getInventory().addItem(createItemModel(model , a[2]));
                    p.sendMessage("dodano");
                    return;
                }
            }
            p.sendMessage("syntax error!! " + a.length);
        },this.getClass(),SDIPlugin.instance);

        this.chunks = SDIPlugin.instance.getManager(ChunkManager.class);
    }



    private String permission = "sdiplugin.modelmanager.op";
    public boolean hasPermission(Player p) {
        if (! p.hasPermission(permission)) {
            p.sendMessage("Za mały poziom dostępu !");
            return false;
        }
        return true;
    }

    public void edit(String namied , ArmorStandModel model , Player p , boolean editmode) {
        if (SDIPlugin.instance.getManager(ChatEditorManager.class).getEditor(p) != null) {
            p.sendMessage("posiadasz otwarty edytor !!");
            return ;
        }
        Vector v = p.getLocation().getBlock().getLocation().toVector().add(new Vector(0.5,0,0.5));
        model.setPosition(v);
        p.sendBlockChange(new Location(p.getWorld() , v.getX() , v.getY() -1,v.getZ()), Bukkit.getServer().createBlockData(Material.RED_WOOL));
        p.sendMessage("zrobiono na pzycji: " + model.getPosition());
        ChatCommandEditor<ArmorStandModel> editor = SDIPlugin.instance.getManager(ChatEditorManager.class).createChatCommandEditor(p,model);

        WeakReference<FakeArmorStandManager> _instance = new WeakReference<>(this);
        editor.addCommand("zapisz" , e ->{
            _instance.get().setModel(namied , model);
            SDIPlugin.instance.getManager(ChatEditorManager.class).removeEditor(e);
            e.sendMessage("zostało zapisane !");
        });

        editor.addCommand("exit" , c -> {
            SDIPlugin.instance.getManager(ChatEditorManager.class).removeEditor(c);
            c.sendMessage("usunieto");
        });
        SDIPlugin.instance.getManager(ChatEditorManager.class).enable(editor , editmode);

        model.setPlayers(new PlayerIterator());
        model.getPlayers().add(p);
    }

    public void setModel(String namied , ArmorStandModel model) {
        idToModel.put(namied , model);
    }




    public void sendList(Player p) {
        if (!hasPermission(p))
            return;
        TextComponent tc = null;
        TextComponent in;
        int i =0;
        for (Map.Entry<String , ArmorStandModel> e : this.idToModel.entrySet()) {
            if (i%4==0) {
                if (tc != null)
                    p.spigot().sendMessage(tc);
                tc = new TextComponent();
                tc.setColor(ChatColor.GRAY);
            }
            tc.addExtra(" " + e.getKey());
            in = new TextComponent("[e]");
            in.setColor(ChatColor.BLUE);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , pointer.getCommand() + " edit " + e.getKey()));
            tc.addExtra(in);

            in = new TextComponent("[g]");
            in.setColor(ChatColor.GREEN);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , pointer.getCommand() + " giveitem " + e.getKey()));
            tc.addExtra(in);
        }
        if (tc != null)
            p.spigot().sendMessage(tc);
    }


    public void sendMenu(Player p) {
        if (! hasPermission(p))
            return;
        TextComponent tc = new TextComponent("modelmanager: ");
        tc.setColor(ChatColor.GRAY);
        TextComponent in ;
        in = new TextComponent("[lista]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , pointer.getCommand() + " list"));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("[dodaj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , pointer.getCommand() + " add "));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , pointer.getCommand() + " edit "));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("[serialize]");
        in.setColor(ChatColor.GOLD);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , pointer.getCommand() + " serialize"));
        tc.addExtra(in);

        p.spigot().sendMessage(tc);
    }


    public void loadModels(File f) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(s -> sb.append(s));
            JsonArray e = SDIPlugin.instance.getGson().fromJson(sb.toString() , JsonArray.class);
            reader.close();
            e.forEach(c -> {
                JsonObject o = c.getAsJsonObject();
                String n = o.get("name").getAsString();
                ArmorStandModel model = (ArmorStandModel) SDIPlugin.instance.getGson().fromJson(o.get("model").getAsJsonObject() , GsonSerializable.class);
                idToModel.put(n , model);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveModels(File f) {
        JsonObject o ;
        JsonArray array = new JsonArray();
        for (Map.Entry<String , ArmorStandModel> e : idToModel.entrySet()) {
            o = new JsonObject();
            o.addProperty("name" , e.getKey());
            o.add("model" , GsonManager.toJsonTree(e.getValue()));
            array.add(o);
        }

        try {
            if (! f.exists())
                f.createNewFile();
            FileWriter writer = new FileWriter(f);
            String s = SDIPlugin.instance.getGson().toJson(array);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(FakeArmorStand_v1_15_R1 armorStand) {
        armorstands.put(armorStand.getID() , armorStand);
    }

    public void register(Collection<FakeArmorStand_v1_15_R1> col) {
        col.forEach(c -> register(c));
    }

    public void unregister(Collection<FakeArmorStand_v1_15_R1> stands) {
        stands.forEach(c ->unregister(c));
    }

    public void unregister(FakeArmorStand_v1_15_R1 s) {
        unregister(s.getID());
    }

    public void unregister(int id) {
        armorstands.remove(id);
    }
    public ArmorStandModel copyModel(@Nonnull ArmorStandModel orginal , @Nonnull Vector position ) {
        return copyModel(orginal , position ,0);
    }

    public ArmorStandModel copyModel(@Nonnull ArmorStandModel orginal , @Nonnull Vector position , float yaw) {
        ArmorStandModel model = orginal.clone();
        if (yaw == 0)
            model.setPosition(position);
        else
            moveAndRotate(model , position ,yaw);
        return model;
    }

    public ArmorStandModel getModel(String nameid) {
        return this.idToModel.get(nameid);
    }

    private void moveAndRotate(ArmorStandModel model ,Vector pos , float yaw) {
        double rotation = SpigotUtils.DegToRand*yaw;
        for (Vector v : model.orgin)
            v.rotateAroundY(rotation);
        int i = 0;
        for (DataModelBlock d : model.blocks) {
            Vector dif = d.position.clone();
            d.position.rotateAroundY(rotation);
            Vector a = d.position;
            a.setX(SpigotUtils.RoundNumber.HALF_UP.round(2 , a.getX()));
            a.setZ(SpigotUtils.RoundNumber.HALF_UP.round(2 , a.getZ()));
            d.position = a;
            i++;
        }
        for (FakeArmorStand_v1_15_R1 a : model.armorstands) {
            a.setYaw(a.getYaw()-yaw);
        }
        model.setPosition(pos);
    }





    @EventHandler
    public void spawnItem(BlockPlaceEvent e) {
        if (!active)
            return;
        ItemStack is = e.getItemInHand();
        if (is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            if (im.hasLore()) {
                List<String> lore = im.getLore();
                if (lore.isEmpty())
                    return;
                String s = lore.get(0);
                if (!s.contains("modeid: ")) {
                    return;
                }
                s = s.replace("modeid: " , "");
                e.setCancelled(true);
                ArmorStandModel m = idToModel.get(s);
                if (m == null) {
                    e.getPlayer().sendMessage("niepoprawny modelid");
                    return;
                }
                BlockFace face = e.getPlayer().getFacing();
                Vector dir = face.getDirection();
                dir.setY(0);
                float yaw ;
//                yaw = (float) (dir.angle(new Vector(1,0,0))/SpigotUtils.DegToRand);
                yaw = (float) (Math.atan(dir.getX()/dir.getZ())/SpigotUtils.DegToRand);
                double realYaw = dir.angle(new Vector(0,0,1)) / SpigotUtils.DegToRand;
                int r = (int) realYaw*100;
                realYaw = (double) r/(double) 100;
                if (realYaw == 90) {
                    if (yaw<0)
                        realYaw =270;
                }
                Vector position = e.getBlockPlaced().getLocation().toVector().add(new Vector(0.5,0,0.5));
                ArmorStandChunkModel chunkModel = new ArmorStandChunkModel();
                chunkModel.uuid = UUID.randomUUID();
                chunkModel.rotation = (float) realYaw;
                chunkModel.orginalModelID = s;
                chunkModel.inWorldPosition = position;
                chunkModel.playerOwner = e.getPlayer().getUniqueId();
                chunkModel.spawnTime = System.currentTimeMillis();
                e.getPlayer().sendMessage("spawnuje " + s + " rotacja " + realYaw);
                Bukkit.getScheduler().runTaskLater(SDIPlugin.instance , () -> {
                    this.chunks.addChunkObject(e.getPlayer().getWorld().getName() ,position , chunkModel );
                    chunkModel.model.setBlocks(e.getPlayer().getWorld());
                },1);
            }
        }
    }

    @Deprecated
    public ItemStack createItemModel(ArmorStandModel model , String modelId) {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.RESET + "" + modelId);
        im.setLore(Arrays.asList("modeid: " + modelId));
        item.setItemMeta(im);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (! hasPermission(p))
                return true;
            sendMenu(p);
        }

        return true;
    }
}
