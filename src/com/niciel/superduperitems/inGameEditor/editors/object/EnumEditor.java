package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.commandGui.*;
import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class EnumEditor extends IChatEditor<Object> {

    private HashMap<String , Object> map ;
    private boolean ERROR = false;
    private Class enumType;
    private String command;

    public EnumEditor(String name, String description, Class clazz) {
        super(name, description, clazz);
        this.enumType = clazz;
        if (! enumType.isEnum()) {
            ERROR = true;
        }
    }

    @Override
    public void enableEditor(IChatEditorMenu owner) {
        this.map = new HashMap<>();
        Object[] array = enumType.getEnumConstants();
        String n = null;
        for (Object o : array) {
            n = ((Enum) o).name();
            map.put(n.toLowerCase() , o);
        }
        command = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                String ina = left.toLowerCase();
                if (map.containsKey(ina)) {
                    Object o = map.get(ina);
                    getReference().setValue(o);
                }
                else {
                    p.playEffect(EntityEffect.HURT);
                }
            }

        } );

        owner.getTreeRoot().commands().register(command, new IGuiTabCompliter() {
            @Override
            public List<String> onTabComplite(Player sender, String[] args, int deep) {
                List<String> out = SpigotUtils.findClosest(map.keySet() , args[deep] , 10);
                return out;
            }
        });
    }

    @Override
    public void disableEditor( ) {  }

    @Override
    public void sendItem(Player p) {
        TextComponent tc  = new TextComponent("[" + SpigotUtils.fixStringLength("E:" + enumType.getSimpleName() , 4) + "] " + getName() + " ");
        TextComponent in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND , command + " "));
        tc.addExtra(in);
        tc.addExtra(" " + getReference().getValue());
        p.spigot().sendMessage(tc);
    }
}