package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
import com.niciel.superduperitems.inGameEditor.IBaseObjectEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;


public class EditorBoundBox extends IChatEditorMenu<BoundingBox> {

    private String selectCommand;
    private boolean selected;
    private int taskID;
    private boolean showRange;
    private String toogleShowRangeCommand;

    public EditorBoundBox(IBaseObjectEditor owner, String name, String description, Class clazz) {
        super(owner, name, description, clazz);
    }


    @Override
    public void onSelect(IChatEditorMenu menu) {
        selected = true;
    }

    @Override
    public void onDeselect() {

    }

    @Override
    public void enableEditor(IChatEditorMenu owner) {
        selected = false;
        selectCommand = owner.getTreeRoot().commands().register(new SimpleButtonGui( c-> {
            owner.getTreeRoot().select(this);
        }));
        toogleShowRangeCommand = owner.getTreeRoot().commands().register(new SimpleButtonGui( c-> {
            togleShowInRange();
        }));
        createRunnable();
    }

    protected void togleShowInRange() {
        showRange = ! showRange;
    }

    @Override
    public void disableEditor() {

    }

    @Override
    public void sendItem(Player p) {
        if (selected) {

        }
        else {
            TextComponent tc = new TextComponent("BoundingBox: ");
            tc.setColor(ChatColor.GRAY);
            TextComponent in = new TextComponent("[edit]");
            in.setColor(ChatColor.GREEN);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , selectCommand));
            tc.addExtra(in);
            tc.addExtra(" ");
            in = new TextComponent("[show:" + showRange +"]");
            in.setColor(ChatColor.GREEN);

            p.spigot().sendMessage(tc);
        }
    }

    public void createRunnable() {
        WeakReference<EditorBoundBox> instance = new WeakReference<>(this);
        Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance, t-> {
            if (instance.isEnqueued()) {
                t.cancel();
                return;
            }
            Player p = instance.get().getTreeRoot().getPlayer();
            if (p.isOnline() == false)
            {
                t.cancel();
                return;
            }
            if (instance.get().showRange) {
                instance.get().showGrid(p);
            }
        },1,10);
    }

    protected void showGrid(Player p) {
        BoundingBox box = getReference().getValue();
        showCorner(p , box.getMin() , box,Color.GREEN);
        showCorner(p , box.getMax() , box,Color.RED);
        //na ten moment tylko tyle :D
    }

    private static double particleCountPerBlock = 5;
    private static double step = 1/particleCountPerBlock;

    private void showCorner(Player p , Vector position ,BoundingBox box, Color color) {
        double x,y,z ;
        x = Math.signum(box.getCenterX()-position.getX());
        y = Math.signum(box.getCenterY()-position.getY());
        z = Math.signum(box.getCenterZ()-position.getZ());
        double a,b,c;
        a = position.getX();
        b = position.getY();
        c = position.getZ();
        Particle.DustOptions d = new Particle.DustOptions(color,1);
        double h;
        for (int i = (int) particleCountPerBlock ; i < particleCountPerBlock ; i++) {
            h = i*step;
            p.spawnParticle(Particle.REDSTONE , a+(h*x),b,c , 1 , 0,0,0,1 , d);
            p.spawnParticle(Particle.REDSTONE , a,b+(h*y),c , 1 , 0,0,0,1 , d);
            p.spawnParticle(Particle.REDSTONE , a,b,c +(h*z), 1 , 0,0,0,1 , d);

        }
    }

}
