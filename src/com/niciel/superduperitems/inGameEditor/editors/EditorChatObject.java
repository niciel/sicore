package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.commandGui.GuiCommand;
import com.niciel.superduperitems.commandGui.SimpleButtonGui;
import com.niciel.superduperitems.inGameEditor.*;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public  class EditorChatObject<T extends Object> extends IChatEditorMenu<T> implements IFieldEditor {

    private String description;
    private String name;
    private List<InheredClasses> menuTree;
    private Ref<T> reference;
    private boolean selected = false;
    private ChatEditorManager manager;

    private Collection<NewInstanceData> classes;
    private boolean enabledEditors = false;

    private boolean fieldeditor = false;
    private Class fieldType;

    private String createNewCommand;
    private String editCommand;

    public EditorChatObject(IBaseObjectEditor owner,String name, String description, Class baseType ) {
        super(owner, name, description, baseType);
        this.manager = IManager.getManager(ChatEditorManager.class);
        this.menuTree = new ArrayList<>();

    }

    protected void generate(Object o) {
        if (enabledEditors)
            disableFieldEditors();
        Class c = o.getClass();
        while (c.getName().equals(Object.class.getName()) == false) {
            generate(c);
            c = c.getSuperclass();
        }
    }


    @Override
    public void init(Field f) {
        fieldeditor = true;
        fieldType = f.getType();
    }

    private void generate(Class clazz) {
        menuTree.clear();

        ChatEditable editable;
        String name;
        String description;
        InheredClasses inh;
        IChatEditor editor;
        Class fieldType;
        final WeakReference<EditorChatObject> ownerEditor = new WeakReference<>(this);
        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        if (clazz.isAnnotationPresent(ChatObjectName.class))
            inh = new InheredClasses( ((ChatObjectName ) clazz.getDeclaredAnnotation(ChatObjectName.class)).name());
        else
            inh = new InheredClasses(clazz.getSimpleName());
        final List<FieldData> data = new ArrayList<>();
        inh.fields = data;
        menuTree.add(inh);
        for (Field f : clazz.getDeclaredFields()) {
            editable = f.getAnnotation(ChatEditable.class);
            if (editable == null || editable.excludeInEdit())
                continue;
            description = editable.description();
            if (editable.name().isEmpty())
                name = f.getName();
            else
                name = editable.name();
            RefCallBack ref = new RefCallBack();
            fieldType = f.getType();
            editor = manager.getEditor(getTreeRoot(),fieldType);
            try {
                if (! f.isAccessible()) {
                    f.setAccessible(true);
                }
                MethodHandle handle = lookup.unreflectSetter(f);
                ref.setValue(f.get(reference.getValue()));
                ref.addCallBack(c->{
                    try {
                        handle.invoke(ownerEditor.get().reference.getValue() , ((Ref)c).getValue());
                        ownerEditor.get().getTreeRoot().sendMenu();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (IllegalAccessException e ) {
                e.printStackTrace();
            }
            if (editor instanceof IFieldEditor)
                ((IFieldEditor) editor).init(f);
            data.add(new FieldData(fieldType , name , description , ref , editor));
        }
    }

    @Override
    public void onSelect(Ref<T> ref) {
        this.reference = ref;
        if (fieldeditor) {
            classes = IManager.getManager(ChatEditorManager.class).getAllSubClasses(fieldType);
        }
        if (reference.getValue() != null) {
            generate(reference.getValue());
            enableFieldEditors();
        }
        else {
            createNewCommand = getTreeRoot().commands().register(new GuiCommand() {

                @Override
                public void execute(Player p, String left) {
                    NewInstanceData d = find(left);
                    if (d == null) {
                        p.sendMessage("blad !!! zla nazwa: " + left);
                        getTreeRoot().sendMenu();
                    }
                    else {
                        p.sendMessage("znaleziono proba zrobienia ");
                        createInstance(d);
                    }
                }
            });
        }

        selected = true;
    }

    @Override
    public void onDeselect() {
        for (InheredClasses inh : menuTree) {
            for (FieldData d : inh.fields) {
                d.editor.disableEditor();
            }
        }
        selected = false;
    }



    protected void enableFieldEditors() {
        for (InheredClasses c : this.menuTree) {
            for (FieldData f : c.fields) {
                f.editor.enableEditor(this , f.reference);
            }
        }
        enabledEditors = true;
    }

    protected void disableFieldEditors() {
        for (InheredClasses c : this.menuTree) {
            for (FieldData f : c.fields) {
                f.editor.disableEditor();
            }
        }
        enabledEditors = false;
    }


    private NewInstanceData find(String name) {
        for (NewInstanceData d : classes) {
            if (d.name.equals(name))
                return d;
        }
        return null;
    }

    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<T> ref) {
        reference = ref;
        EditorChatObject inst = this;
        editCommand = getTreeRoot().commands().register(new SimpleButtonGui(player -> {
            owner.getTreeRoot().select(inst);
        }));
    }


    private void createInstance(NewInstanceData d) {
        if (selected) {
            if (enabledEditors = false) {
                T obj;
                Class clazz = null;
                try {
                    clazz = Class.forName(d.Clazz);
                    obj = (T) clazz.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return;
                }
                generate(obj);
                reference.setValue(obj);
            }
        }
    }

    @Override
    public void disableEditor( ) {
        disableFieldEditors();
        reference = null;
    }


    @Override
    public void sendItem(Player p) {
        if (selected) {
            if (reference.getValue() != null) {
                TextComponent tc = new TextComponent("********edytor:"+ getName() +"********");
                tc.setColor(ChatColor.GRAY);
                p.spigot().sendMessage(tc);
                for (InheredClasses inh : menuTree) {
                    tc = new TextComponent("****typ: " + inh.nameOfClass+ "****");
                    tc.setColor(ChatColor.GRAY);
                    p.spigot().sendMessage(tc);
                    for (FieldData d : inh.fields) {
                        d.editor.sendItem(p);
                    }
                }
            }
            else {
                TextComponent tc = new TextComponent("********new:"+ name +"********");
                TextComponent in ;
                tc.setColor(ChatColor.GRAY);
                p.spigot().sendMessage(tc);
                for (NewInstanceData nid : classes) {
                    tc = new TextComponent("typ: " + SpigotUtils.fixStringLength(nid.name,15) );
                    tc.setColor(ChatColor.GRAY);
                    tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT ,new Text(description)));
                    in = new TextComponent("[new]");
                    in.setColor(ChatColor.GOLD);
                    in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , createNewCommand + " " + nid.name));
                    tc.addExtra(in);
                    p.spigot().sendMessage(tc);
                }
            }
        }
        else {
            TextComponent tc = new TextComponent("Object:[" + getName()+"]: ");
            TextComponent in  = new TextComponent("[edit]");
            tc.setColor(ChatColor.GRAY);
            in.setColor(ChatColor.GREEN);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND ,editCommand));
            tc.addExtra(in);
            p.spigot().sendMessage(tc);
        }


    }

    private class InheredClasses {

        protected  InheredClasses(String nameof) {
            this.nameOfClass = nameof;
        }

        public String nameOfClass;
        public List<FieldData> fields;
    }

    private class FieldData {

        public FieldData(Class type, String name, String description, Ref reference, IChatEditor item) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.reference = reference;
            this.editor = item;

        }

        public Class type;
        public String name;
        public String description;
        public Ref reference;
        public IChatEditor editor;

    }


/*
    private GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    private List<EditorData> list;

    private WeakReference<ChatCommandEditor> editor;
    private String name;
    private String description;
    private Ref<T> reference;
    private CommandPointer selectEditor;
    private Class type;
    private List<ToGen> toGenerate;

    public void generate() {
        this.list = new ArrayList<>();
        if (reference.getValue() == null) {

            List<Class> list = SpigotUtils.getPluginClasses();
            toGenerate = new ArrayList<>();
            List<Class> inf = new ArrayList<>();
            list.forEach( c-> {
                if (type.isAssignableFrom(c))
                    inf.add(c);
            });
            WeakReference<EditorChatObject> _instance = new WeakReference<>(this);
            CommandPointer pointer;

            for (Class clazz : inf) {
                final ToGen generate = new ToGen();
                generate.pointer = manager.registerGuiCommand((p,a) -> {
                    _instance.get().generateObject(generate);
                },this.getClass() , SDIPlugin.instance);
                generate.clazz = clazz;
                generate.name = clazz.getSimpleName();
                toGenerate.add(generate);
            }
        }
        else {
            Class clazz = reference.getValue().getClass();
           // while (clazz.getName().contentEquals(Object.class.getName()) == false) {
                generate( clazz);
                generateForMethodInClass(clazz);
                clazz = clazz.getSuperclass();
                editor.get().send();
            //}
        }
    }

    private void generateObject(ToGen to) {
        Object o;
        try {
            o = to.clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        toGenerate = null;
        reference.setValue((T) o);
        generate();
    }

    protected void generate( Class clazz) {
        Class fieldType;
        ChatEditable editable;
        IChatEditor editor;
        EditorData d;
        String name;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Field f : clazz.getDeclaredFields()) {
            editable = f.getAnnotation(ChatEditable.class);
            if (editable == null)
                continue;
            if (this.editor != null) {
                if (this.editor.get().isEditMode() && editable.excludeInEdit())
                    continue;
            }
            if (f.isAccessible() == false)
                f.setAccessible(true);

            if (editable.name() != null && ! editable.name().isEmpty())
                name = editable.name();
            else
                name = f.getName();

            fieldType = f.getType();

            editor = ChatCommandEditor.getEditor(fieldType);
            if (fieldType == null)
            {
                SDIPlugin.instance.logWarning(this , "nie odnaleziono edytora do pola " + f.getType().getName());
                continue;
            }
            d = new EditorData(reference.getValue());
            try {
                d.setter = lookup.unreflectSetter(f);
                d.getter = lookup.unreflectGetter(f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            WeakReference<EditorData> _data = new WeakReference<>(d);
            d.editor = editor;
            d.get();
            RefCallBack call = new RefCallBack(d.get());
            call.addCallBack(c -> {
                _data.get().set( ((RefCallBack) c).getValue() );
            });
            list.add(d);
            if (editor instanceof IFieldEditor)
                ((IFieldEditor) editor).filed(f);
            editor.enable(this.editor , name , description , f.getType() , call);
        }
    }


    protected void generateForMethodInClass(Class clazz) {
        ChatEditableMethod editable;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        EditorChatMethod editor;
        MethodHandle handle;

        for (Method m : clazz.getDeclaredMethods()) {
            editable = m.getAnnotation(ChatEditableMethod.class);
            if (editable == null)
                continue;
            if (m.getParameterCount() != 1) {
                SDIPlugin.instance.logWarning(this , "liczba parametrow nie moze byc rozna od 1 " + m.toString() + " clazz " +clazz.getName());
                continue;
            }
            if (! m.getParameters()[0].getType().getName().contentEquals(EditorChatMethod.class.getName())) {
                SDIPlugin.instance.logWarning(this , "parametr powinien byc innego typu " + m.toString() + " clazz " +clazz.getName());
                continue;
            }
            m.setAccessible(true);
            try {
                handle = lookup.unreflect(m);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            EditorChatMethod method = new EditorChatMethod(reference.getValue() , handle);
            EditorData data = new EditorData(this);
            data.editor = method;
            list.add(data);
        }

    }




    @Override
    public void sendMenu(Player p) {
        if (reference.getValue() == null) {
            TextComponent tc = null;
            TextComponent in;
            int max = 0;
            for (ToGen g : toGenerate) {
                if (max %4==0) {
                    if (tc != null)
                        p.spigot().sendMessage(tc);
                    tc = new TextComponent("  dodaj");
                    tc.setColor(ChatColor.GRAY);
                }
                else
                    tc.addExtra(", ");
                in = new TextComponent("[" + g.name +"]");
                if (g.clazz.isInterface()) {
                    in.setColor(ChatColor.BLACK);
                }
                else {
                    in.setColor(ChatColor.GOLD);
                    in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , g.pointer.getCommand()));
                }
                tc.addExtra(in);
                max++;
            }
            p.spigot().sendMessage(tc);
        }
        else {
            p.sendMessage(new String[]{"","","","","","","","",""});
            p.sendMessage("size " + list.size());
            for (EditorData d : list) {
                d.editor.sendItem(p);
            }
            TextComponent tc;

            for (Dual<String, CommandPointer> dual : commands) {
                tc = new TextComponent("[" + dual.first + "]");
                tc.setColor(ChatColor.GREEN);
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , dual.second.getCommand()));
                p.spigot().sendMessage(tc);
            }
        }

    }



    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("OE name" + name + " : ");
        tc.setColor(ChatColor.GRAY);
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder().append(description).create()));
        TextComponent in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , selectEditor.getCommand()));
        tc.addExtra(in);
        String value;
        if (reference.getValue() == null)
            value = "null";
        else
            value = "class:"+reference.getValue().getClass().getSimpleName();
        tc.addExtra(" " + value);
        p.spigot().sendMessage(tc);
    }



    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<T> refToObject) {
        this.editor = editor;
        this.name = name;
        this.reference = refToObject;
        this.description = description;
        this.type = type;
        WeakReference<EditorChatObject> _instance = new WeakReference<>(this);

        if (this.editor != null) {
            selectEditor = manager.registerGuiCommand((P,a) -> {
                ((ChatCommandEditor) _instance.get().editor.get() ).select(_instance.get());
            } , this.getClass() , SDIPlugin.instance);
        }
        else {
            selectEditor = manager.registerGuiCommand((P,a) -> {
                ((EditorChatObject) _instance.get()).sendMenu(P);
            } , this.getClass() , SDIPlugin.instance);
        }
        generate();
    }


    private class ToGen {
        Class clazz;
        String name;
        CommandPointer pointer;
    }
    */
    // nowe







}
