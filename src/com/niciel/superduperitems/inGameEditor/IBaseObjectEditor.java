package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.helpers.GuiMultiCommand;
import com.niciel.superduperitems.utils.Ref;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public interface IBaseObjectEditor<T> {

    boolean select(ChatEditorMenu menu) ;

    GuiMultiCommand commands();

    void goBack();

    void sendMenu();

    Player getPlayer();

    public void disable(EditorResult res);

    Ref getReference() ;

    void setExitConsumer(BiConsumer<EditorResult,IBaseObjectEditor> c);

}
