package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public abstract class ChatEditorMenu<T extends Object> extends ChatEditor<T> {


    private WeakReference<IBaseObjectEditor> owner;


    public ChatEditorMenu(IBaseObjectEditor owner , String name, String description) {
        super(name ,description );
        this.owner = new WeakReference<>(owner);
    }

    public IBaseObjectEditor getTreeRoot() {
        return owner.get();
    }


    protected String selectEditorCommand;

    @Override
    public void enableEditor(ChatEditorMenu owner) {
        WeakReference<ChatEditorMenu> _instance = new WeakReference<>(this);
        selectEditorCommand = owner.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                owner.getTreeRoot().select(_instance.get());
            }
        });
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc= new TextComponent("editor: " + getName()+ " ");
        tc.setColor(ChatColor.GRAY);
        TextComponent in = new TextComponent("[selectEditorCommand]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, selectEditorCommand));
        tc.addExtra(in);
        tc.addExtra(" " + getDescription());

        p.spigot().sendMessage(tc);
    }

    public abstract void sendMenu() ;
    public abstract void onSelect(ChatEditorMenu menu) ;
    public abstract void onDeselect();
}
