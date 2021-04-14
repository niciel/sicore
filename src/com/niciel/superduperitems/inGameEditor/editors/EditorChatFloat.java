package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.inGameEditor.ChatEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorMenu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class EditorChatFloat extends ChatEditor<Float> {



    private String pointer;


    public EditorChatFloat(String name, String description ) {
        super(name, description);
    }


    @Override
    public void enableEditor(ChatEditorMenu owner) {
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

                if (getReference().getValue() != i) {
                    getReference().setValue(i);
                }
            }
        });
    }

    @Override
    public void disableEditor( ) {

    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[Intege] " + getName() + " ");
        TextComponent in = new TextComponent("[edytuj]");
        tc.setColor(ChatColor.WHITE);

        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer + " "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        tc.addExtra(" wartosc: " + getReference().getValue());
        p.spigot().sendMessage(tc);
    }
}
