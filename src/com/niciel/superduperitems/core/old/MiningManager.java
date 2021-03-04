package com.niciel.superduperitems.core.old;

import com.niciel.superduperitems.utils.IManager;
import com.niciel.superduperitems.utils.SpigotUtils;
import com.niciel.superduperitems.utils.Vector2int;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.util.BlockVector;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Random;

public class MiningManager implements IManager , Listener {


    private static Random random = new Random();

    private PlayerDataManager playerData;
    private HashMap<Vector2int, HashMap<BlockVector , BlockCrack>> chunks;

    private EnumMap<Material, Integer> blockPower;
    private int defaultValue;

    @Override
    public void onLateEnable() {
        playerData = getPlugin().getManager(PlayerDataManager.class);
        blockPower = new EnumMap(Material.class);
        defaultValue = 3;
        chunks = new HashMap<>();
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void blockBreakEvent(BlockBreakEvent e) {
        HashMap<BlockVector, BlockCrack> vec = chunks.get(new Vector2int(e.getBlock().getChunk())) ;
        if (vec == null) {
            vec = new HashMap<>();
            chunks.put(new Vector2int(e.getBlock().getChunk()) ,vec);
        }
        Block b = e.getBlock();
        BlockVector bvec = new BlockVector(b.getX() , b.getY() , b.getZ());
        BlockCrack crack = vec.get(bvec);
        if (crack == null) {
            crack = new BlockCrack(random.nextInt(Integer.MAX_VALUE) , 1);
            vec.put(bvec , crack);

        }
        int power;
        if (blockPower.containsKey(b.getType()))
            power = blockPower.get(b.getType());
        else
            power = defaultValue;

        if (crack.damage >= power) {
//            TODO blok normalnie nisczony
        }
        else {
            SpigotUtils.sendBlockCrackAnimation(e.getPlayer() , crack.ids , bvec , (int) (crack.damage/(float)power*9));
            crack.damage++;
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Vector2int vec = new Vector2int(e.getChunk());
        if (chunks.containsKey(vec))
            chunks.remove(vec);
    }


    public class BlockCrack {

        int ids;
        int damage;

        public BlockCrack(int ids, int level) {
            this.ids = ids;
            this.damage = level;
        }
    }

}
