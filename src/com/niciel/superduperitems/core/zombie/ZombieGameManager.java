package com.niciel.superduperitems.core.zombie;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.Tickable;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.customitems.ItemManager;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorManager;
import com.niciel.superduperitems.randomchest.IRandomBlock;
import com.niciel.superduperitems.randomchest.InitRandom;
import com.niciel.superduperitems.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
//import net.minecraft.server.v1_15_R1.EntityArmorStand;
//import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;


@SimpleCommandInfo(usage = "gamecore" , description = "gamecore" , aliases = "gamecore" , command = "gamecore")

public class ZombieGameManager implements CommandExecutor , IManager , Listener , Tickable {


    private ItemManager itemManager = SDIPlugin.instance.getManager(ItemManager.class);
    private HashMap<String , ZPlayer> nameToZPlayer ;
    private List<ZPlayer> tickList;
    private WeakReference<ChatEditorManager> editorManager;

    private HashMap<Vector2int, List<ChestDropState>> chests = new HashMap<Vector2int ,  List<ChestDropState>>();
    private HashMap<String, ChestScheme> nameToChestScheme = new HashMap<>();
    private HashSet<ChestDropState> activeChests = new HashSet<>();

    private IRandomBlock random;

    protected void removeTickingPlayer(Player p) {
        for (int i = 0 ; i < 0 ; i++) {
            if (tickList.get(i).player.get().getName().contentEquals(p.getName()) ) {
                tickList.remove(i);
                return;
            }
        }
    }

    public World getGameWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public void loadPlayers() {
        Bukkit.getOnlinePlayers().forEach( c -> {
            ZPlayer z = new ZPlayer(c.getUniqueId());
            tickList.add(z);
            nameToZPlayer.put(c.getName() , z);
        });
    }

    private int tick =0;

    public void onTick() {
        for (ZPlayer z : tickList)
            z.onTick();
        if (tick%20 == 0) {
            Location loc;
            long current = System.currentTimeMillis();
            ChestScheme scheme;
            Chest chest;
            List<ItemStack> drops;
            IRandomBlock r;
            ItemStack[] content;
            for (ChestDropState s : activeChests) {
                if (s.generated)
                    continue;
                scheme = this.nameToChestScheme.get(s.chestScheme);
                if (scheme == null) {
//                    TODO
                    continue;
                }
                if (s.lastOpenTime+(scheme.respawnTime *1000) >= current) {
                    continue;
                }
                loc = new Location(getGameWorld() , s.position.getX(), s.position.getY(), s.position.getZ());
                if (SpigotUtils.getFirstNerbayPlayer(loc , scheme.playerDistance) != null)
                    continue;
                r = randomBlocks.get(scheme.random_ID);
                if (r == null) {
//                    TODO
                    continue;
                }
                if (loc.getBlock().getType() == Material.AIR) {
                    loc.getBlock().setBlockData(s.block);
                }
                chest = (Chest) loc.getBlock();
                drops = new ArrayList<>();
                r.generate(1,drops);
                content = chest.getBlockInventory().getContents();
//                TODO wielkosc dropu
                if (content.length <= drops.size()) {
                    SDIPlugin.instance.logWarning(this , "przekroszono wielkosc chesta "
                            + content.length + " dropow " + drops.size() + " type " + scheme.random_ID);
                    continue;
                }
                for (int i = 0 ; i < drops.size() ; i++) {
                    content[i] = drops.get(i);
                }
                chest.getBlockInventory().setContents(content);
                s.generated = true;
            }
        }
        tick++;
    }

    public void serializeChests() {
        JsonObject object = new JsonObject();
        JsonArray main = new JsonArray();
        JsonElement o;
        for (List<ChestDropState> e : this.chests.values()) {
            for (ChestDropState s : e) {
                main.add(GsonManager.toJsonTree(s));
            }
        }
        object.add("positions" , main);
        int chests = main.size();
        main = new JsonArray();
        for (ChestScheme s : this.nameToChestScheme.values()){
            main.add(GsonManager.toJsonTree(s));
        }
        object.add("schemats" , main);
        File f = new File(SDIPlugin.instance.getDataFolder() , "chestPosData.data");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(GsonManager.toJson(object));
            writer.flush();
            writer.close();
            SDIPlugin.instance.logInfo("zapisano : " + chests + " pozycji skrzyn!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserializeChests() {
        File f = new File(SDIPlugin.instance.getDataFolder() , "chestPosData.data");
        if (! f.exists())
            return;
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            r.lines().forEach(s -> sb.append(s));
            JsonObject object = GsonManager.fromJson(sb.toString() , JsonObject.class);
            JsonArray array = object.get("positions").getAsJsonArray();
            array.forEach(c -> {
                ChestDropState state = GsonManager.fromJson(c , ChestDropState.class);
                this.addChest(state);
            });
            int chests = array.size();
            array = object.get("schemats").getAsJsonArray();
            array.forEach( c-> {
                ChestScheme scheme = GsonManager.fromJson(c , ChestScheme.class);
                this.nameToChestScheme.put(scheme.name , scheme);
            });
            SDIPlugin.instance.logInfo("wczytano " + chests + " skrzynek !");
            SDIPlugin.instance.logInfo("wczytano " + array.size() + " schematow skrzyn !");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeChest(Location loc) {
        List<ChestDropState> l = this.chests.get(new Vector2int(loc.getChunk()));
        if (l == null)
            return;
        Iterator<ChestDropState> itr = l.iterator();
        Vector vec = loc.toVector();
        while (itr.hasNext()) {
            if (itr.next().position.equals(vec)) {
                itr.remove();
                break;
            }
        }
    }

    private static String permissionChest = "gamecore.editChest";

    public void onCommandChest(Player p , String args[] ) {
        if (! p.hasPermission(permissionChest)) {
            p.sendMessage("brak pozwolen , " + permissionChest);
            return;
        }
        if (args.length == 0) {
            return;
        }
        else if (args[0].contentEquals("list")) {
            sendChestSchemeList(p);
            return;
        }
        if (args.length >= 2) {
            if (args[0].contentEquals("edit")) {
                if (editorManager.get().getEditor(p) != null) {
                    p.sendMessage("masz otwarty edytor");
                    editorManager.get().getEditor(p).send();
                    return;
                }

                ChestScheme scheme = this.nameToChestScheme.get(args[1]);
                if (scheme == null) {
                    p.sendMessage("nie istnieje : " + args[1] + " tworze nowy");
                    scheme = new ChestScheme();
                    scheme.name = args[1];
                }

                ChatCommandEditor<ChestScheme> e = editorManager.get().createChatCommandEditor(p , scheme);
                WeakReference<ChatCommandEditor<ChestScheme>> _editor = new WeakReference<>(e);
                WeakReference<ChatEditorManager> _manager = new WeakReference<>(editorManager.get());
                WeakReference<ZombieGameManager> _instance = new WeakReference<>(this);

                e.addCommand("wyjdz" , c-> {
                    _manager.get().removeEditor(c);
                    c.sendMessage("usunięto !");
                });
                e.addCommand("dodaj" , c-> {
                    if (_instance.get().nameToChestScheme.containsKey(_editor.get().mainObject.name)) {
                        c.sendMessage("taka nazwa istnieje !! " + _editor.get().mainObject.name);
                        _editor.get().send();
                    }
                    else if (_editor.get().mainObject.name.isEmpty()) {
                        c.sendMessage("name nie moze byc null ani pusty !!!");
                        _editor.get().send();
                        return;
                    }
                    else {
                        _instance.get().nameToChestScheme.put(_editor.get().mainObject.name , _editor.get().mainObject);
                        c.sendMessage("dodano !");
                        _manager.get().removeEditor(c);
                        return;
                    }
                });
                this.nameToChestScheme.remove(args[1]);
                p.sendMessage("enabled");
                editorManager.get().enable(e , true);
                return;
            }
            else if (args[0].contentEquals("remove")) {
                nameToChestScheme.remove(args[1]);
                p.sendMessage("usunięto o ile było");
//                TODO a co ze skrzyniami ??
                return  ;
            }


        }
    }

    private CommandPointer pointer ;

    public ItemStack getItem(ChestScheme s) {
        ItemStack is = new ItemStack(Material.CHEST) ;
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("chest typeID: " + s.name);
        List<String> lore = new ArrayList<>();
        lore.add("random: " + s.random_ID);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public void sendChestSchemeList(Player p) {
        p.sendMessage("lista schematow " + this.nameToChestScheme.size());
        TextComponent tc = null;
        TextComponent in ;
        int i = 0;
        for (ChestScheme s : this.nameToChestScheme.values()) {
            if (i == 0)
                tc = new TextComponent();
            else
                tc.addExtra(", ");

            in = new TextComponent(s.name+":");
            in.setColor(ChatColor.GREEN);
            tc.addExtra(in);

            in = new TextComponent("[e]");
            in.setColor(ChatColor.RED);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND ,"/gamecore chest edit " + s.name));
            tc.addExtra(in);

            in = new TextComponent("[item]");
            in.setColor(ChatColor.RED);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND ,pointer.getCommand() + " " + s.name));
            tc.addExtra(in);

            tc.addExtra(" ");
            in = new TextComponent("[X]");
            in.setColor(ChatColor.RED);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND ,"/gamecore chest remove " + s.name));
            tc.addExtra(in);

            i++;
            if (i >= 4) {
                p.spigot().sendMessage(tc);
                i = 0;
            }
        }
        if (i != 0)
            p.spigot().sendMessage(tc);
    }


    public void addChest(ChestDropState s) {
        List<ChestDropState> l = this.chests.get(s.chunkpos);
        if (l == null) {
            l = new ArrayList<>();
            this.chests.put(s.chunkpos , l);
            if (getGameWorld().isChunkLoaded(s.chunkpos.x , s.chunkpos.y)) {
                this.activeChests.add(s);
//                TODO chunk wczytany a wiec cos ma sie dziac !
            }
        }
        l.add(s);
    }



    public  ZPlayer getZPlayer(Player p) {
        return nameToZPlayer.get(p.getName());
    }

    //<editor-fold desc="inheritance">


    @Override
    public void onDisable() {
        serializeChests();
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    private String permissions = "gamecore.usecommand";

    private String permissionsSave = "gamecore.usecommand.save";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (! commandSender.hasPermission(this.permissions)) {
            return false;
        }
//        TODO
        Player p = (Player) commandSender;
        if (strings.length >= 1) {
            if (strings[0].contains("save")) {
                if (! p.hasPermission(permissionsSave)) {
                    p.sendMessage("brak Pozwolen");
                    return true;
                }
                serializeChests();
                saveRandoms();
                p.sendMessage("gotowe");
                return true;
            }
            if (strings[0].contentEquals("random")) {
                onCommandListRandoms(p , Arrays.copyOfRange(strings , 1 , strings.length));
            }
            else if (strings[0].contentEquals("chest")) {
                onCommandChest(p , Arrays.copyOfRange(strings , 1 , strings.length));
            }
        }
        return true;
    }


    @Override
    public void onLateEnable() {
        new InitRandom();
        itemManager.registerItemComponent(GunComponent.class , "gunComponent");
        tickList = new ArrayList<>();
        nameToZPlayer = new HashMap<String , ZPlayer>();
        loadPlayers();
        Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance , r -> onTick() , 1,1);
        editorManager = new WeakReference<>(SDIPlugin.instance.getManager(ChatEditorManager.class));
        loadRandoms();
        deserializeChests();
        pointer = SDIPlugin.instance.getManager(GuiCommandManager.class).registerGuiCommand( (p,a) -> {
            ChestScheme s = nameToChestScheme.get(a);
            if (s == null) {
                p.sendMessage("niepoprawny schemat ? '" + a + "'");
                return;
            }
            if (p.getInventory().getItemInMainHand() == null) {
                p.getInventory().setItemInMainHand(getItem(s));
            }
            else
                p.getInventory().addItem(getItem(s));
            p.sendMessage("dodano przedmiot");
        } , ZombieGameManager.class , SDIPlugin.instance);
    }
    //</editor-fold>

    //<editor-fold desc="random">

    private HashMap<String , IRandomBlock> randomBlocks = new HashMap<>();


    public void saveRandoms() {
        File f = new File(SDIPlugin.instance.getDataFolder() , "random.sev");
        JsonArray array = new JsonArray();
        JsonObject o;
        for (Map.Entry<String , IRandomBlock> e : this.randomBlocks.entrySet()) {
            if (e.getValue() == null) {
                SDIPlugin.instance.logWarning(this, "to sie nie powinno satać");
                continue;
            }
            o = new JsonObject();
            o.addProperty("key" , e.getKey());
            o.add("value" , GsonManager.toJsonTree(e.getValue()));
            array.add(o);
        }
        try {
            BufferedWriter writter = new BufferedWriter(new FileWriter(f));
            writter.write(GsonManager.toJson(array));
            writter.flush();
            writter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IRandomBlock getRandom(String name) {
        return randomBlocks.get(name);
    }

    public void loadRandoms() {
        File f = new File(SDIPlugin.instance.getDataFolder() , "random.sev");
        if (f.exists() == false)
            return;
        JsonArray array = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(c -> sb.append(c));
            array = GsonManager.fromJson(sb.toString() , JsonArray.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.randomBlocks = new HashMap<>();
        array.forEach( e -> {
            JsonObject o = e.getAsJsonObject();
            String key = o.get("key").getAsString();
            IRandomBlock random = GsonManager.fromJson(o.get("value"), IRandomBlock.class);
            this.randomBlocks.put(key , random);
        });

    }

    public void onCommandListRandoms(Player p , String args[] ) {
        if (args.length == 0) {
            sendRandomListEditor(p);
            return;
        }
        else if (args.length >= 2) {
            if (args[0].contentEquals("remove")) {
                if (randomBlocks.remove(args[1]) == null) {
                    p.sendMessage(ChatColor.RED + "nie odnaleziono");
                }
                else {
                    p.sendMessage(ChatColor.GREEN + " usunięto");
                }
                sendRandomListEditor(p);
            }
            else if (args[0].contentEquals("edit")) {
                IRandomBlock b = this.randomBlocks.get(args[1]);
                if (b == null) {
                    p.sendMessage("nie odnaleziono bloku !");
                    return ;
                }
                if (editorManager.get().getEditor(p) != null) {
                    p.sendMessage("masz otwarty edytor !");
                    editorManager.get().getEditor(p).send();
                    return;
                }
                p.sendMessage("otwartoedytor");
                ChatCommandEditor e = editorManager.get().enableChatCommandEditor(p , b);
                e.send();//???? TODO potrzebne ?
                p.sendMessage("otwartoedytor");
            }
            else if (args[0].contentEquals("add")) {
                String n = args[1];
                if (this.randomBlocks.containsKey(n)) {
                    p.sendMessage("taka naza istnieje w przestrzeni nazw !");
                    return;
                }
                if (editorManager.get().getEditor(p) != null) {
                    p.sendMessage("masz otwarty edytor !");
                    editorManager.get().getEditor(p).send();
                    return;
                }
                p.sendMessage("tworze edytor");
                ChatCommandEditor<IRandomBlock> e = this.editorManager.get().createChatCommandEditor(p ,null);
                WeakReference<ZombieGameManager> _manager = new WeakReference<>(this);
                WeakReference<ChatCommandEditor<IRandomBlock>> _edit = new WeakReference<>(e);
                String _id = n;
                e.addCommand("dodaj" ,c -> {
                    _manager.get().randomBlocks.put(_id , _edit.get().mainObject);
                    _manager.get().editorManager.get().removeEditor(c);
                    c.sendMessage("dodano");
                    _manager.get().sendRandomListEditor(c);
                });
                e.type = IRandomBlock.class;
                editorManager.get().enable(e , true);
            }
        }
    }

    public void sendRandomListEditor(Player p) {
        p.sendMessage("randomList:");
        TextComponent tc ;
        TextComponent in;
        for (Map.Entry<String , IRandomBlock> e : randomBlocks.entrySet()) {
            tc = new TextComponent("- " + e.getKey() +" ");
            tc.setColor(ChatColor.DARK_GRAY);
            in = new TextComponent("[e]");
            in.setColor(ChatColor.BLUE);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , "/gamecore random edit " + e.getKey()));
            tc.addExtra(in);
            tc.addExtra(" ");
            in = new TextComponent("[X]");
            in.setColor(ChatColor.RED);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , "/gamecore random remove " + e.getKey()));
            tc.addExtra(in);
            p.spigot().sendMessage(tc);
        }
    }
    //</editor-fold>

    //<editor-fold desc="events">


    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        ItemStack is = e.getItemInHand();
        if (is.getType() == Material.CHEST) {
            if (is.hasItemMeta()) {
                ItemMeta im = is.getItemMeta();
                if (! im.hasDisplayName())
                    return;
                String s = im.getDisplayName();
                if (! s.contains("chest typeID: "))
                    return;
                s = s.replace("chest typeID: " , "");
                ChestScheme scheme = this.nameToChestScheme.get(s);
                if (scheme == null) {
                    e.getPlayer().sendMessage("nieodnaleziono schematu  '" + s + "'");
                    return;
                }
                ChestDropState state = new ChestDropState();
                state.chestScheme = s;
                state.lastOpenTime = System.currentTimeMillis() - (scheme.respawnTime*1000);
                state.position = e.getBlockPlaced().getLocation().toVector();
                state.chunkpos = new Vector2int(e.getBlockPlaced().getChunk());
                state.block = e.getBlockPlaced().getBlockData();
                this.addChest(state);

                e.getPlayer().sendMessage("dodano chest: " + s);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Vector2int pos = new Vector2int(e.getChunk());
        this.activeChests.removeIf(p -> p.chunkpos.equals(pos));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        List<ChestDropState> list = this.chests.get(new Vector2int(e.getChunk()));
        if (list != null) {
            this.activeChests.addAll(list);
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() !=  InventoryType.CHEST) {
            return;
        }
        if (e.getInventory().getHolder() instanceof Chest) {
            Chest c = (Chest) e.getInventory().getHolder();
            if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
//                List<ChestDropState> list = chests.get(new Vector2int(c.getChunk()));
//                if (list != null) {
//                    Vector v = c.getLocation().toVector();
//                    for (ChestDropState s : list) {
//                        if (! s.position.equals(v)) {
//                            continue;
//                        }
////                        TODO sprawdzenie ??
//
//                        ChestScheme scheme = nameToChestScheme.get(s.chestScheme);
//                        if (scheme == null) {
//                            SDIPlugin.instance.logWarning(this , "chestscheme id: " + s.chestScheme + " position " + s.position + " not exists !");
//                            return;
//                        }
//                        long current = System.currentTimeMillis();
//                        if (s.lastOpenTime + (1000*scheme.respawnTime) > current) {
//                            return;
//                        }
//                        s.lastOpenTime = current;
//                        IRandomBlock r = randomBlocks.get(scheme.random_ID);
//                        if (r == null) {
//                            SDIPlugin.instance.logWarning(this , "chestscheme id: " + s.chestScheme + " position " + s.position );
//                            SDIPlugin.instance.logWarning(this , "randomBlock: '" + scheme.random_ID + "' not exists !");
//                            return;
//                        }
//                        ItemStack[] is = c.getBlockInventory().getContents();
//                        List<ItemStack> itemList = new ArrayList<>();
//                        r.generate(1f , itemList);
//                        for (int i = 0 ; i < itemList.size() ; i++) {
//                            if (i >= is.length) {
//                                break;
//                            }
//                            is[i] = itemList.get(i);
//                        }
//                        e.getInventory().setContents(is);
//                        c.getBlockInventory().setContents(is);
//                        e.getPlayer().sendMessage("wygenerowano: " + itemList.size());
//                        return;
//                    }
//                }
            }
            else if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                List<ChestDropState> list = chests.get(new Vector2int(c.getChunk()));
                if (list != null) {
                    Vector v = c.getLocation().toVector();
                    for (ChestDropState s : list) {
                        if (!s.position.equals(v)) {
                            continue;
                        }
                        e.getPlayer().sendMessage("chest info: TODO");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        String name = e.getEntity().getCustomName();
        if (name != null && name.isEmpty() == false) {
            if (name.contentEquals("remove"))
                e.getEntity().remove();
        }
    }

    @EventHandler
    public void projectile(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.hasMetadata("gun")) {
                GunComponent component = ((WeakReference<GunComponent>) arrow.getMetadata("gun").get(0).value()).get();
                if (component != null) {
                    if (e.getEntity() instanceof LivingEntity)
                        component.damage((LivingEntity) arrow.getShooter(), (LivingEntity) e.getEntity());
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent e) {
        if (e.getInstaBreak()) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
                return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ZPlayer z = new ZPlayer(e.getPlayer().getUniqueId());
        tickList.add(z);
        nameToZPlayer.put(e.getPlayer().getName() , z);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        nameToZPlayer.remove(e.getPlayer().getName());
        removeTickingPlayer(e.getPlayer());
    }

    @EventHandler
    public void inventoryInteractEvent(InventoryClickEvent e) {
        ZPlayer p = getZPlayer((Player) e.getWhoClicked());
        if (p.isReloading()) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent e) {
        ZPlayer p = getZPlayer((Player) e.getWhoClicked());
        if (p.isReloading()) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    //</editor-fold>


}
