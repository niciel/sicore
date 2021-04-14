package com.niciel.superduperitems.inGameEditor;
import com.niciel.superduperitems.commandGui.*;
import com.niciel.superduperitems.commandGui.helpers.GuiDoubleConfirmButton;
import com.niciel.superduperitems.commandGui.helpers.GuiMultiCommand;
import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class ChatCommandEditor<T> implements IBaseObjectEditor {

    private final GuiCommandManager guimanager;
    private final ChatEditorManager editorManager;
    private boolean disabled = false;

    private final Player p;
    private final Ref<T> refToEdit;
    private Stack<ChatEditorMenu> stack;

    private BiConsumer<EditorResult , IBaseObjectEditor> exitCode;
    public Consumer<IBaseObjectEditor> sendOnMainPage;

    /**
     * komendy wykorzystywane przez edytory, co zmiane edytora komendy sa czyszczone
     */
    private final GuiMultiCommand multicommand;
    private final GuiMultiCommand editorCommands;
    private CommandPointer playerPointer;
    private CommandPointer editorCpointer;

    public ChatCommandEditor(Player p, T toEdit) {
        this.p = p;
        this.refToEdit = new Ref<>(toEdit);
        this.stack = new Stack<>();
        this.editorManager = IManager.getManager(ChatEditorManager.class);
        this.guimanager = IManager.getManager(GuiCommandManager.class);
        this.multicommand = new GuiMultiCommand();
        this.editorCommands = new GuiMultiCommand();
        this.playerPointer = guimanager.registerCommandPointer(this.multicommand);
        this.editorCpointer = guimanager.registerCommandPointer(this.editorCommands);
        ChatEditor t = editorManager.getEditor(this ,toEdit.getClass() , "baza nazwa" , " baza opis");
        if ((t instanceof ChatEditorMenu) == false) {
            disabled = true;
            return;
        }
        stack.push((ChatEditorMenu) t);
        t.initialize(refToEdit);
        ((ChatEditorMenu) t).onSelect(null);
    }

    @Override
    public boolean select(ChatEditorMenu menu) {
        if (disabled)
            return false;
        ChatEditorMenu last = stack.peek();
        last.onDeselect();
        multicommand.clear();
        stack.push(menu);
        menu.onSelect(last);
        sendMenu();
        return true;
    }

    @Override
    public GuiMultiCommand commands() {
        return multicommand;
    }


    @Override
    public void goBack() {
        if (disabled)
            return;
        if (stack.size() > 1) {
            stack.pop().onDeselect();
            stack.peek().onSelect(stack.get(stack.size() - 1));
            sendMenu();
        }
    }

    private boolean firstUse = true;


    @Override
    public Ref getReference() {
        return this.refToEdit;
    }

    private TextComponent goBack;
    private TextComponent quitWithoutSave;

    protected void firstUse() {
        firstUse = false;
        TextComponent in = new TextComponent("[<back<]");
        in.setColor(ChatColor.GREEN);
        WeakReference<ChatCommandEditor> _instance = new WeakReference<>(this);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.editorCommands.register(new SimpleButtonGui(p-> {
            _instance.get().goBack();
        }))));
        goBack = new TextComponent("****");
        goBack.addExtra(in);
        goBack.addExtra("****");
        goBack.setColor(ChatColor.GRAY);
        TextComponent tc = new TextComponent();
        in = new TextComponent("[wyjdz bez zapisu]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.editorCommands.register(new GuiDoubleConfirmButton(
                accept-> {_instance.get().disable(EditorResult.DISCARD_CHANGES);},"wyjdz bez zapisywania", reject-> {_instance.get().sendMenu();} , "nie czekaj jeszcze nie:L"
        ))));
        in.setColor(ChatColor.RED);
        tc.addExtra(in);
        in = new TextComponent("[wyjdz ale zapisz]");
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.editorCommands.register(new GuiDoubleConfirmButton(
                accept-> {_instance.get().disable(EditorResult.APPLAY_CHANGES);},"wyjdz i zapisz", reject-> {_instance.get().sendMenu();} , "chce wrocic do edytora!!"
        ))));
        in.setColor(ChatColor.GREEN);
        tc.addExtra(in);
        quitWithoutSave = tc;
    }

    @Override
    public void sendMenu() {
        getPlayer().sendMessage(new String[]{"","","","","","","","","","","","",""});
        if (firstUse) {
            firstUse();
        }
        if (disabled)
            return;
        stack.peek().sendMenu();
        if (stack.size() > 1)
            getPlayer().spigot().sendMessage(goBack);
        else {
            if (sendOnMainPage == null)
                getPlayer().spigot().sendMessage(quitWithoutSave);
            else
                sendOnMainPage.accept(this);
        }
    }

    @Override
    public Player getPlayer() {
        return p;
    }


    public void disableSilently(EditorResult result,boolean removePlayerFromEdit) {
        if (disabled)
            return;
        disabled = true;
        stack.peek().onDeselect();
        guimanager.remove(this.playerPointer);
        guimanager.remove(this.editorCpointer);
        this.playerPointer = null;
        this.editorCpointer = null;
        if (removePlayerFromEdit)
            editorManager.removePlayerFromEditorMap(getPlayer());
        if (this.exitCode != null){
            this.exitCode.accept(result , this);
        }
    }

    @Override
    public void disable(EditorResult result) {
        disableSilently(result,true);
    }


    @Override
    public void setExitConsumer(BiConsumer c) {
        if (exitCode == null)
            exitCode = c;
    }



}
