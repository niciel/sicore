package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.inGameEditor.ChatEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorMenu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class EditorChatDouble extends ChatEditor<Double> {



    private String command;


    public EditorChatDouble(String name, String description) {
        super(name, description);
    }

    @Override
    public void enableEditor(ChatEditorMenu owner) {
        command = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String s) {
                double i = 0;
                try {
                    i = Double.parseDouble(s);
                }
                catch (NumberFormatException e) {
                    p.sendMessage("niepoprawna wartosc liczbowa: " + s);
                    owner.getTreeRoot().sendMenu();
                    return;
                }

                if (getReference().getValue() != i) {
                    getReference().setValue(i);
                }
            }
        });
    }

    @Override
    public void disableEditor(){}

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[double] " + getName() + " ");
        TextComponent in = new TextComponent("[edytuj]");
        tc.setColor(ChatColor.GRAY);

        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,command +" "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        tc.addExtra(" wartosc: " + getReference().getValue());
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
