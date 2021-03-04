package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.*;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditableMethod;
import com.niciel.superduperitems.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EditorChatObject<T extends Object> extends IChatEditorMenu<T> {

    private String description;
    private String name;
    private List<IChatEditorMenu> menuTree;
    private Ref<T> reference;

    private List<FieldData> fields;

    public EditorChatObject(EditorChatObject owner, int deep, String name, String description, Class baseType) {
        super(owner, deep, name, description, baseType);
    }


    @Override
    public void onSelect() {
        for (FieldData d : fields) {
            d.item.enableEditor(this,d.reference);
        }
    }

    @Override
    public void onDeselect() {
        for (FieldData d : fields) {
            d.item.disableEditor(this);
        }
    }


    @Override
    public void enableEditor(IChatEditorMenu owner, Ref<T> ref) {
        reference = ref;
    }

    @Override
    public void disableEditor(IChatEditorMenu owner) {
        reference = null;
    }

    @Override
    public void sendItem(Player p) {



    }


    private class FieldData {
        public Class type;
        public String name;
        public String description;
        public Ref reference;
        public IChatEditor item;
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
