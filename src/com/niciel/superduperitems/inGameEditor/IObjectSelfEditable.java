package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.editors.object.EditorChatObject;
import org.bukkit.entity.Player;

public interface IObjectSelfEditable {


    void onEnableEditor(EditorChatObject editor);
    void onDisableEditor(EditorChatObject editor);

    void onSendItemMenu(Player p);
}
