package com.niciel.superduperitems.inGameEditor.editors;

import com.google.common.base.Enums;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.*;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.EditorExtraData;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.List;

public class EnumEditor extends IChatEditor<Object> {



    private HashMap<String , Object> map ;
    private boolean ERROR = false;
    private String editor;
    private Class enumType;

    private Ref<Object> reference;

    /**
     * @param name
     * @param description
     * @param clazz       class of object or if field ist null field type
     */
    public EnumEditor(String name, String description, Class clazz) {
        super(name, description, clazz);
        this.enumType = clazz;
        if (! enumType.isEnum()) {
            ERROR = true;
        }


    }



    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<Object> ref) {
        if (ERROR)
            return;
        this.reference = ref;
        this.map = new HashMap<>();
        Object[] array = enumType.getEnumConstants();
        String n = null;
        for (Object o : array) {
            n = ((Enum) o).name();
            map.put(n.toLowerCase() , o);
        }
        Ref<String> id = new Ref<String>();

        String command = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                String ina = left.toLowerCase();
                if (map.containsKey(ina)) {
                    Object o = map.get(ina);
                    reference.setValue(o);
                }
                else {
                    p.playEffect(EntityEffect.HURT);
                }
            }

        });

        owner.getTreeRoot().commands().register((String) ref.getValue() , new IGuiTabCompliter() {
            @Override
            public List<String> onTabComplite(Player sender, String[] args, int deep) {
                List<String> out = SpigotUtils.findClosest(map.keySet() , args[deep] , 10);
                return out;
            }
        });
    }

    @Override
    public void disableEditor( ) {

    }

    @Override
    public void sendItem(Player p) {
        if (ERROR) {
            p.sendMessage("blad inicjalizacji");
            return;
        }
        TextComponent tc  = new TextComponent("[" + SpigotUtils.fixStringLength("E:" + enumType.getSimpleName() , 4) + "] " + getName() + " ");
        TextComponent in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND , editor + " "));
        tc.addExtra(in);
        tc.addExtra(" " + reference.getValue());
        p.spigot().sendMessage(tc);
    }
}
