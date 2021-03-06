package com.niciel.superduperitems.inGameEditor;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.*;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;


/**
 * SPRAWDZIC NAPARAWIC I ZAMIENIC Z CHATCOMMANDEDITOR !!!TODO
 */

public class ChatCommandEditor<T>   {


    private  GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);



    public Class type;
    public T mainObject;
    public String editorPrefix;



    private List<EditorData> list ;

    private GuiMultiCommand commands;
    private CommandPointer pointerToCommands;

    private HashMap<String , String> addedCommands;
    private WeakReference<Player> player;

    public boolean editMode;
    private List<EditorData> data;

    private HashMap<String , HashSet<String>> excluded;
    private String backToEditorCommand;
    private List<IChatEditorMenu> selectedList;
    private IChatEditor selectedEditor;


    public ChatCommandEditor(Player player , T value) {
        this.mainObject = value;
        this.player = new WeakReference<>(player);

        this.commands = new GuiMultiCommand();
        this.pointerToCommands = manager.registerGuiCommand(this.commands , this.getClass() , SDIPlugin.instance);
        this.selectedList = new ArrayList<>();
        this.addedCommands = new HashMap<>();
    }

    public boolean select(IChatEditorMenu ed) {
        if (getSelectedMenu() == null) {
            if (this.selectedEditor instanceof IChatEditorMenu)
                ((IChatEditorMenu) this.selectedEditor).onDeselect(this);
            selectedList.add(ed);
            send();
            return true;
        }
        else {
            if (getSelectedMenu().uuid.equals(ed.uuid)) {
                send();
                return true;
            }
            else {
                getSelectedMenu().onDeselect(this);
                selectedList.add(ed);
                getSelectedMenu().onSelect(this);
                send();
                return true;
            }
        }
    }


    public IChatEditorMenu getSelectedMenu() {
        if (! selectedList.isEmpty()) {
            return selectedList.get(selectedList.size()-1);
        }
        return null;
    }

    protected void onRemove() {
        if (getSelectedMenu() == null) {
            if (selectedEditor != null && selectedEditor instanceof IChatEditorMenu)
                ((IChatEditorMenu) selectedEditor).onDeselect(this);
        }
        else {
            if (getSelectedMenu() instanceof IChatEditorMenu)
                ((IChatEditorMenu) selectedEditor).onDeselect(this);
        }
    }

    public void send() {
        if (!selectedList.isEmpty()) {
            getPlayer().sendMessage("whatthefuck");
            getSelectedMenu().sendMenu(getPlayer());
            sendGoBack();
        }
        else {
            if (selectedEditor instanceof IChatEditorMenu)
                ((IChatEditorMenu) this.selectedEditor).sendMenu(getPlayer());
            else {
                selectedEditor.sendItem(getPlayer());

            }
//            TODO
            TextComponent tc;
            for (Map.Entry<String , String> p : this.addedCommands.entrySet()) {
                tc = new TextComponent("["+p.getValue()+"]");
                tc.setColor(ChatColor.GOLD);
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.pointerToCommands.getCommand() + " " + p.getKey()));
                getPlayer().spigot().sendMessage(tc);
            }
        }
    }

    public void sendGoBack() {
        TextComponent tc = new TextComponent("[<<<<<]");
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.backToEditorCommand));
        tc.setColor(ChatColor.GREEN);
        getPlayer().spigot().sendMessage(tc);
    }


    public Player getPlayer() {
        return player.get();
    }



    public boolean isEditMode() {
        return editMode;
    }

    public void goBack() {
        if (selectedList.isEmpty())
            return;
        getSelectedMenu().onDeselect(this);
        selectedList.remove(selectedList.size()-1);
        if (selectedList.isEmpty()) {
            if (this.selectedEditor instanceof IChatEditorMenu)
                ((IChatEditorMenu) this.selectedEditor).onSelect(this);
        }
        send();
    }


    protected void generate() {
        WeakReference<ChatCommandEditor> _instance = new WeakReference<>(this);
        String command = this.commands.register(new SimpleButtonGui( c -> {
            _instance.get().goBack();
        }));
        this.backToEditorCommand = this.pointerToCommands.getCommand() + " " + command;


        data = new ArrayList<>();
        if (this.type == null)
            this.type = this.mainObject.getClass();
        selectedEditor = ChatCommandEditor.getEditor(this.type);
        RefCallBack<T> ref = new RefCallBack<>(this.mainObject);
        ref.addCallBack( c-> {
            _instance.get().mainObject = c.getValue();
        });
        this.selectedEditor.enable(new WeakReference<>(this) , "" , "" ,this.type, ref );
        if (this.selectedEditor != null) {
            if (this.selectedEditor instanceof IChatEditorMenu) {
                ((IChatEditorMenu) this.selectedEditor).onSelect(this);
            }
        }
    }



    protected void generateForClass(Class clazz) {
        ChatEditable editable;
        IChatEditor editor;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        String description ;
        WeakReference<ChatCommandEditor> _instance = new WeakReference<>(this);
        String name;
        for (Field f : clazz.getDeclaredFields()) {
            editable = f.getAnnotation(ChatEditable.class);
            if (editable == null)
                continue;
            if (editMode || editable.excludeInEdit())
                continue;

            if (f.isAccessible() == false)
                f.setAccessible(true);

            if (editable.name() == null || editable.name().isEmpty())
                name = f.getType().getSimpleName();
            else
                name = editable.name();

            if (editable.description() == null || editable.description().isEmpty())
                description = "description";
            else
                description = editable.description();

            editor = getEditor(f.getType());
            if (editor == null) {
//                :D
//                TODO to nie powinno miec miejsca :D
                SDIPlugin.instance.logWarning(this , "nie odnaleziono edytora do pola " + f.getType().getName());
                continue;
            }
            EditorData d = new EditorData(mainObject);
            d.editor = editor;

            try {
                d.setter = lookup.unreflectSetter(f);
                d.getter = lookup.unreflectGetter(f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            RefCallBack call = new RefCallBack();
            call.addCallBack(c -> {
                d.set(((Ref) c).getValue());
            });
            d.get();

            data.add(d);
            editor.enable(_instance ,name , description ,f.getType(), call);
//            TODO
        }
    }

    public void addCommand(String command , GuiCommandArgs args) {
        String id = this.commands.register(args);
        this.addedCommands.put(id , command);
    }


    public void onDisable() {
        pointerToCommands.dispose();
//        TODO

    }

}
