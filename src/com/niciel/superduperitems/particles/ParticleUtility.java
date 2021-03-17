package com.niciel.superduperitems.particles;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.chunkdatastorage.ChunkData;
import com.niciel.superduperitems.chunkdatastorage.ChunkManager;
import com.niciel.superduperitems.chunkdatastorage.ChunkWorldManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.managers.SimpleCommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


@SimpleCommandInfo(command = "particles" , aliases =  {} , description = "jakos tak /[comenda]" , usage = "jakos tak /[comenda]" )
public class ParticleUtility implements CommandExecutor , IManager , Listener {


    private String addPermission;
    private HashMap<String, ParticleData> particles;
    private File datafile;


    private HashMap<String , ParticleData> playerToData;
    private HashMap<String , ChatCommandEditor> editors;

    public ParticleUtility() {
        this.addPermission = "addParticlesutility";
        this.particles = new HashMap<>();
        playerToData = new HashMap<>();
        editors = new HashMap<>();
    }


    @Override
    public void onEnable() {
        datafile = new File(SDIPlugin.instance.getDataFolder(), "particles.yml");
    }

    @Override
    public void onLateEnable() {
        if (datafile.exists()) {

        }
    }

/*

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] cmd) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (!p.hasPermission(addPermission)) {
                p.sendMessage("niewystarczajacy poziom uprawnien");
                return false;
            }
            if (cmd.length == 1) {
                if (cmd[0].contentEquals("wyswietl")) {
                    for (Map.Entry<String , ParticleData> e : particles.entrySet()) {
                        p.sendMessage(e.getKey());
                    }
                }
            }
            if (cmd.length >= 2) {
                String param = cmd[0];
                if (param.contentEquals("dodaj")) {
                    if (editors.containsKey(p.getName())) {
                        editors.get(p.getName()).send();
                    }
                    else if (particles.containsKey(cmd[1])) {
                        p.sendMessage("taki particle istnieje mozesz go edytowac albo usunac");
                        p.sendMessage("komenda TODO");
//                        TODO
                    }
                    else {
                        ParticleData data = new ParticleData();
                        Vector v = p.getEyeLocation().toVector();
                        data.x = v.getX();
                        data.y = v.getY();
                        data.z = v.getZ();
                        ChatCommandEditor ce = new ChatCommandEditor(p ,data);
                        WeakReference<ChatCommandEditor> _editorInstance = new WeakReference<>(ce);
                        ce.addCommand("wyswietl" , (pl) -> {
                            ParticleData d = (ParticleData) _editorInstance.get().mainObject;
                            d.send((Player) pl);
                        });
                        ce.addCommand("wyswietl 10sec" , e-> {
                            ParticleData d = (ParticleData) _editorInstance.get().mainObject;
                            WeakReference<Player> pl = new WeakReference<>(p);
                            Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance, new Consumer<BukkitTask>() {
                                int ticks = 0;
                                @Override
                                public void accept(BukkitTask t) {
                                    if (ticks >=200) {
                                        t.cancel();
                                        return;
                                    }
                                    if (pl == null || pl.isEnqueued()) {
                                        t.cancel();
                                        return;
                                    }
                                    if (ticks%d.timer == 0){
                                        if (pl.get().isOnline()) {
                                            d.send(pl.get());
                                        }
                                        else {
                                            t.cancel();
                                        }
                                    }
                                    ticks++;
                                }
                            }  , 1, 1);
                        });
                        String typeName = cmd[1];
                        WeakReference<ParticleUtility> _instance = new WeakReference<>(this);
                        ce.addCommand("dodaj" , (ps) -> {
                            Player pl = (Player) ps;
                            _instance.get().particles.put(typeName , (ParticleData) _editorInstance.get().mainObject);
                            pl.sendMessage("dodano "+((ParticleData) _editorInstance.get().mainObject).particle);
                            _instance.get().editors.remove(pl.getName());
                            _instance.get().saveParticles();
                        });

                        ce.addCommand("wylacz" , (pl) -> {
                            ((Player)pl).sendMessage("edytor zostal wylaczony ");
                            _instance.get().editors.remove(((Player) pl).getName());
                        });

                        editors.put(p.getName() , ce);
                        ce.send();
                        return true;
                    }
                }
                else if (param.contentEquals("zaznacz")) {
                    String toSelect = cmd[1];
                    ParticleData data = particles.get(toSelect);
                    if (toSelect.contentEquals("null")) {
                        playerToData.remove(p.getName());
                        p.sendMessage("particles zostal wylaczony");
                        return true;
                    }
                    if (data == null) {
                        p.sendMessage("taki particle nie istnieje: " + toSelect);
                        return false;
                    }
                    playerToData.put(p.getName() , data);
                    p.sendMessage("zostal zaznaczony !");
                }
            }
        }
        return false;
    }


 */
    public void loadParticlesSync() {
        JsonArray array;
        try {
            FileReader reader = new FileReader(datafile);
            BufferedReader r = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            r.lines().forEach(c->sb.append(c));

            array = new JsonParser().parse(sb.toString()).getAsJsonArray();
            //array = GsonManager.getInstance().fromJson(sb.toString(),JsonArray.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        particles.clear();
        array.forEach( a -> {
            JsonObject o = a.getAsJsonObject();

            particles.put(o.get("name").getAsString() , (ParticleData) GsonManager.getInstance().fromJson(o.get("particle").getAsJsonObject() ,  GsonSerializable.class));
        });
    }

    public void saveParticles() {
        Bukkit.getScheduler().runTaskAsynchronously(SDIPlugin.instance, ()-> {
            JsonObject o;
            JsonArray array = new JsonArray();
            for (Map.Entry<String , ParticleData> e : particles.entrySet()) {
                o = new JsonObject();
                o.addProperty("name" , e.getKey());
                o.add("particle" , GsonManager.getInstance().toJson(e.getValue()));
                array.add(o);
            }
            try {
                FileWriter writer = new FileWriter(datafile);
                writer.write(GsonManager.getInstance().toJson(array).toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().hasPermission(addPermission)) {
            ParticleData data = playerToData.get(e.getPlayer().getName());
            if (data == null) {
                return ;
            }
            e.setCancelled(true);
            ChunkManager manager = SDIPlugin.instance.getManager(ChunkManager.class);
            ChunkWorldManager world = manager.getWorldManager(e.getPlayer().getWorld());
            if (world == null) {
                e.getPlayer().sendMessage("w tym swiecie nie da sie nic dodac");
                return;
            }
            ChunkData d = world.getOrCreateChunkData(e.getBlockPlaced().getLocation());
            ParticleChunkObject m = d.getChunkObject(ParticleChunkObject.class);
            if (m == null) {
                m = new ParticleChunkObject();
                d.addElement(m);
            }
            data = data.clone();
            Vector v = e.getBlock().getLocation().toVector();
            data.x = v.getX() +0.5;
            data.y = v.getY()+0.5;
            data.z = v.getZ()+0.5;
            m.particles.add(data);
            d.setChanged();
            e.getPlayer().sendMessage("particleesss" + data.particle + " " + v);
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        playerToData.remove(e.getPlayer().getName());
        editors.remove(e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
