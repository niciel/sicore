package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.GuiMultiCommand;
import org.bukkit.entity.Player;

public interface IBaseObjectEditor {

    boolean select(IChatEditorMenu menu) ;

    GuiMultiCommand commands();

    void goBack();

    void sendMenu();

    Player player();

}
