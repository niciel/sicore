package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.commandGui.*;
import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.inGameEditor.ChatEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorMenu;
import com.niciel.superduperitems.utils.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class EnumEditor extends ChatEditor<Object> {

    private HashMap<String , Object> map ;
    private boolean ERROR = false;
    private Class enumType;
    private String command;

    public EnumEditor(Class clazz, String name, String description) {
        super(name, description);
        this.enumType = clazz;
        if (! enumType.isEnum()) {
            ERROR = true;
        }
    }

    @Override
    public void enableEditor(ChatEditorMenu owner) {
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
        int i = 10;
        if (map.size() < 10)
            i = map.size();
        int j = i;
        owner.getTreeRoot().commands().register(command, new IGuiTabCompliter() {
            @Override
            public List<String> onTabComplite(Player sender, String[] args, int deep) {
                List<String> out = SpigotUtils.findClosest(map.keySet() , args[deep] , j);
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