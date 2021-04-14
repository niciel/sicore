package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
import com.niciel.superduperitems.inGameEditor.*;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditorChatList extends ChatEditorMenu<List> implements IFieldEditor  {

    private Class genericType;
    private String genericName;
    private boolean selected;
    private ChatEditorManager manager = IManager.getManager(ChatEditorManager.class);
    private List<Integer> constantIDs;

    private Collection<NewInstanceData> classes;

    private String selectToEditCommand;
    private String removeCommand;
    private String editListElement;
    private String commandBackToList;
    private String createNewObject;

    private ChatCommandEditor editor;

    private boolean generated = false;
    private int IDS = 0;


    public EditorChatList(IBaseObjectEditor owner, String name, String description) {
        super(owner, name, description);
    }

    @Override
    public void sendMenu() {

    }

    public EditorChatList(IBaseObjectEditor owner, String name, String description, Class parametrizedType) {
        super(owner, name, description);
        initWithParametrizedType(parametrizedType);
    }


    private void fixIDs() {
        this.constantIDs = new ArrayList<>();
        if (getReference().getValue() == null) {
            getReference().setValue(new ArrayList());
        }
        for (int i = 0 ; i < getReference().getValue().size() ; i++) {
            constantIDs.add(IDS);
            IDS++;
        }
    }

    @Override
    public void onSelect(ChatEditorMenu menu) {
        selected = true;

        this.removeCommand = menu.getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                Integer in = Integer.parseInt(left);
                if (in == null) {
                    menu.getTreeRoot().sendMenu();
                    p.sendMessage("nieporawny numer do usniecia: " + left);
                }
                else {
                    removeElement(in);
                    menu.getTreeRoot().sendMenu();
                }
            }
        });
        this.editListElement = getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                Integer in = Integer.parseInt(left);
                if (in == null) {
                    menu.getTreeRoot().sendMenu();
                    p.sendMessage("nieporawny numer do usniecia: " + left);
                }
                else {
                    openEditor(in);
                }
            }
        });
        commandBackToList = getTreeRoot().commands().register(new SimpleButtonGui((p)-> {
            quitListElementEditor();
            getTreeRoot().sendMenu();
        }));

        createNewObject = getTreeRoot().commands().register(new GuiCommand() {
            @Override
            public void execute(Player p, String left) {
                createNObjhect(left);
            }
        });

    }

    @Override
    public void initialize(Ref<List> reference) {
        super.initialize(reference);
        if (reference.getValue() == null)
            reference.setValue(new ArrayList());
    }

    public void createNObjhect(String name) {
        for (NewInstanceData n :classes) {
            if (n.name.contentEquals(name)) {
                try {
                    Object o = Class.forName(n.Clazz).newInstance();
                    addElement(o);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onDeselect() {
        selected = false;
        quitListElementEditor();
    }



    @Override
    public void enableEditor(ChatEditorMenu owner) {
        selectToEditCommand = owner.getTreeRoot().commands().register( new SimpleButtonGui(c-> {
            owner.getTreeRoot().select(this);
        }));

    }



    @Override
    public void disableEditor() {

    }

    @Override
    public void sendItem(Player p) {
        if (constantIDs == null) {
            fixIDs();

        }
        if (selected){
            if (editor != null) {
                editor.sendMenu();
            }
            else {
                p.sendMessage("lista wielkosci: " + getReference().getValue().size() + " typu: " + genericName);
                TextComponent tc;
                TextComponent in ;
                for (int i = 0 ; i < constantIDs.size() ;i++) {
                    tc = new TextComponent("[" + i +"]" );
                    in = new TextComponent("[X]");
                    in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , removeCommand + " " + constantIDs.get(i)));
                    in.setColor(ChatColor.RED);
                    tc.addExtra(in);
                    tc.addExtra(",   ,");
                    in = new TextComponent("[selectEditorCommand]");
                    in.setColor(ChatColor.GREEN);
                    in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND ,this.editListElement + " " +constantIDs.get(i)));
                    tc.addExtra(in);
                    tc.addExtra(" ,value: " + getReference().getValue().get(i).toString());
                    p.spigot().sendMessage(tc);
                }
                tc = new TextComponent("createN: ");
                for (NewInstanceData n : this.classes) {
                    in = new TextComponent(n.name);

                    in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , createNewObject + " " + n.name));
                    tc.addExtra(in);
                    tc.addExtra(" ,");
                }
                p.spigot().sendMessage(tc);
            }
        }
        else {
            TextComponent tc = new TextComponent("[L:"+SpigotUtils.fixStringLength(genericName , 4) + "] " + getName() + " ");
            TextComponent in = new TextComponent("[edytuj]");
            in.setColor(ChatColor.GREEN);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , selectToEditCommand));
            tc.addExtra(in);
            tc.addExtra(" wielkosc: " + getReference().getValue().size());
            p.spigot().sendMessage(tc);
        }
    }

    public void quitListElementEditor() {
        if (editor != null) {
            editor.disableSilently(EditorResult.FAIL , false);
            editor = null;
        }
    }

    public void openEditor(int id) {
        Object o = getElement(id);
        quitListElementEditor();
        editor = new ChatCommandEditor(getTreeRoot().getPlayer(),o);
        editor.sendOnMainPage = (e) -> {
            TextComponent tc = new TextComponent("[<<< Back To List Editor <<<]");
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.commandBackToList));
            getTreeRoot().getPlayer().spigot().sendMessage(tc);
        };
        getTreeRoot().sendMenu();
    }

    public void removeElement(int id) {
        for(int i = 0 ; i < constantIDs.size() ; i++) {
            if (constantIDs.get(i) == id) {
                constantIDs.remove(i);
                getReference().getValue().remove(i);
                return;
            }
        }
    }

    public void addElement(Object o) {
        getReference().getValue().add(o);
        this.constantIDs.add(IDS);
        openEditor(IDS);
        IDS++;
    }

    public Object getElement(int id) {
        for (int i = 0 ; i < constantIDs.size() ; i++) {
            if (constantIDs.get(i) == id)
                return getReference().getValue().get(i);
        }
        return null;
    }

    @Override
    public void init(Field f) {
        Type t = f.getGenericType();
        Class clazz = f.getType();
        if (t instanceof ParameterizedType) {
            initWithParametrizedType((Class) ((ParameterizedType) t).getActualTypeArguments()[0]);
        }
        else
        {
            SDIPlugin.instance.logWarning(this, "brak typu generycznego dla klasy: " + clazz.getName() + " field " +f.getName());
            this.genericType = Object.class;
        }
    }

    private void initWithParametrizedType(Class type) {
        genericType = type;
        if (genericType.isAnnotationPresent(ChatObjectName.class))
            genericName = ((ChatObjectName) genericType.getAnnotation(ChatObjectName.class)).name();
        else
            genericName = genericType.getSimpleName();
        classes = IManager.getManager(ChatEditorManager.class).getAllSubClasses(genericType);

        generated = true;
    }




}
