package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
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

    private CommandPointer pointer;
    private WeakReference<ChatCommandEditor> editor;
    private Ref<String> ref;
    private String name;

    public EditorChatString() {}




    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<String> refToObject) {
        this.ref = refToObject;
        this.editor = editor;
        this.name = name;



        if (refToObject.getValue() == null) {
            refToObject.setValue("");
        }


        WeakReference<EditorChatString> _instance = new WeakReference<>(this);
        WeakReference<Ref<String>> _ref = new WeakReference<>(refToObject);
        pointer = command.registerGuiCommand( (p,s) ->
                {
                    String out = ChatColor.translateAlternateColorCodes('&' , s);
                    _ref.get().setValue(out);
                    _instance.get().editor.get().send();
                }
                , this.getClass() , SDIPlugin.instance);
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[String] " + name + " ");
        TextComponent in = new TextComponent("[ustaw]");
        tc.setColor(ChatColor.WHITE);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer.getCommand() + " "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        if (ref.getValue() != null && ! ref.getValue().isEmpty()) {
            in = new TextComponent("[edytuj]");
            tc.setColor(ChatColor.WHITE);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer.getCommand() + " " + ref.getValue()));
            in.setColor(ChatColor.GREEN);
            tc.addExtra(in);
        }
        tc.addExtra(" wartosc: " + ref.getValue());
        p.spigot().sendMessage(tc);
    }
}
