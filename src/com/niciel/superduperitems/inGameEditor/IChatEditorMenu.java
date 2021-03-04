package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.commandGui.MultiGuiCommand;
import com.niciel.superduperitems.inGameEditor.editors.EditorChatObject;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.UUID;

public abstract class IChatEditorMenu<T extends Object> extends  IChatEditor<T> {


    public final UUID uuid;
    private int deep;
    private WeakReference<EditorChatObject> owner;



    public IChatEditorMenu(EditorChatObject owner , int deep ,String name, String description, Class baseType ) {
        super(name ,description ,baseType);
        this.uuid = UUID.randomUUID();
        this.deep = deep;
        this.owner = new WeakReference<>(owner);
    }

    public  int getDeep() {
        return deep;
    }

    public EditorChatObject getTreeRoot() {
        return owner.get();
    }

    public abstract void onSelect() ;
    public abstract void onDeselect();
}
