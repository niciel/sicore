package com.niciel.superduperitems.chunkdatastorage;

import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.utils.*;
import com.niciel.superduperitems.utils.Ref;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.rmi.UnexpectedException;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChunkWorldManager  {

    private World world;
    private HashMap<Vector2int, ChunkData> data;
    private Function<Player, IChunkPlayerData> dataProvider;
    private BukkitTask runnable;
    private Connection database;
    private File databaseFile;


    private HashMap<Vector , ICustomBlock> objects;
    private HashMap<UUID, List<Vector>> registeryBlocks;

    public void addBlock(UUID owner , Vector v , ICustomBlock block) {
        Block b = world.getBlockAt(new Location(world , 0,0,0).add(v));
        v = b.getLocation().toVector();
        if (objects.containsKey(v))
            return;
        List<Vector> reg = registeryBlocks.get(owner);
        if (reg == null) {
            reg = new ArrayList<>();
            registeryBlocks.put(owner , reg);
        }
        reg.add(v.clone());
        objects.put(v , block);
    }

    public void removeBlocks(UUID owner) {
        List<Vector> reg = registeryBlocks.remove(owner);
        if (reg != null) {
            reg.forEach(c-> objects.remove(c));
        }
    }

    public void removeBlock(UUID owner , Vector v) {
        List<Vector> reg = registeryBlocks.get(owner);
        if (reg != null) {
            for (int i = 0 ; i < reg.size() ; i++) {
                if (reg.get(i).equals(v)) {
                    objects.remove(v);
                    reg.remove(i);
                    break;
                }
            }
            if (reg.isEmpty()) {
                registeryBlocks.remove(owner);
            }
        }
    }

    private boolean LOAD_CHUNK_VALUE = true;
    private boolean UNLOAD_CHUNK_VALUE = false;

    private Function<AsyncDataCallBack<AsyncOperation> , Boolean> loadFunction;
    private Consumer<AsyncDataCallBack<AsyncOperation>> loadConsumer;
    private HashMap<Vector2int , AsyncDataCallBack<AsyncOperation>> asyncOperation;

    private Function<AsyncDataCallBack<AsyncOperation> , Boolean> saveFunction;
    private Consumer<AsyncDataCallBack<AsyncOperation>> saveConsumer;

    private HashMap<Vector2int , Boolean> syncOperation;




    public ChunkWorldManager(World w , Function<Player, IChunkPlayerData> provider ) {
        this.data = new HashMap<>();
        this.asyncOperation = new HashMap<>();
        this.syncOperation = new HashMap<>();
        this.world = w;
        this.dataProvider = provider;
    }

    public void enable(ChunkManager manager) {
        this.objects = new HashMap<>();
        this.registeryBlocks = new HashMap<>();
        this.initConsumers_();

        runnable = Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance , () -> {
            trackPlayer();
        } ,1,10);

        databaseFile = new File(world.getWorldFolder() , "chunkworldmanager.db");
        try {
            if (database != null)
                database.close();
            database = SpigotUtils.connectToDatabase(databaseFile);
        } catch (UnexpectedException | SQLException e) {
            e.printStackTrace();
            SDIPlugin.instance.logWarning(this , "brak database !!");
//            TODO cos z tym trzeba zrobic !
        }
        String querry = "CREATE TABLE IF NOT EXISTS worlddata (" +
                "vector INT8 PRIMARY KEY," +
                "data TEXT NOT NULL" +
                ");";
        try {
            PreparedStatement statment = database.prepareStatement(querry);
            if (statment.execute()) {
                SDIPlugin.instance.logInfo("baza danych swiata: " + world.getName() + " zaladowana pomyslnie");
            }
            else {
                SDIPlugin.instance.logWarning(this , "baza danych swiata: " + world.getName() + " nie zostala wczytana");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadLoadedChunks_();
    }

    protected void initConsumers_() {
        WeakReference<ChunkWorldManager> _instance = new WeakReference<>(this);
        loadFunction = (a) -> {
            try {
                String querry = "SELECT data FROM worlddata WHERE vector = ?";
                PreparedStatement statement = _instance.get().getConnection().prepareStatement(querry);
                statement.setBigDecimal(1 , new BigDecimal(ChunkWorldManager.convert(a.getData().vector)));
                ResultSet result = statement.executeQuery();
                if (result == null ) {
                    statement.close();
                    result.close();
                    return true;
                }
                if (! result.next()){
                    a.getData().data = null;
                    statement.close();
                    result.close();
                    return  true;
                }
                String out = result.getString("data");
                ChunkData data = (ChunkData) SDIPlugin.instance.getGson().fromJson(out , GsonSerializable.class);
                a.getData().data = data;
                statement.close();
                result.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        };

        loadConsumer = a-> {
            if (a.isAsyncEnds()) {
                if (a.isSuccess()) {
                    if (a.getData().STOP_action) {
//                        TODO
                    }
                    else {
                        ChunkData d = a.getData().data;
                        if (d != null) {
                            _instance.get().addChunk(d);
                        }
                    }
                }
                else {
                    SDIPlugin.instance.logWarning(this , "nie udalo sie wczytac chunka !!! " + a.getData().vector);
                }
                _instance.get().asyncOperation.remove(a.getData().vector);
                _instance.get().syncOperation.remove(a.getData().vector);
            }
            _instance.get().asyncOperation.remove(a.getData().vector);
        };

        saveFunction = a-> {
            String o = GsonManager.toJson(a.getData().data);
            try {
                PreparedStatement ps = getConnection().prepareStatement("UPDATE worlddata SET " +
                        "data = ? WHERE vector = ?");
                ps.setString(1 , o );
                ps.setBigDecimal(2 , new BigDecimal(convert(a.getData().vector)));
                ps.execute();
                ps.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        };

        saveConsumer = a-> {
            if (a.isAsyncEnds()) {
                if (a.isSuccess()) {
                    if (a.getData().STOP_action) {
//                        TODO
                    }
                    else {
                        //a.getData().data.disableChunk();
                        _instance.get().removeChunk(a.getData().vector);
                    }
                    _instance.get().asyncOperation.remove(a.getData().vector);
                    _instance.get().syncOperation.remove(a.getData().vector);
                }
            }
            _instance.get().asyncOperation.remove(a.getData().vector);

        };
    }


    protected void loadLoadedChunks_() {
        List<Vector2int> toLoad = new ArrayList<>();
        for (Chunk c : world.getLoadedChunks()) {
            toLoad.add(new Vector2int(c));
        }
        SDIPlugin.instance.logInfo("wczytuje aktualne chujki: " + toLoad.size());
        if (toLoad.isEmpty())
            return;

        int size = toLoad.size();
        StringBuilder sb = new StringBuilder("SELECT data FROM worlddata WHERE vector IN (?");
        for (int i = 1 ; i < size ;i++) {
            sb.append(",?");
        }
        sb.append(");");

        try {
            PreparedStatement statement = getConnection().prepareStatement(sb.toString());
            for (int i = 0 ; i < size ; i++) {
                statement.setBigDecimal(i+1 , new BigDecimal(convert(toLoad.get(i))));
            }
            ResultSet set  = statement.executeQuery();
            String data;
            ChunkData d;
            List<ChunkData> list = new ArrayList<>();
            while (set.next()) {
                data = set.getString("data");
                d = GsonManager.fromJson(data , ChunkData.class);
                list.add(d);
            }
            Vector2int wypizdowieWIELKIE = new Vector2int(Integer.MIN_VALUE , Integer.MIN_VALUE);
            for (Player p : world.getPlayers()) {
                dataProvider.apply(p).move(wypizdowieWIELKIE);
            }
            for (ChunkData c : list) {
                addChunk(c);
            }
            SDIPlugin.instance.logInfo("wczytano z bazy danych: " + list.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void addChunkObject(Vector vec , IChunkObject obj) {
        Vector2int v = new Vector2int(new Location(world , vec.getX() , vec.getY() , vec.getZ()).getChunk());
        Ref ref = new Ref(false);
        ChunkData d = getOrCreateChunkData(v );

        d.addElement(obj);
    }

    protected void addChunk(ChunkData d) {
        data.put(d.getPosition() , d);
        d.enableChunk(this);
    }


    protected void removeChunk(Vector2int d) {
        ChunkData data = getChunkData(d);
        if (data != null) {
            data.disableChunk();
            this.data.remove(d);
        }
    }

    public void disable(ChunkManager manager) {
        runnable.cancel();
        try {
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public World getWorld() {
        return  world;
    }

    public void onBlockBreak(BlockBreakEvent e) {
        ICustomBlock c = objects.get(e.getBlock().getLocation().toVector());
        if (c != null)
            c.onBreak(e);
    }



    public ChunkData getChunkData(Vector v ) {
        return getChunkData(new Location(world , v.getX() , v.getY() , v.getZ()));
    }

    public ChunkData getChunkData(Location loc ) {
        return getChunkData(new Vector2int(loc.getChunk()));
    }

    public ChunkData getChunkData(Vector2int v) {
        return data.get(v);
    }

    public AsyncDataCallBack<AsyncOperation> getOperation(Vector2int v) {
        return asyncOperation.get(v);
    }


    public void loadChunk(Vector2int vec) {
        loadChunk(vec , loadConsumer);
    }

    public boolean loadChunk(Vector2int v , Consumer<AsyncDataCallBack<AsyncOperation>> sync) {
        AsyncDataCallBack<AsyncOperation> d = asyncOperation.get(v);
        if (v != null) {
//            TODO
            return false;
        }
        AsyncOperation operation = new AsyncOperation(false , v);
        d = AsyncDataCallBack.createAndRun(SDIPlugin.instance , loadFunction , sync , operation);
        return true;
    }



    public ChunkData getOrCreateChunkData(Location l ) {
        return getOrCreateChunkData(new Vector2int(l.getChunk()) );
    }


    /**
     *
     * @param v chunkvector
     * @return ZAWSZE ZWROCI CHUNK DATA (pobierze z bazy danych lub stworzy i doda nowy !!!) AKCJA SYNCHRONICZNA !!!!
     */
    public ChunkData getOrCreateChunkData(Vector2int v ) {
        ChunkData c = getChunkData(v);
        if (c== null) {
//            TODO
            String querry;
            try {
                querry = "SELECT data FROM worlddata WHERE vector = ?";
                PreparedStatement st = getConnection().prepareStatement(querry);
                st.setBigDecimal(1 , new BigDecimal(convert(v)));
                ResultSet res = st.executeQuery();
                if (res.next()) {
                    String object = res.getString("data");
                    c = (ChunkData) SDIPlugin.instance.getGson().fromJson(object , GsonSerializable.class);
                    res.close();
                    st.close();
                }
                else {
                    c = new ChunkData(v);
                    querry = "INSERT INTO worlddata (vector , data) VALUES(?,?);";
                    st = getConnection().prepareStatement(querry);
                    st.setBigDecimal(1  , new BigDecimal(convert(v)));
                    st.setString(2 , GsonManager.toJson(c));
                    st.execute();
                    st.close();

                }
                data.put(v , c);
                c.enableChunk(this);
                c.setChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return c;
    }


    public void onBlockDamage(BlockDamageEvent e) {
        ICustomBlock c = objects.get(e.getBlock().getLocation().toVector());
        if (c!=null)
            c.onBlockDamage(e);
    }



    public String getWorldName() {
        return world.getName();
    }

    public void trackPlayer() {

        List<ChunkData> last = new ArrayList<>();
        List<ChunkData> newc = new ArrayList<>();
        IChunkPlayerData d ;
        Vector2int vector;
        Vector2int previus;
        int view;
        Iterator<ChunkData> itr;
        Vector2int v;
        for (Player p : world.getPlayers()) {
            d = dataProvider.apply(p);
            if (d == null) {
                SDIPlugin.instance.logWarning(this , "DataProvider CannotBe null !!");
                continue;
            }
            view = d.viewDistance();
            vector = new Vector2int(p.getLocation().getChunk());
            previus = d.getPreviusChunk();
            if (previus == null) {
                addAround(vector , view , c-> {
                    c.playersInRange.add(p);
                });
                d.move(vector);
                return;
            }
            if (previus.equals(vector))
                continue;
            d.move(vector);

            newc.clear();
            last.clear();

            addAround(previus , view , c-> last.add(c));
            addAround(vector , view , c-> newc.add(c));

            itr = newc.iterator();
            while (itr.hasNext()) {
                v = itr.next().getPosition();
                for (int i = 0 ; i < last.size() ; i++) {
                    if (v.equals(last.get(i).getPosition())) {
                        last.remove(i);
                        itr.remove();
                        break;
                    }
                }
            }

            for (ChunkData c : newc) {
                c.enterViewDistance(p,d);
            }
            for (ChunkData c : last) {
                c.exitDistance(p,d);
            }
        }
    }


    protected void exitRange(Player p ) {
        IChunkPlayerData d = dataProvider.apply(p);
        Vector2int vec = d.getPreviusChunk();
        addAround(vec , d.viewDistance() , c-> c.exitDistance(p,d));
    }



    public static long convert(Vector2int v) {
        return convert(v.x , v.y);
    }


    public static long convert(int x , int z) {
        long a = 0;
        a = x;
        a = a <<32;
        a = a|z;
        return a;
    }

    public Connection getConnection() {
        try {
            if (database.isClosed()) {
                SDIPlugin.instance.logWarning(this , "ponowene laczenie z baza danych!");
                database = SpigotUtils.connectToDatabase(databaseFile);
            }
        } catch (SQLException | UnexpectedException e) {
            e.printStackTrace();
        }
        return database;
    }

    protected void addAround( Vector2int v , int view , Consumer<ChunkData> wtf) {
        Vector2int nv = new Vector2int(0,0);
        ChunkData d;
        for (int x = -view ; x <= view ; x++) {
            for (int y = - view ; y <= view ; y++) {
                nv.x = x + v.x;
                nv.y = y + v.y;
                d = data.get(nv);
                if (d != null)
                    wtf.accept(d);
            }
        }
    }


    public void onPlayerQuit(Player p) {
        exitRange(p);
    }

    public void onPlayerTeleportToOtherWorld(Player p) {
        exitRange(p);
    }

    public class AsyncOperation {

        public ChunkData data;
        public final boolean serialization;
        public final Vector2int vector;

        public boolean STOP_action;

        public AsyncOperation(boolean serialization, Vector2int vector) {
            this.serialization = serialization;
            this.vector = vector;
        }
    }


    public void onChunkLoad(ChunkLoadEvent e) {
        Vector2int vec = new Vector2int(e.getChunk());
        syncOperation.put(vec , LOAD_CHUNK_VALUE);
        AsyncDataCallBack<AsyncOperation> t = getOperation(vec);
        if (t != null) {
            if (t.getData().serialization == true) {
                t.getData().STOP_action = true;
            }

            else if (t.isAsyncEnds())
                asyncOperation.remove(vec);
        }
        else {
            t = AsyncDataCallBack.createAndRun(SDIPlugin.instance , loadFunction , loadConsumer , new AsyncOperation(false , vec));
            asyncOperation.put(vec , t);
        }
    }

    public boolean removeIChunkObject(UUID uuid) {
        for (ChunkData d : data.values()) {
            if (d.remove(uuid)){
                return true;
            }
        }
        return false;
    }

    public void onChunkUnload(ChunkUnloadEvent e) {
        Vector2int vec = new Vector2int(e.getChunk());
        ChunkData d = getChunkData(vec);
        if (d == null)
            return;
        d.disableChunk();
        if (d.isChanged()) {
            syncOperation.put(vec , UNLOAD_CHUNK_VALUE);
            AsyncDataCallBack<AsyncOperation> t = getOperation(vec);
            if (t != null) {

                if (t.getData().serialization == false) {
                    t.getData().STOP_action = true;
                }
                else if (t.isAsyncEnds())
                    asyncOperation.remove(vec);
            }
            else {
                AsyncOperation op = new AsyncOperation(true , vec);
                op.data = d;
                asyncOperation.put(vec , t);
                t = AsyncDataCallBack.createAndRun(SDIPlugin.instance , saveFunction , saveConsumer ,op);
            }
        }
    }

}
