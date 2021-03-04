package com.niciel.superduperitems.fakeArmorstands;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class VectorAxisManipulator {



    public Vector vector;
    public Consumer<Vector> update;
    private CommandPointer moveAxis;

    public double a = 1;
    public double b = 0.25;
    public double c = 0.0625;

    public void enable() {
        WeakReference<VectorAxisManipulator> _instance = new WeakReference<>(this);
        moveAxis = SDIPlugin.instance.getManager(GuiCommandManager.class).registerGuiCommand((p, a) -> {
            if (a.length == 3) {
                String axis = a[1];
                double d ;
                try {
                    d = Double.parseDouble(a[2]);
                }
                catch (NumberFormatException e) {
                    p.sendMessage("niepoprawna wartosc liczbowa");
                    return;
                }
                if (axis.contains("set")) {
                    axis = axis.replaceAll("set" , "");
                    _instance.get().setPosition(d ,axis);
                }
                else {
                    if (! _instance.get().procesMoveAxis(d , axis)) {
                        p.sendMessage("niepoprawna os lub dzialanie sprzeczne !!");
                    }
                }

            }
        }, this.getClass() , SDIPlugin.instance );
    }

    public void setPosition(double d , String axis) {
        if (axis.contentEquals("x")) {
            vector.setX(d);
            update();
        }
        else if (axis.contentEquals("y")) {
            vector.setY(d);
            update();
        }
        else if (axis.contentEquals("z")) {
            vector.setZ(d);
            update();
        }
    }


    public void update() {
        if (this.update != null)
            this.update.accept(vector);
        System.out.println("update");
    }

    public void send(Player p) {
        send(p , "x" , ChatColor.RED);
        send(p , "y" , ChatColor.GREEN);
        send(p , "z" , ChatColor.BLUE);
    }

    public boolean procesMoveAxis(double d , String axis) {
        if (axis.contentEquals("x")) {
            vector.setX(vector.getX()+d);
            update();
        }
        else if (axis.contentEquals("y")) {
            vector.setY(vector.getY()+d);
            update();
        }
        else if (axis.contentEquals("z")) {
            vector.setZ(vector.getZ()+d);
            update();
        }
        else
            return false;
        return true;
    }

    public double offset = 1;

    public void sendAxisParticles(Player p , Vector add ) {
        Vector vector = this.vector.clone().add(add);
        double ad = offset*2+1;
        p.spawnParticle(Particle.REDSTONE , vector.getX()+ad , vector.getY() , vector.getZ() , 20 , offset ,0,0 ,0,
                new Particle.DustOptions(Color.RED , 1f));
        p.spawnParticle(Particle.REDSTONE , vector.getX() , vector.getY()+ad , vector.getZ() , 20 , 0 ,offset,0 ,0,
                new Particle.DustOptions(Color.GREEN , 1f));
        p.spawnParticle(Particle.REDSTONE , vector.getX() , vector.getY() , vector.getZ()+ad , 20 , 0 ,0,offset,0,
                new Particle.DustOptions(Color.BLUE , 1));

    }

    protected void send(Player p, String axis , ChatColor color) {
        if (vector == null) {
            p.sendMessage(" vector null wartosc niemozliwa do edycji !");
            return ;
        }
        TextComponent tc = new TextComponent();
        TextComponent in;
        in = new TextComponent("[<<<]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , moveAxis.getCommand()+" " + axis + " " + -a));
        in.setColor(color);
        tc.addExtra(in);
        tc.addExtra("[]");
        in = new TextComponent("[<<]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , moveAxis.getCommand()+" " + axis + " " + -b));
        in.setColor(color);
        tc.addExtra(in);
        tc.addExtra("[]");
        in = new TextComponent("[<]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , moveAxis.getCommand()+" " + axis + " " + -c));
        in.setColor(color);
        tc.addExtra(in);
        tc.addExtra("[] ");
        if (vector != null) {
            in = new TextComponent("[move]");
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , moveAxis.getCommand()+" " + axis + " " ));
        }
        else {
            in = new TextComponent("[setPosition]");
            in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder("\n\n" + vector.toString()).create()));
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , moveAxis.getCommand()+" set" + axis + " " ));
        }
        in.setColor(color);
        tc.addExtra(in);

        tc.addExtra(" []");
        in = new TextComponent("[>]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , moveAxis.getCommand()+" " + axis + " " + c));
        in.setColor(color);
        tc.addExtra(in);
        tc.addExtra("[]");

        in = new TextComponent("[>>]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , moveAxis.getCommand()+" " + axis + " " + b));
        in.setColor(color);
        tc.addExtra(in);
        tc.addExtra("[]");

        in = new TextComponent("[>>>]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , moveAxis.getCommand()+" " + axis + " " + a));
        in.setColor(color);
        tc.addExtra(in);

        p.spigot().sendMessage(tc);
    }

}
