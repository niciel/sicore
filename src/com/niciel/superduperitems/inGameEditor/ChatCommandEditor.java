package com.niciel.superduperitems.inGameEditor;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.commandGui.GuiMultiCommand;
import com.niciel.superduperitems.commandGui.SimpleButtonGui;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.Ref;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Stack;
import java.util.function.BiConsumer;




public class ChatCommandEditor<T> extends IChatEditorMenu implements IBaseObjectEditor {

    private final GuiCommandManager guimanager;
    private final ChatEditorManager editorManager;
    private boolean disabled = false;

    private final Player p;
    private final T toEdit;
    private final Ref<T> refToEdit;
    private final CommandPointer playerPointer;
    /**
     * komendy wykorzystywane przez edytory, co zmiane edytora komendy sa czyszczone
     */
    private final GuiMultiCommand multicommand;
    private final CommandPointer editorCpointer;
    private final GuiMultiCommand editorCommands;
    private Stack<IChatEditorMenu> stack;
    private IChatEditorMenu mainMenu;


    private BiConsumer<EditorResult , IBaseObjectEditor> exitCode;


    public ChatCommandEditor(Player p, T toEdit) {
        super(null,null,null,null);
        this.p = p;
        this.toEdit = toEdit;
        this.refToEdit = new Ref<>(toEdit);
        this.stack = new Stack<>();
        this.editorManager = IManager.getManager(ChatEditorManager.class);
        this.guimanager = IManager.getManager(GuiCommandManager.class);
        this.multicommand = new GuiMultiCommand();
        this.editorCommands = new GuiMultiCommand();
        this.playerPointer = guimanager.registerCommandPointer(this.multicommand);
        this.editorCpointer = guimanager.registerCommandPointer(this.editorCommands);
        IChatEditor t = editorManager.getEditor(this ,toEdit.getClass());
        if (t instanceof IChatEditorMenu)
            mainMenu = (IChatEditorMenu) t;
        else {
            disabled = true;
        }
    }

    @Override
    public boolean select(IChatEditorMenu menu) {
        System.out.println("select");
        if (disabled)
            return false;
        if (stack.isEmpty() == false) {
            stack.peek().onDeselect();
        }
        multicommand.clear();
        stack.push(menu);
        menu.onSelect(refToEdit);
        sendMenu();
        System.out.println("kuniec");
        return true;
    }

    @Override
    public GuiMultiCommand commands() {
        return multicommand;
    }

    @Override
    public Object getObject() {
        return toEdit;
    }

    @Override
    public void goBack() {
        if (disabled)
            return;
        if (stack.isEmpty() == false) {
            IChatEditorMenu menu = stack.pop();
            menu.onDeselect();
            multicommand.clear();
            if (stack.isEmpty()) {
                mainMenu.onSelect(refToEdit);
            }
            else {
                stack.peek().onSelect(refToEdit);
            }
            sendMenu();
        }
    }

    private boolean firstUse = true;


    private TextComponent goBack;

    protected void firstUse() {
        firstUse = false;
        this.mainMenu.enableEditor(this ,refToEdit);
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
    }

    @Override
    public void sendMenu() {
        getPlayer().sendMessage(new String[]{"","","","","","","","","","","","",""});

        if (firstUse) {
            firstUse();
        }
        if (disabled)
            return;
        if (stack.isEmpty()) {
            mainMenu.sendItem(getPlayer());
        }
        else {
            stack.peek().sendItem(getPlayer());
            getPlayer().spigot().sendMessage(goBack);
        }
    }

    @Override
    public Player getPlayer() {
        return p;
    }


    @Override
    public void disable() {
        disabled = true;
        if (stack.isEmpty()) {
            if (mainMenu != null)
                mainMenu.onDeselect();
        }
        else {
            IChatEditorMenu m = stack.peek();
            m.onDeselect();

        }
    }



    @Override
    public void setExitConsumer(BiConsumer endResult) {
        if (exitCode != null)
            exitCode = endResult;
    }

        /*
HACK
    */
    @Override
    public IBaseObjectEditor getTreeRoot() {
        return this;
    }

    @Override
    public void onSelect(Ref ref) {}

    @Override
    public void onDeselect() {}

    @Override
    public void enableEditor(IChatEditorMenu owner, Ref ref) {}

    @Override
    public void disableEditor() {}

    @Override
    public void sendItem(Player p) {}


}
