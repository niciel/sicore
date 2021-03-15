package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.helpers.GuiMultiCommand;
import com.niciel.superduperitems.utils.Ref;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public interface IBaseObjectEditor<T> {

    boolean select(IChatEditorMenu menu) ;

    GuiMultiCommand commands();

    void goBack();

    void sendMenu();

    Player getPlayer();

    public void setExitConsumer(BiConsumer<EditorResult , IBaseObjectEditor> endResult);

    public void disable(EditorResult res);

    Ref getReference() ;

}
