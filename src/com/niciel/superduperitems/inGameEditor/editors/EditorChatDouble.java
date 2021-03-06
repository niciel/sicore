package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
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

public class EditorChatDouble extends IChatEditor<Double> {


    private Ref<Double> ref;
    private String name;

    private String command;

    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<Double> ref) {

    }

    @Override
    public void disableEditor(IChatEditorMenu owner) {

    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[double] " + name + " ");
        TextComponent in = new TextComponent("[edytuj]");
        tc.setColor(ChatColor.GRAY);

        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer.getCommand()+" "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        tc.addExtra(" wartosc: " + ref.getValue());
        p.spigot().sendMessage(tc);
    }


    /*
        private static GuiCommandManager command = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private CommandPointer pointer;

    private Ref<Double> ref;
    private String name;



    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<Double> refToObject) {
        this.ref = refToObject;
        this.name = name;

        WeakReference<Ref<Double>>  _ref = new WeakReference<>(ref);

        if (ref.getValue() == null) {
            ref.setValue(0d);
        }


        pointer = command.registerGuiCommand( (p,s) ->
                {
                    double i = 0;
                    try {
                        i = Double.parseDouble(s);
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
     */
}
