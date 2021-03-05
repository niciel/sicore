package com.niciel.superduperitems.inGameEditor;

import org.bukkit.entity.Player;

public interface IBaseObjectEditor {

    boolean select(IChatEditorMenu menu) ;

    void goBack();

    void sendMenu();

    Player player();

}
