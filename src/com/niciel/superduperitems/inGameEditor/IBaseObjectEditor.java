package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.GuiMultiCommand;
import com.niciel.superduperitems.managers.IManager;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public interface IBaseObjectEditor<T> {

    boolean select(IChatEditorMenu menu) ;

    GuiMultiCommand commands();

    T getObject();

    void goBack();

    void sendMenu();

    Player getPlayer();

    public void setExitConsumer(BiConsumer<EditorResult , IBaseObjectEditor> endResult);

    public void disable();


}
