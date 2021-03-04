package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.IManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatEditorManager implements IManager , Listener {

    private HashMap<UUID , EditorData> editors = new HashMap<>();

    public final HashMap<String , Class> nameToEditableClasses = new HashMap<>();

    public <T> ChatCommandEditor<T> createChatCommandEditor(Player player , T toEdit) {
        return new ChatCommandEditor<>(player , toEdit);
    }

    public <T> ChatCommandEditor<T> enableChatCommandEditor(Player player , T toEdit) {
        ChatCommandEditor e = getEditor(player);
        if( e != null) {
            return e;
        }
        e = new ChatCommandEditor(player , toEdit);
        enable(e , false);
        return e;
    }

    public void enable(ChatCommandEditor e , boolean allowmultiple) {
        if (getEditor(e.getPlayer()) != null) {
            return;
        }
        UUID uuid = e.getPlayer().getUniqueId();
        e.generate();
        editors.put(uuid , new EditorData(e , uuid ,allowmultiple ));
        e.send();
    }

    public void removeEditor(Player p) {
        ChatCommandEditor e = getEditor(p);
        if (e== null)
            return;
        e.onRemove();
        editors.remove(p.getUniqueId());
    }

    public ChatCommandEditor getEditor(Player p) {
        EditorData data = editors.get(p.getUniqueId());
        if (data != null)
            return data.editor;
        return null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeEditor(e.getPlayer());
    }

    public class EditorData {

        public EditorData(ChatCommandEditor editor, UUID owner, boolean allowMultipleEditors) {
            this.editor = editor;
            this.owner = owner;
            this.allowMultipleEditors = allowMultipleEditors;
            this.extraEditors = new ArrayList<>();
        }

        public ChatCommandEditor editor;
        public UUID owner;
        public boolean allowMultipleEditors;
        public List<ChatCommandEditor> extraEditors;

    }

}
