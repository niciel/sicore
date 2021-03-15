package com.niciel.superduperitems.inGameEditor.editors.object;

import org.bukkit.entity.Player;

public interface IObjectSelfEditable {


    void onEnableEditor(EditorChatObject editor);
    void onDisableEditor(EditorChatObject editor);

    void onSendItemMenu(Player p);
}
