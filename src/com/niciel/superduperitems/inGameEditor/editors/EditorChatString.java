package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.inGameEditor.ChatEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorMenu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class EditorChatString extends ChatEditor<String> {




    private String pointer;

    /**
     * @param name
     * @param description
     * @param clazz       class of object or if field ist null field type
     */
    public EditorChatString(String name, String description) {
        super(name, description);
    }


    @Override
    public void enableEditor(ChatEditorMenu owner) {
        pointer = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                getReference().setValue(ChatColor.translateAlternateColorCodes('&' , left));
            }
        });
    }

    @Override
    public void disableEditor( ) {

    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[String] " + getName() + " ");
        TextComponent in = new TextComponent("[ustaw]");
        tc.setColor(ChatColor.WHITE);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer + " "));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        if (getReference().getValue() != null && ! getReference().getValue().isEmpty()) {
            in = new TextComponent("[edytuj]");
            tc.setColor(ChatColor.WHITE);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND ,pointer + " " + getReference().getValue()));
            in.setColor(ChatColor.GREEN);
            tc.addExtra(in);
        }
        tc.addExtra(" wartosc: " + getReference().getValue());
        p.spigot().sendMessage(tc);
    }


    public String getCommandPointer() {
        return this.pointer;
    }
}
