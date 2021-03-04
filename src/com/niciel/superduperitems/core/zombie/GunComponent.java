package com.niciel.superduperitems.core.zombie;

import com.google.gson.JsonObject;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class GunComponent implements ItemComponent , Serializable {

    private static ZombieGameManager manager = SDIPlugin.instance.getManager(ZombieGameManager.class);
    private static int GUNS_IDS = 0;
    public final int gunID = GUNS_IDS++;

    @ChatEditable(name = "damage")
    private double dmg = 8;
    @ChatEditable(name = "bulletSpeed")
    private float speed = 3;
    @ChatEditable(name = "spread")
    private float spreed = 12;
    @ChatEditable(name = "sneakingSpread")
    private float crauhingSpreed = 6;
    @ChatEditable(name = "reloadTime")
    private int reloadTime = 1;
    @ChatEditable(name = "magazine")
    private int magazineCount = 30;

    @ChatEditable(name = "distance")
    private  double bulletDistance = 20;
    @ChatEditable(name = "ammunition")
    private int ammunitionModelID = 0;



    @ChatEditable(name = "shootDelay")
    private int shootDelay = 1;
    @ChatEditable(name = "bullet per shot")
    private int shootsPerDelay = 1;



    @ChatEditable
    private int arrowsInShoot;

    @ChatEditable
    private String shootSound = null;
    @ChatEditable
    private float shootVolume = 100;
    @ChatEditable
    private float shootPitch = 100;

    @Override
    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("damage" ,dmg);
        o.addProperty("speed" ,speed);
        o.addProperty("spreed" ,spreed);
        o.addProperty("shiftSpreed" ,crauhingSpreed);
        o.addProperty("shotCount" , shootDelay);
        o.addProperty("reloadTime" ,reloadTime);
        o.addProperty("magazineSize" ,magazineCount);
        o.addProperty("distance" , bulletDistance);
        o.addProperty("ammunition" , ammunitionModelID);
        o.addProperty("ammunitionPerShot" , shootsPerDelay);
        o.addProperty("arrowsInShoot" , arrowsInShoot);

        o.addProperty("shootSound" , shootSound);
        o.addProperty("shootVolume" , shootVolume);
        o.addProperty("shootPitch" , shootPitch);
        return o;
    }

    @Override
    public void deserialize(JsonObject e) {
        JsonObject o = e.getAsJsonObject();
        dmg = o.get("damage").getAsInt();
        speed = o.get("speed" ).getAsFloat();
        spreed = o.get("spreed").getAsFloat();
        crauhingSpreed = o.get("shiftSpreed").getAsFloat();
        shootDelay = o.get("shotCount").getAsInt();
        reloadTime = o.get("reloadTime").getAsInt();
        magazineCount = o.get("magazineSize").getAsInt();
        bulletDistance = o.get("distance").getAsDouble();
        ammunitionModelID = o.get("ammunition").getAsInt();
        if (o.has("ammunitionPerShot"))
            shootsPerDelay = o.get("ammunitionPerShot").getAsInt();
        if (o.has("arrowsInShoot"))
            this.arrowsInShoot = o.get("arrowsInShoot").getAsInt();

        if (o.has("shootSound")) {
            shootSound = o.get("shootSound").getAsString();
            shootVolume = o.get("shootVolume").getAsFloat();
            shootPitch = o.get("shootPitch").getAsFloat();
        }

    }



    private int maxDurability;
    private double bulletDurrability;

    private int taskDelay;

    private Material material;

    @Override
    public void onEnable(CustomItem ci) {
        maxDurability = ci.material.getMaxDurability();
        bulletDurrability = (double) maxDurability /magazineCount;
        this.taskDelay = shootDelay/shootsPerDelay;
        material = ci.material;
    }


    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType().isInteractable())
                return;
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        Player p = e.getPlayer();
        ZPlayer zp = manager.getZPlayer(p);
        if (zp.drop) {
            zp.drop = false;
            return;
        }
        e.setCancelled(true);
        if (zp.isReloading())
            return;
        int deltaTick = SDIPlugin.instance.tick - zp.getLastShootTick();
        if (deltaTick <= this.shootDelay) {
            return;
        }
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Damageable meta = (Damageable) item.getItemMeta();
        int magazie = inMagazine(meta);
        if (magazie <= 0) {
            beginReload(zp , meta , magazie);
            return;
        }
        shoot(zp ,meta , magazie, item);
    }


    public void shoot(ZPlayer zp , Damageable meta , int magazineSize ,ItemStack item) {
        Player p = zp.player.get();
        double pitch = ((p.getLocation().getPitch() + 90) * Math.PI) / 180;
        double yaw  = ((p.getLocation().getYaw() + 90)  * Math.PI) / 180;
        double x,y,z;
        x = Math.sin(pitch) * Math.cos(yaw);
        y = Math.sin(pitch) * Math.sin(yaw);
        z = Math.cos(pitch);
        Vector v = new Vector(x,z,y);
        v.normalize();
        v.multiply(0.7);
        int newMagazineSize = 0;
        if (shootsPerDelay == 1) {
            if (shootSound != null)
                zp.getPlayer().getWorld().playSound(zp.getPlayer().getLocation() , shootSound , shootVolume , shootPitch);
            for (int i = 0 ; i < arrowsInShoot ; i++) {
                if (p.isSneaking())
                    spawnArrow(p , p.getEyeLocation().add(v) , v , speed  ,  crauhingSpreed , dmg);
                else
                    spawnArrow(p , p.getEyeLocation().add(v) , v , speed  ,  spreed , dmg);
            }
            //zp.getPlayer().sendMessage("shoots " + 1);
            newMagazineSize = magazineSize-1;
        }
        else {
            int shoots;
            newMagazineSize = magazineSize -shootsPerDelay;
            if (newMagazineSize >= 0) {
                shoots = shootsPerDelay;
            }
            else {
                shoots = shootsPerDelay + newMagazineSize;
                newMagazineSize = 0;
            }
            //zp.getPlayer().sendMessage("shoots " + shoots + " delay " + this.taskDelay);
            WeakReference<ZPlayer> _player = new WeakReference<>(zp);
            Ref<Integer> i = new Ref<>(shoots);
            Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance, (Consumer<BukkitTask>) c -> {
                int a = i.getValue();
                if (a <= 0)
                    c.cancel();
                else {
                    i.setValue(a-1);
                    spawnArrow(p , p.getEyeLocation() , v , speed ,spreed,dmg);
                }
            },1,taskDelay);
//            TODO
        }
        //zp.getPlayer().sendMessage("przed " + magazineSize + " bedzie " + newMagazineSize);

        zp.setLastShootTick(SDIPlugin.instance.tick);
        int dmg = (int) ((this.magazineCount - newMagazineSize) * bulletDurrability);
        if (dmg >= maxDurability) {
            dmg = maxDurability - 1;
            beginReload(zp , meta , newMagazineSize);
        }
        meta.setDamage(dmg);
        item.setItemMeta((ItemMeta) meta);
        p.getInventory().setItemInMainHand(item);





//        Player p = zp.player.get();
//        double pitch = ((p.getLocation().getPitch() + 90) * Math.PI) / 180;
//        double yaw  = ((p.getLocation().getYaw() + 90)  * Math.PI) / 180;
//        double x,y,z;
//        x = Math.sin(pitch) * Math.cos(yaw);
//        y = Math.sin(pitch) * Math.sin(yaw);
//        z = Math.cos(pitch);
//        Vector v = new Vector(x,z,y);
//        v.normalize();
//        Arrow arrow = null;
//        int shoots = shootDelay;
//        if (magazineSize < shootDelay)
//            shoots = magazineSize;
//        for (int i = 0 ; i < shoots ; i++) {
//            if (p.isSneaking())
//                spawnArrow(p , p.getEyeLocation().add(v) , v , speed , crauhingSpreed ,dmg);
//            else
//                spawnArrow(p , p.getEyeLocation().add(v) , v , speed , spreed ,dmg);
//        }
//        shoots = magazineSize - shoots;
//        int dmg = (int) ((magazineCount - shoots )* bulletDurrability);
//        if (dmg >= maxDurability) {
//            dmg = maxDurability - 1;
//            beginReload(zp , meta , shoots);
//        }
//        meta.setDamage(dmg);
//        item.setItemMeta((ItemMeta) meta);
//        p.getInventory().setItemInMainHand(item);
    }

    public void spawnArrow(Player shooter , Location position , Vector direction , float speed, float spreed , double dmg ) {
        Arrow arrow = shooter.getWorld().spawnArrow(position , direction , speed , spreed );
        arrow.setCustomName("remove");
        arrow.setCustomNameVisible(false);
        arrow.setDamage(dmg);
        arrow.setBounce(false);
        arrow.setShooter(shooter);
        arrow.setMetadata("gun" , new FixedMetadataValue(SDIPlugin.instance , new WeakReference<>(this)));
    }

    public void reloadTick(ZPlayer p , int ammunition , int reloadingTick) {
        if (reloadingTick >= reloadTime) {
            p.cancelReloading();
            endReloading(p , ammunition);
        }
        else {
            ItemStack is = p.player.get().getInventory().getItemInMainHand();
            Damageable dmg = (Damageable) is.getItemMeta();
            float val = 1f - ((float) reloadingTick/reloadTime);
            int damage = (int) (val*material.getMaxDurability());
            dmg.setDamage(damage);
            is.setItemMeta((ItemMeta) dmg);
            p.player.get().getInventory().setItemInMainHand(is);
        }
    }



    public int inMagazine(Damageable dmg) {
        int left = material.getMaxDurability() - dmg.getDamage();
        double close = dmg.getDamage() % bulletDurrability;
        return (int) (left / bulletDurrability);
    }

    public void beginReload(ZPlayer zp , Damageable meta , int inMagazine) {
        if (zp.isReloading())
            return;
        else {
            if (inMagazine == magazineCount)
                return;
            int dif = magazineCount-inMagazine;
            int ammunition = SpigotUtils.removeItem(zp.player.get().getInventory() , pr -> {
                if (pr.hasItemMeta()) {
                    ItemMeta im = pr.getItemMeta();
                    if (im.hasCustomModelData()) {
                        if (ammunitionModelID == im.getCustomModelData())
                            return true;
                    }
                }
                return false;
            } , dif);
            if (ammunition > 0) {
                zp.beginReload(this ,ammunition+inMagazine);
            }
            else {
                zp.player.get().sendMessage("brak amunicji :l");
            }
        }
    }

    public void endReloading(ZPlayer player , int ammunition ) {
        Player p = player.player.get() ;
        p.playEffect(EntityEffect.FIREWORK_EXPLODE);
        ItemStack is = p.getInventory().getItemInMainHand();
        Damageable d = (Damageable) is.getItemMeta();
        d.setDamage((int) ((magazineCount -ammunition)*bulletDurrability));
        is.setItemMeta((ItemMeta) d);
        p.getInventory().setItemInMainHand(is);
    }

    public void damage(LivingEntity damager , LivingEntity damaged) {
        damaged.damage(dmg);
        damaged.setNoDamageTicks(0);
    }

    @EventHandler
    public void itemHeldEvent(PlayerItemHeldEvent e) {
        ZPlayer zp = manager.getZPlayer(e.getPlayer());
        if (zp.isReloading())
            e.setCancelled(true);
    }

    @EventHandler
    public void itemDropEvent(PlayerDropItemEvent e) {
        ZPlayer zp = manager.getZPlayer(e.getPlayer());
        if (zp.isReloading() == false) {
            Damageable d = (Damageable) e.getItemDrop().getItemStack().getItemMeta();
            int magazine = inMagazine(d);
            if (magazine != magazineCount) {
                beginReload(zp , d ,magazine);
            }
            else {
                e.getPlayer().sendMessage("nie wymaga przeladowania !");
                zp.drop = true;
            }
        }

        e.setCancelled(true);
    }


    public class Shoots extends BukkitRunnable {

        public WeakReference<ZPlayer> player;
        public int shoots ;
        public double dmg;
        public float spread;
        public float speed;


        @Override
        public void run() {
            if (player.get() == null) {
                this.cancel();
                return;
            }
            Player p = player.get().getPlayer();
            if (p == null) {
                this.cancel();
                return;
            }


        }

    }

}
