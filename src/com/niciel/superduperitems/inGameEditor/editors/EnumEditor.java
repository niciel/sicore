package com.niciel.superduperitems.inGameEditor.editors;

import com.google.common.base.Enums;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
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


    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private HashMap<String , Object> map ;
    private WeakReference<ChatCommandEditor> owner ;
    private boolean ERROR = false;
    private CommandPointer editor;
    private Class enumType;
    private String name;
    private Ref<Object> reference;

    public EnumEditor(Class enumType) {
        this.enumType = enumType;
        if (! enumType.isEnum()) {
            ERROR = true;
        }
    }


    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<Object> refToObject) {
        this.owner = editor;
        this.reference = refToObject;
        this.map = new HashMap<>();
        if (ERROR)
            return;
        Object[] array = enumType.getEnumConstants();
        this.name = name;
        String n = null;
        for (Object o : array) {
            n = ((Enum) o).name();
            map.put(n.toLowerCase() , o);
        }

        WeakReference<EnumEditor> _instance = new WeakReference<>(this);
        this.editor = manager.registerGuiCommand( (pl,a) -> {
            String ina = a.toLowerCase();
            if (_instance.get().map.containsKey(ina)) {
                Object o = _instance.get().map.get(ina);
                _instance.get().reference.setValue(o);
                _instance.get().owner.get().send();
            }
            else {
                pl.playEffect(EntityEffect.HURT);
            }

        },this.getClass() , SDIPlugin.instance);

        manager.registerGuiHelper(this.editor , (pl,a) -> {
            List<String> out = SpigotUtils.findClosest(_instance.get().map.keySet() , a[0] , 10);
            return out;
        });
    }

    @Override
    public void sendItem(Player p) {
        if (ERROR) {
            p.sendMessage("blad inicjalizacji");
            return;
        }
        TextComponent tc  = new TextComponent("[" + SpigotUtils.fixStringLength("E:" + enumType.getSimpleName() , 4) + "] " + name + " ");
        TextComponent in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent( ClickEvent.Action.SUGGEST_COMMAND , editor.getCommand() + " "));
        tc.addExtra(in);
        tc.addExtra(" " + reference.getValue());
        p.spigot().sendMessage(tc);
    }
}
