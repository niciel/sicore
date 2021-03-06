package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.MultiGuiCommand;
import org.bukkit.entity.Player;

public interface IBaseObjectEditor {

    boolean select(IChatEditorMenu menu) ;

    MultiGuiCommand commands();

    void goBack();

    void sendMenu();

    Player player();

}
