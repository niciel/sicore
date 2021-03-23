package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.commandGui.helpers.GuiCommand;
import com.niciel.superduperitems.commandGui.helpers.SimpleButtonGui;
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
    private boolean selected = false;
    private ChatEditorManager manager;

    private Collection<NewInstanceData> classes;
    private boolean enabledEditors = false;

    private boolean fieldeditor = false;
    private Class fieldType;

    private String createNewCommand;
    private String editCommand;


    private boolean isFieldCallBackEnabled = false;

    public EditorChatObject(IBaseObjectEditor owner,String name, String description, Class baseType ) {
        super(owner, name, description, baseType);
        this.manager = IManager.getManager(ChatEditorManager.class);
        this.menuTree = new ArrayList<>();
    }

    protected void generate(Object o) {
        if (enabledEditors)
            disableFieldEditors();
        menuTree.clear();
        Class c = o.getClass();
        if (o instanceof IFieldUpdateCallBack)
            isFieldCallBackEnabled = true;
        else
            isFieldCallBackEnabled = false;
        while (c.getName().equals(Object.class.getName()) == false) {
            generate(c);
            //System.out.println("generated " + c.getName());
            c = c.getSuperclass();
        }
    }


    @Override
    public void init(Field f) {
        fieldeditor = true;
        fieldType = f.getType();
    }

    private void generate(Class clazz) {
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
            String fieldName = f.getName();
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
                ref.setValue(f.get(getReference().getValue()));
                ref.addCallBack(c->{
                    try {
                        handle.invoke(ownerEditor.get().getReference().getValue() , ((Ref)c).getValue());
                        ownerEditor.get().getTreeRoot().sendMenu();
                        if (ownerEditor.get().isFieldCallBackEnabled) {
                            ((IFieldUpdateCallBack) ownerEditor.get()).validate(fieldName);
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (IllegalAccessException e ) {
                e.printStackTrace();
            }
            if (editor instanceof IFieldEditor)
                ((IFieldEditor) editor).init(f);
            editor.initialize(ref);
            data.add(new FieldData(fieldType , name , description , editor));
        }
    }

    @Override
    public void onSelect(IChatEditorMenu menu) {
        if (fieldeditor) {
            classes = IManager.getManager(ChatEditorManager.class).getAllSubClasses(fieldType);
        }
        if (getReference().getValue() != null) {
            generate(getReference().getValue());
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
                        menu.getTreeRoot().sendMenu();
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
        if (getReference().getValue() instanceof IObjectSelfEditable)
            ((IObjectSelfEditable) getReference().getValue()).onDisableEditor(this);
        selected = false;
    }

    protected void enableFieldEditors() {
        for (InheredClasses c : this.menuTree) {
            for (FieldData f : c.fields) {
                f.editor.enableEditor(this );
            }
        }
        if (getReference().getValue() instanceof IObjectSelfEditable)
            ((IObjectSelfEditable) getReference().getValue()).onEnableEditor(this);
        enabledEditors = true;
    }

    protected void disableFieldEditors() {
        for (InheredClasses c : this.menuTree) {
            for (FieldData f : c.fields) {
                f.editor.disableEditor();
            }
        }
        enabledEditors = false;
        //if (getReference().getValue() instanceof IObjectSelfEditable)
        //    ((IObjectSelfEditable) getReference().getValue()).onDisableEditor(this);
    }

    private NewInstanceData find(String name) {
        for (NewInstanceData d : classes) {
            if (d.name.equals(name))
                return d;
        }
        return null;
    }

    @Override
    public void enableEditor(IChatEditorMenu owner) {
        EditorChatObject inst = this;
        editCommand = getTreeRoot().commands().register(new SimpleButtonGui(player -> {
            owner.getTreeRoot().select(inst);
        }));

    }


    private void createInstance(NewInstanceData d) {
        if (selected) {
            if (enabledEditors) {
                disableFieldEditors();
            }
            T obj;
            Class clazz = null;
            try {
                clazz = Class.forName(d.Clazz);
                obj = (T) clazz.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            getReference().setValue(obj);
            generate(obj);
            enableFieldEditors();
            if (obj instanceof IObjectSelfEditable)
                ((IObjectSelfEditable) obj).onEnableEditor(this);
            getReference().setValue(obj);
        }
    }

    @Override
    public void initialize(Ref<T> reference) {
        super.initialize(reference);


    }

    @Override
    public void disableEditor( ) {
        disableFieldEditors();
    }


    @Override
    public void sendItem(Player p) {
        if (selected) {
            if (getReference().getValue() != null) {
                TextComponent tc;
                for (InheredClasses inh : menuTree) {
                    tc = new TextComponent("****typ: " + inh.nameOfClass+ "****");
                    tc.setColor(ChatColor.GRAY);
                    p.spigot().sendMessage(tc);
                    for (FieldData d : inh.fields) {
                        d.editor.sendItem(p);
                    }
                }
                if (getReference().getValue() instanceof IObjectSelfEditable)
                    ((IObjectSelfEditable) getReference().getValue()).onSendItemMenu(p);
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

        public FieldData(Class type, String name, String description, IChatEditor item) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.editor = item;

        }

        public Class type;
        public String name;
        public String description;
        public IChatEditor editor;

    }




}
