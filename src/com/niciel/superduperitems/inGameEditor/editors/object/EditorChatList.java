package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
import com.niciel.superduperitems.inGameEditor.*;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class EditorChatList extends IChatEditorMenu<List> implements IFieldEditor  {

    private Class genericType;
    private String genericName;
    private boolean selected;
    private Ref<List> reference;
    private Collection<NewInstanceData> classes;

    private String selectToEditCommand;

    private boolean generated = false;


    public EditorChatList(IBaseObjectEditor owner, String name, String description,  Class clazz ) {
        super(owner, name, description, clazz);
    }

    @Override
    public void onSelect(IChatEditorMenu menu) {
        selected = true;
    }

    @Override
    public void onDeselect() {
        selected = false;
    }

    @Override
    public void enableEditor(IChatEditorMenu owner) {
        selectToEditCommand = owner.getTreeRoot().commands().register( new SimpleButtonGui(c-> {
            owner.getTreeRoot().select(this);
        }));
    }



    @Override
    public void disableEditor() {
        reference = null;
    }

    @Override
    public void sendItem(Player p) {
        if (selected){

        }
        else {
            TextComponent tc = new TextComponent("[L:"+SpigotUtils.fixStringLength(genericName , 4) + "] " + getName() + " ");
            TextComponent in = new TextComponent("[edytuj]");
            in.setColor(ChatColor.GREEN);
            //in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , sendEditList.getCommand()));
            tc.addExtra(in);
            tc.addExtra(" wielkosc: " + classes.size());
            p.spigot().sendMessage(tc);
        }
    }

    @Override
    public void init(Field f) {
        Type t = f.getGenericType();
        Class clazz = f.getType();
        if (t instanceof ParameterizedType) {
            this.genericType = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
        }
        else
        {
            SDIPlugin.instance.logWarning(this, "brak typu generycznego dla klasy: " + clazz.getName() + " field " +f.getName());
            this.genericType = Object.class;
        }

        if (genericType.isAnnotationPresent(ChatObjectName.class))
            genericName = ((ChatObjectName) genericType.getAnnotation(ChatObjectName.class)).name();
        else
            genericName = genericType.getSimpleName();
        classes = IManager.getManager(ChatEditorManager.class).getAllSubClasses(genericType);
        generated = true;
    }

   /*
    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private WeakReference<IChatEditorMenu> owner;
    private Ref<List> reference;

    private List list;

    private List<ListEditorData> listEditor;

    private List<String> genericTypesName;

    private List<Dual<String , CommandPointer>> genericTypesAddPointers;

    private String genericTypeName;
    private CommandPointer sendEditList;
    private CommandPointer removeP;

    private Class genericType;
    private String name;

    private int count = 0;


    private WeakReference<ChatCommandEditor> editor;


    public void addEditorElement(Object o) {
        IChatEditor editor = ChatCommandEditor.getEditor(o.getClass());
        RefCallBack ref = new RefCallBack(o);
        WeakReference<EditorChatList> _instance = new WeakReference<>(this);
        editor.enable(this.editor , "name" , "description" , o.getClass() , ref);
        //ponizej
        int numerID = getNextInnerInt();
        ListEditorData listData = new ListEditorData(numerID, removeP.getCommand() , ref , editor);
        ref.addCallBack(c-> {
            _instance.get().update(Integer.toString(numerID));
        });
        listEditor.add(listData);
    }

    public void addElementToList( Class type) {
        RefCallBack ref = new RefCallBack(null);
        IChatEditor editor  = ChatCommandEditor.getEditor(type);
        editor.enable(this.editor , "name" , "deescription" , type , ref);
        //ponizej
        int numerID = getNextInnerInt();
        ListEditorData listData = new ListEditorData(numerID, removeP.getCommand() , ref , editor);
        listEditor.add(listData);
        list.add(ref.getValue());
        this.editor.get().send();
        ref.addCallBack( c-> update(String.valueOf(numerID)));
    }

    public int getNextInnerInt() {
        return count++;
    }


    public void update(String numericID) {
        for (int i = 0 ; i < list.size() ; i ++) {
            if (listEditor.get(i).numericID.contentEquals(numericID)) {
                list.set(i , listEditor.get(i).ref.getValue());
                return;
            }
        }
        editor.get().getPlayer().sendMessage("not OK");
    }




    public void remove(String numericID) {
        for (int i = 0 ; i < list.size() ; i ++) {
            if (listEditor.get(i).numericID.contentEquals(numericID)) {
                listEditor.remove(i);
                list.remove(i);
                editor.get().send();
                return;
            }
        }
    }


    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<List> refToObject) {
        this.editor = editor;
        this.name = name;
        this.reference = refToObject;
        this.listEditor = new ArrayList<>();
        if (this.genericType == null)
            this.genericType = type;
        if (reference.getValue() == null) {
            reference.setValue(new ArrayList());
        }
        list = reference.getValue();

        this.genericTypeName = genericType.getSimpleName();

        this.genericTypesName = new ArrayList<>();
        List<Class> inhered = new ArrayList<>();
        List<Class> allclasses = SpigotUtils.getPluginClasses();
        for (Class clazz : allclasses) {
            if (genericType.isAssignableFrom(clazz)) {
                inhered.add(clazz);
            }
        }
        if (! Modifier.isAbstract(genericType.getModifiers()) && ! Modifier.isInterface(genericType.getModifiers())) {
            boolean flag = true;
            for (Class clazz : inhered) {
                if (clazz.getName().contentEquals(genericType.getName())) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                inhered.add(genericType);
        }

        genericTypesAddPointers = new ArrayList<>();
        WeakReference<EditorChatList> _instance = new WeakReference<>(this);

        removeP = manager.registerGuiCommand((pl , a)-> {
            _instance.get().remove(a);
        }, int.class , SDIPlugin.instance);



        //tu jeblo
        for (Class clazz : inhered) {
            final Class _addType = clazz;
            if (clazz.isEnum() || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
                continue;

            // remove
            CommandPointer pointer = manager.registerGuiCommand( (pl , a) -> {
                _instance.get().addElementToList(_addType);
            } , this.getClass() , SDIPlugin.instance);
            genericTypesAddPointers.add(new Dual<>(clazz.getSimpleName() , pointer));
        }
        // tu lepiej



        sendEditList = manager.registerGuiCommand( (player,a)-> {
            _instance.get().editor.get().select(_instance.get());
        } , this.getClass() , SDIPlugin.instance);

        list.forEach(c -> addEditorElement(c));
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("[L:"+SpigotUtils.fixStringLength(genericTypeName , 4) + "] " + name + " ");
        TextComponent in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , sendEditList.getCommand()));
        tc.addExtra(in);
        tc.addExtra(" wielkosc: " + list.size());
        p.spigot().sendMessage(tc);
    }

    @Override
    public void sendMenu(Player p) {
        p.sendMessage("\n\nedytujesz liste wielkosci " + reference.getValue().size());
        TextComponent tc ;
        TextComponent in ;
        int i = 0;
        for (ListEditorData data : listEditor) {
            tc = new TextComponent("      element " + i + " ");
            in = new TextComponent("[X]");
            in.setColor(ChatColor.RED);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , data.fullCommand));
            // zrobic triplet i walnoac wszystko w jedna liste :D
            tc.addExtra(in);
            p.spigot().sendMessage(tc);
            data.editor.sendItem(p);
            i++;
        }
        //dodawnaie
        p.sendMessage("");
        tc = new TextComponent("dodaj: ");
        for (Dual<String , CommandPointer> dual : genericTypesAddPointers) {
            in = new TextComponent(dual.first + " ");
            in.setColor(ChatColor.GOLD);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , dual.second.getCommand()));
            tc.addExtra(in);
        }
        p.spigot().sendMessage(tc);


    }

    @Override
    public void onSelect(ChatCommandEditor editor) {

    }

    @Override
    public void onDeselect(ChatCommandEditor editor) {

    }


    @Override
    public void filed(Field f) {
        Type t = f.getGenericType();
        if (t instanceof ParameterizedType) {
            this.genericType = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
        }
    }
    */
}