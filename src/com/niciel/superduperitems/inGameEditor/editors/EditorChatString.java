package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommand;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.EditorExtraData;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import sun.util.resources.cldr.en.CalendarData_en_Dsrt_US;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class EditorChatString extends IChatEditor<String> {



    private static GuiCommandManager command = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private String pointer;
    private Ref<String> ref;

    /**
     * @param name
     * @param description
     * @param clazz       class of object or if field ist null field type
     */
    public EditorChatString(String name, String description, Class clazz) {
        super(name, description, clazz);
    }


    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<String> ref) {
        this.ref = ref;
        pointer = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                ref.setValue(ChatColor.translateAlternateColorCodes('&' , left));
            }
        });
    }

    @Override
    public void disableEditor( ) {
        ref = null;
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[String] " + getName() + " ");
        TextComponent in = new TextComponent("[ustaw]");
        tc.setColor(ChatColor.WHITE);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer + " "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        if (ref.getValue() != null && ! ref.getValue().isEmpty()) {
            in = new TextComponent("[edytuj]");
            tc.setColor(ChatColor.WHITE);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer + " " + ref.getValue()));
            in.setColor(ChatColor.GREEN);
            tc.addExtra(in);
        }
        tc.addExtra(" wartosc: " + ref.getValue());
        p.spigot().sendMessage(tc);
    }
}
