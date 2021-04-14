package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.utils.Ref;
import org.bukkit.entity.Player;

public abstract class ChatEditor<T extends Object> {


    private String name;
    private String description;
    private Ref<T> reference;

    /**
     *
     * @param name
     * @param description
     */
    public ChatEditor(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void enableEditor(ChatEditorMenu owner);
    public abstract void disableEditor();


    public void initialize(Ref<T> reference) {
        if (this.reference == null)
            this.reference = reference;
    }

    public Ref<T> getReference() {
        return this.reference;
    }

    public abstract void sendItem(Player p);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
