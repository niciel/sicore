package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommand;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.EditorExtraData;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Dual;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EditorChatMethod<T> extends IChatEditor<Object> {






    protected MethodHandle methodHandle;
    protected WeakReference<T> reference;
    public BiConsumer<Player , T> onSend;


    public T data;

    public EditorChatMethod(Object owner , MethodHandle handle) {
        this.reference = new WeakReference(owner);
        this.methodHandle = handle;
    }

    public void invoke() {
        try {
            methodHandle.invoke(reference.get() , this);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<Object> refToObject) {
        invoke();
    }

    @Override
    public void sendItem(Player p) {
        if (onSend != null)
            onSend.accept(p , data);
    }
}
