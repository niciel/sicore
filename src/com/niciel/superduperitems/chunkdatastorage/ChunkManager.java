package com.niciel.superduperitems.chunkdatastorage;

import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.managers.SimpleCommandInfo;
import com.niciel.superduperitems.utils.Vector2int;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;



@SimpleCommandInfo(command = "chunkmanager" , aliases =  {} , description = "jakos tak /[comenda]" , usage = "jakos tak /[comenda]" )
public class ChunkManager implements IManager , Listener , CommandExecutor {
    private Function<Player,IChunkPlayerData> function;
    private List<ChunkWorldManager> worlds;
    private WeakReference<JavaPlugin> plugin;




    public ChunkManager(JavaPlugin plugin , Function<Player , IChunkPlayerData> function) {
        this.function = function;
        this.worlds = new ArrayList<>();
        this.plugin = new WeakReference<>(plugin);
    }



    @Override
    public void onLateEnable() {
        WeakReference<ChunkManager> _instance = new WeakReference<>(this);
        File f;
        ChunkWorldManager manager;
        for (World w : Bukkit.getWorlds()) {
            f = new File (w.getWorldFolder() , "chunkworldmanager.db");
            if (f.exists() || new File(w.getWorldFolder(), "jdbcbat.yml").exists()) {
                manager = new ChunkWorldManager(w , function);
                worlds.add(manager);
            }
        }
        worlds.forEach(c-> c.enable(this));
    }



    public void deserializeWorldManager(World w) {
//        TODO
    }

    public void serializeWorldManager(World w) {
//        TODO
    }

    public void disableChunk(Vector2int vec) {

    }

    public ChunkWorldManager getWorldManager(World world) {
        return getWorldManager(world.getName());
    }

    public ChunkWorldManager getWorldManager(String world) {
        for (ChunkWorldManager w : worlds) {
            if (w.getWorldName().contentEquals(world))
                return w;
        }
        return null;
    }

    public boolean addChunkObject(String world , Vector position , IChunkObject o) {
        ChunkWorldManager m = getWorldManager(world);
        if (m == null)
            return false;
        Chunk c = m.getWorld().getChunkAt(new Location(m.getWorld() , position.getX() , position.getY() , position.getZ()));
        //SDIPlugin.instance.logWarning(this , "wartosc chunka " + c.isLoaded());
        Vector2int chunkVector = new Vector2int(c);
        ChunkData data = m.getOrCreateChunkData(chunkVector);
        data.addElement(o);
        return true;
    }



    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        ChunkWorldManager m = getWorldManager(e.getBlock().getWorld());
        if (m != null)
            m.onBlockBreak(e);
    }



    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {
        deserializeWorldManager(e.getWorld());
        System.out.println("n\n\n\n\n\n\n\\n\n wczytuje swiat " + e.getWorld().getName());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        ChunkWorldManager m = getWorldManager(e.getWorld());
        if (m != null) {
            m.onChunkUnload(e);
        }
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent e) {
        ChunkWorldManager m = getWorldManager(e.getWorld());
        if (m != null) {
            m.onChunkLoad(e);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ChunkWorldManager m = getWorldManager(e.getPlayer().getWorld());
        if (m != null) {
            m.onPlayerQuit(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMoveWorld(PlayerChangedWorldEvent e) {
        ChunkWorldManager from = getWorldManager(e.getFrom());
        if (from != null) {
            from.onPlayerTeleportToOtherWorld(e.getPlayer());
        }
        IChunkPlayerData d = function.apply(e.getPlayer());
        d.move(new Vector2int(Integer.MIN_VALUE , Integer.MIN_VALUE));
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        serializeWorldManager(e.getWorld());
        deserializeWorldManager(e.getWorld());
        System.out.println("n\n\n\n\n\n\n\\n\n zapisuje swiat " + e.getWorld().getName());
    }

    private static String permission = "chunkmanagerEditor";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (p.hasPermission(permission) == false) {
                p.sendMessage("brak uprawnien");
                return false;
            }
            if (args.length == 1) {
                if (args[0].contentEquals("wyswietl")) {
                    Vector2int current = new Vector2int(p.getLocation().getChunk());
                    ChunkWorldManager wm = getWorldManager(p.getWorld());
                    if (wm == null) {
                        p.sendMessage("swiat nie jest wspierany");
                        return true;
                    }
                    TextComponent tc ;
                    TextComponent in;
                    for (int x = -3 ; x <= 3 ; x++) {
                        tc = new TextComponent();
                        for (int z = -3; z <= 3; z++) {
                            if (x==z && x ==0) {
                                in = new TextComponent("O");
                            }
                            else {
                                in = new TextComponent("X");
                            }
                            ChunkData data = wm.getChunkData(new Vector2int( current.x + x , current.y + z));
                            if (data == null){
                                in.setColor(ChatColor.RED);
                            }
                            else {
                                in.setColor(ChatColor.GREEN);
//                                TODO
                            }
                            tc.addExtra(in);
                        }
                        p.spigot().sendMessage(tc);
                    }
                }
            }
        }
        return false;
    }
}
