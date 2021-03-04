package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class EditorChatInt extends IChatEditor<Integer> {

    private static GuiCommandManager command = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private CommandPointer pointer;
    private Ref<Integer> ref;

    private String name;





    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<Integer> refToObject) {
        ref = refToObject;
        this.name = name;
        WeakReference<Ref<Integer>>  _ref = new WeakReference<>(ref);

        if (ref.getValue() == null) {
            ref.setValue(0);
        }

        pointer = command.registerGuiCommand( (p,s) ->
                {
                    int i = 0;
                    try {
                        i = Integer.parseInt(s);
                    }
                    catch (NumberFormatException e) {
                        p.sendMessage("niepoprawna wartosc liczbowa: " + s);
                        return;
                    }

                    if (_ref.get().getValue() != i) {
                        _ref.get().setValue(i);
                        editor.get().send();
                    }
                }
                , this.getClass() , SDIPlugin.instance);
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[Intege] " + name + " ");
        TextComponent in = new TextComponent("[edytuj]");
        tc.setColor(ChatColor.WHITE);

        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer.getCommand()+" "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        tc.addExtra(" wartosc: " + ref.getValue());
        p.spigot().sendMessage(tc);
    }
}
