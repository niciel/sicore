package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
import com.niciel.superduperitems.inGameEditor.IBaseObjectEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorMenu;
import com.niciel.superduperitems.utils.IRefSilent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;


public class EditorChatVector extends ChatEditorMenu<Vector> {


    private static HashMap<String , Get> map;
    private String command;
    private String showAxisCommand;
    private String commandEdit ;

    private boolean showaxis;
    private boolean canceled = false;

    private boolean selected;

    private static DecimalFormat decimal = new DecimalFormat("####.##");

    public EditorChatVector(IBaseObjectEditor owner, String name, String description, Class clazz) {
        super(owner, name, description);
    }

    @Override
    public void sendMenu() {
        if (getReference().getValue() == null) {
            ((IRefSilent) getReference()).setSilently(new Vector(0,0,0));
        }
        Player p = getTreeRoot().getPlayer();
        p.sendMessage("Value: " + formatDecimalVector());
        TextComponent tc = new TextComponent("showAxis: " + showaxis);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,showAxisCommand));
        p.spigot().sendMessage(tc);
        TextComponent in;

        tc = new TextComponent();
        in = new TextComponent("[player]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , command + " here"));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("[eye]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , command + " eye"));
        tc.addExtra(in);
        tc.addExtra(", ");


        in = new TextComponent("X");
        in.setColor(ChatColor.BLUE);
        in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(getReference().getValue().getX() +"").create()));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[s]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command + " x "));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[a]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command + " addx "));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("Y");
        in.setColor(ChatColor.BLUE);
        in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(getReference().getValue().getY() +"").create()));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[s]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command + " y "));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[a]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command + " addy "));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("Z");
        in.setColor(ChatColor.BLUE);
        in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(getReference().getValue().getZ() +"").create()));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[s]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command + " z "));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[a]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command + " addz "));
        tc.addExtra(in);

        p.spigot().sendMessage(tc);
    }

    @Override
    public void onSelect(ChatEditorMenu menu) {
        selected = true;
        WeakReference<EditorChatVector> _instance = new WeakReference<>(this);
        command = menu.getTreeRoot().commands().register((pl, args , i) -> {
            if (args.length >= i) {
                args = Arrays.copyOfRange(args , i,args.length);
                if (args[0].contentEquals("here")) {
                    Vector v = pl.getLocation().toVector();
                    _instance.get().getReference().setValue(v);
                }
                else if (args[0].contentEquals("eye")) {
                    Vector v = pl.getEyeLocation().toVector();
                    _instance.get().getReference().setValue(v);
                }
                else if (args.length >=2){
                    Double d;
                    try {
                        d = Double.parseDouble(args[1]);
                    }
                    catch (NumberFormatException e) {
                        pl.playEffect(EntityEffect.HURT);
                        return;
                    }
                    _instance.get().procesAxis(args[0] , d);
                }
            }
        });
        showAxisCommand = menu.getTreeRoot().commands().register(new SimpleButtonGui(p-> {
           _instance.get().toggleAxisShow();
           _instance.get().getTreeRoot().sendMenu();
        }));

        canceled = false;
        Bukkit.getScheduler().runTaskTimer(SDIPlugin.instance , c -> {
            if (canceled) {
                c.cancel();
                return;
            }
            if (_instance.isEnqueued() == false) {
                Vector pos = _instance.get().getReference().getValue();
                Player p =  _instance.get().getTreeRoot().getPlayer();
                if (p.isOnline() == false) {
                    c.cancel();
                    return;
                }
                if (_instance.get().showaxis == false)
                    return;
                int max =5;
                double d = 1d/max;

                Particle.DustOptions xAxis = new Particle.DustOptions(Color.RED , 1f);
                Particle.DustOptions yAxis = new Particle.DustOptions(Color.GREEN , 1f);
                Particle.DustOptions zAxis = new Particle.DustOptions(Color.BLUE , 1f);

                for (int i = 0; i < max ;i++) {
                    p.spawnParticle(Particle.REDSTONE ,pos.getX() + ( i*d),pos.getY(),pos.getZ() , 1 ,xAxis );
                    p.spawnParticle(Particle.REDSTONE ,pos.getX() ,pos.getY()+ (d*i),pos.getZ() , 1 ,yAxis );
                    p.spawnParticle(Particle.REDSTONE ,pos.getX() ,pos.getY(),pos.getZ() + (i*d), 1 ,zAxis );
                }
            }
            else {
                c.cancel();
                return;
            }
        } , 1,5);
    }

    private void toggleAxisShow() {
        showaxis = ! showaxis;
    }

    @Override
    public void onDeselect() {
        showaxis = false;
        canceled = true;
    }

    @Override
    public void enableEditor(ChatEditorMenu owner) {
        selected = false;
        commandEdit = owner.getTreeRoot().commands().register(new SimpleButtonGui(p-> {
            owner.getTreeRoot().select(this);
        }));
    }

    @Override
    public void disableEditor( ) {

    }


    protected boolean procesAxis(String axis ,double value) {
        axis = axis.toLowerCase();
        String a;
        boolean add;
        Vector v = getReference().getValue();
        if (axis.contains("add")) {
            add = true;
            a = axis.replace("add" , "");
        }
        else {
            add = false;
            a =axis;
        }
        Get g = map.get(a);
        if (g == null) {
            return false;
        }
        if (add) {
            g.set(v,g.get(v) + value);
        }
        else
            g.set(v,value);
        getReference().setValue(v);
        return true;
    }

    private String formatDecimalVector() {
        Vector v = getReference().getValue();
        if (v == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("[X:");
        sb.append(decimal.format(v.getX())).append(",Y:");
        sb.append(decimal.format(v.getY())).append(",Z:");
        sb.append(decimal.format(v.getZ())).append("]");
        return sb.toString();
    }


    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[vector] " + formatDecimalVector() + " ");
        TextComponent in = new TextComponent("[selectEditorCommand]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , commandEdit));
        tc.addExtra(in);
        p.spigot().sendMessage(tc);
    }


    static {
        map = new HashMap<>();
        map.put("x", new Get() {
            @Override
            public double get(Vector vector) {
                return vector.getX();
            }

            @Override
            public void set(Vector vector,double d) {
                vector.setX(d);
            }
        });
        map.put("y", new Get() {
            @Override
            public double get(Vector vector) {
                return vector.getY();
            }

            @Override
            public void set(Vector vector,double d) {
                vector.setY(d);
            }
        });
        map.put("z", new Get() {
            @Override
            public double get(Vector vector) {
                return vector.getZ();
            }

            @Override
            public void set(Vector vector,double d) {
                vector.setZ(d);
            }
        });
    }

    private interface Get {
        double get(Vector vector);
        void set(Vector vector,double d);
    }

}
