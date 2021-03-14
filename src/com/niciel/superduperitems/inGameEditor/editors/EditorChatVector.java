package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.IBaseObjectEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;


public class EditorChatVector extends IChatEditorMenu<Vector> {
    public EditorChatVector(IBaseObjectEditor owner, String name, String description, Class clazz) {
        super(owner, name, description, clazz);
    }

    @Override
    public void onSelect(Ref<Vector> ref) {

    }

    @Override
    public void onDeselect() {

    }

    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<Vector> ref) {

    }

    @Override
    public void disableEditor( ) {

    }

    @Override
    public void sendItem(Player p) {

    }



    /*
    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private Vector vector;
    private CommandPointer command;

    private HashMap<String , Get> map;



    public boolean procesAxis(String axis , double value) {
        axis = axis.toLowerCase();
        String a;
        boolean add;
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
            g.set(g.get() + value);
        }
        else
            g.set(value);
        return true;
    }

    public void coopy(Vector v) {
        this.vector.setX(v.getX());
        this.vector.setY(v.getY());
        this.vector.setZ(v.getZ());
    }



    @Override
    public void sendMenu(Player p) {
        TextComponent tc = new TextComponent();
        TextComponent in = new TextComponent("[player]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , command.getCommand() + " here"));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("[eye]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , command.getCommand() + " eye"));
        tc.addExtra(in);
        tc.addExtra(", ");


        in = new TextComponent("X");
        in.setColor(ChatColor.BLUE);
        in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(vector.getX() +"").create()));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[s]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command.getCommand() + " x "));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[a]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command.getCommand() + " addx "));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("Y");
        in.setColor(ChatColor.BLUE);
        in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(vector.getY() +"").create()));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[s]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command.getCommand() + " y "));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[a]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command.getCommand() + " addy "));
        tc.addExtra(in);
        tc.addExtra(", ");

        in = new TextComponent("Z");
        in.setColor(ChatColor.BLUE);
        in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(vector.getZ() +"").create()));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[s]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command.getCommand() + " z "));
        tc.addExtra(in);
        tc.addExtra("|");
        in = new TextComponent("[a]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , command.getCommand() + " addz "));
        tc.addExtra(in);

        p.spigot().sendMessage(tc);
    }

    @Override
    public void onSelect(ChatCommandEditor editor) {

    }

    @Override
    public void onDeselect(ChatCommandEditor editor) {

    }

    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<Vector> refToObject) {
        if (refToObject.getValue() == null)
            refToObject.setValue(new Vector(0d,0d,0d));
        this.vector = refToObject.getValue();
        WeakReference<EditorChatVector> _instance = new WeakReference<>(this);
        command = manager.registerGuiCommand((pl, args) -> {
            if (args.length >= 2) {
                args = Arrays.copyOfRange(args , 1,3);
                if (args[0].contentEquals("here")) {
                    Vector v = pl.getLocation().toVector();
                    _instance.get().coopy(v);
                }
                else if (args[0].contentEquals("eye")) {
                    Vector v = pl.getEyeLocation().toVector();
                    _instance.get().coopy(v);
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
        } , this.getClass() , SDIPlugin.instance);
        map = new HashMap<>();
        map.put("x", new Get() {
            @Override
            public double get() {
                return vector.getX();
            }

            @Override
            public void set(double d) {
                vector.setX(d);
            }
        });
        map.put("y", new Get() {
            @Override
            public double get() {
                return vector.getY();
            }

            @Override
            public void set(double d) {
                vector.setY(d);
            }
        });
        map.put("z", new Get() {
            @Override
            public double get() {
                return vector.getZ();
            }

            @Override
            public void set(double d) {
                vector.setZ(d);
            }
        });
    }

    @Override
    public void sendItem(Player p) {

    }


    private interface Get {
        public double get();
        public void set(double d);
    }



     */
}
