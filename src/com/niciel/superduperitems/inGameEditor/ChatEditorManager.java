package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.fakeArmorstands.ArmorStandModel;
import com.niciel.superduperitems.fakeArmorstands.ArmorStandModelEditor;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;
import com.niciel.superduperitems.inGameEditor.editors.*;
import com.niciel.superduperitems.utils.Dual;
import com.niciel.superduperitems.utils.IManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChatEditorManager implements IManager , Listener {


    private  HashMap<String , Dual<Class , IChatEditorSuppiler>> classNameToEditor = new HashMap<>();
    private  ArrayList<Dual<Predicate<Class>, IChatEditorSuppiler>> suppilerss = new ArrayList<>();
    private HashMap<UUID , EditorData> editors = new HashMap<>();


    public ChatEditorManager() {
        init();
    }


    public <T> ChatCommandEditor<T> createChatCommandEditor(Player player , T toEdit) {
        return new ChatCommandEditor<>(player , toEdit);
    }

    public <T> ChatCommandEditor<T> enableChatCommandEditor(Player player , T toEdit) {
        ChatCommandEditor e = getEditor(player);
        if( e != null) {
            return e;
        }
        e = new ChatCommandEditor(player , toEdit);
        enable(e , false);
        return e;
    }

    protected void init() {
        addSupplier(String.class , new PrimitiveSuppiler(String.class));
        addSupplier(int.class , new PrimitiveSuppiler(String.class));
        addSupplier(Integer.class , new PrimitiveSuppiler(String.class));
        addSupplier(List.class , new PrimitiveSuppiler(String.class));
        addSupplier(ArrayList.class , new PrimitiveSuppiler(String.class));
        //addSupplier(p -> ItemStack.class.isAssignableFrom(p) , EditorChatItemStack::new);//TODO
        addSupplier(Double.class , new PrimitiveSuppiler(String.class));
        addSupplier(double.class , new PrimitiveSuppiler(String.class));
        addSupplier(Float.class , new PrimitiveSuppiler(String.class));
        addSupplier(float.class , new PrimitiveSuppiler(String.class));
        addSupplier(Vector.class , new PrimitiveSuppiler(String.class));
        addSupplier(ArmorStandModel.class , new PrimitiveSuppiler(String.class));
    }


    public void enable(ChatCommandEditor e , boolean allowmultiple) {
        if (getEditor(e.getPlayer()) != null) {
            return;
        }
        UUID uuid = e.getPlayer().getUniqueId();
        e.generate();
        editors.put(uuid , new EditorData(e , uuid ,allowmultiple ));
        e.send();
    }

    public void removeEditor(Player p) {
        ChatCommandEditor e = getEditor(p);
        if (e== null)
            return;
        e.onRemove();
        editors.remove(p.getUniqueId());
    }

    public ChatCommandEditor getEditor(Player p) {
        EditorData data = editors.get(p.getUniqueId());
        if (data != null)
            return data.editor;
        return null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeEditor(e.getPlayer());
    }

    public class EditorData {

        public EditorData(ChatCommandEditor editor, UUID owner, boolean allowMultipleEditors) {
            this.editor = editor;
            this.owner = owner;
            this.allowMultipleEditors = allowMultipleEditors;
            this.extraEditors = new ArrayList<>();
        }

        public ChatCommandEditor editor;
        public UUID owner;
        public boolean allowMultipleEditors;
        public List<ChatCommandEditor> extraEditors;

    }


    public <T> void  addSupplier(Class<T> clazz , IChatEditorSuppiler sup) {
        classNameToEditor.put(clazz.getName()  , new Dual(clazz , sup));
    }

    public <T> void  addSupplier(Predicate<Class> predict , IChatEditorSuppiler sup) {
        suppilerss.add(new Dual<>(predict , sup));
    }

    public  IChatEditor getEditor(IBaseObjectEditor editor , Class clazz) {
        Dual<Class , IChatEditorSuppiler> dual = classNameToEditor.get(clazz.getName());
        if (dual != null) {
            return dual.second.get(editor,clazz);
        }
        for (Dual<Predicate<Class> , IChatEditorSuppiler> d : suppilerss) {
            if (d.first.test(clazz))
                return d.second.get(editor,clazz);
        }

        if (clazz.isEnum())
            return new EnumEditor(clazz);

        /* copy paste start*/
        String name;
        String description;
        if (clazz.isAnnotationPresent(ChatObjectName.class)) {
            name = ((ChatObjectName)clazz.getDeclaredAnnotation(ChatObjectName.class)).name();
            description = "base";
        }
        else {
            name = clazz.getSimpleName();
            description = "?base?";
        }

        /* copy paste end*/
        return new EditorChatObject(editor , name,description , clazz);
    }
}
