package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommand;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class EditorChatFloat extends IChatEditor<Float> {



    private String pointer;
    private Ref<Float> ref;


    public EditorChatFloat(String name, String description,  Class clazz , Field field) {
        super(name, description, clazz,field);
    }


    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<Float> ref) {
        pointer = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String s) {
                float i = 0;
                try {
                    i = Float.parseFloat(s);
                }
                catch (NumberFormatException e) {
                    p.sendMessage("niepoprawna wartosc liczbowa: " + s);
                    owner.getTreeRoot().sendMenu();
                    return;
                }

                if (ref.getValue() != i) {
                    ref.setValue(i);
                }
            }
        });
    }

    @Override
    public void disableEditor(IChatEditorMenu owner) {

    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[Intege] " + getName() + " ");
        TextComponent in = new TextComponent("[edytuj]");
        tc.setColor(ChatColor.WHITE);

        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer + " "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        tc.addExtra(" wartosc: " + ref.getValue());
        p.spigot().sendMessage(tc);
    }
}
