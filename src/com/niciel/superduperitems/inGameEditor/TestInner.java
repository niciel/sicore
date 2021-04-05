package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;
import com.niciel.superduperitems.inGameEditor.editors.object.EditorChatObject;
import org.bukkit.entity.Player;


@ChatObjectName(name = "nazwa")
public class TestInner implements IObjectSelfEditable {


    @ChatEditable
    private String innerStringTest = " test ";



    @Override
    public void onEnableEditor(EditorChatObject editor) {
        editor.getTreeRoot().getPlayer().sendMessage("enabled");
    }

    @Override
    public void onDisableEditor(EditorChatObject editor) {
        editor.getTreeRoot().getPlayer().sendMessage("disabled");
    }

    @Override
    public void onSendItemMenu(Player p) {
        p.sendMessage("seditemmenu");
    }
}
