package com.niciel.superduperitems.inGameEditor;

//import com.niciel.superduperitems.fakeArmorstands.ArmorStandModel;
//import com.niciel.superduperitems.fakeArmorstands.ArmorStandModelEditor;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.inGameEditor.annotations.ChatObjectName;
import com.niciel.superduperitems.inGameEditor.editors.*;
import com.niciel.superduperitems.managers.SiJavaPlugin;
import com.niciel.superduperitems.managers.SimpleCommandInfo;
import com.niciel.superduperitems.utils.Dual;
import com.niciel.superduperitems.managers.IManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.*;
import java.util.function.Predicate;



@SimpleCommandInfo(command = "ingameeditor" , usage =  "" , description =  "" ,aliases = {})
public class ChatEditorManager implements IManager , Listener , CommandExecutor {


    private HashMap<String , Dual<Class , IChatEditorSuppiler>> classNameToEditor = new HashMap<>();
    private ArrayList<Dual<Predicate<Class>, IChatEditorSuppiler>> suppilerss = new ArrayList<>();
    private HashMap<UUID , IBaseObjectEditor> editors = new HashMap<>();

    private HashMap<String,List<NewInstanceData>> classParents = new HashMap<>();

    public ChatEditorManager() {
        init();
    }


    public <T> IBaseObjectEditor<T> createChatCommandEditor(Player player , T toEdit) {
        if (getEditor(player) == null) {
            ChatCommandEditor<T> editor = new ChatCommandEditor<>(player , toEdit);
            editors.put(player.getUniqueId() , editor);
            return editor;
        }
        return null;
    }

    protected void init() {
        addSupplier(String.class , new PrimitiveSuppiler(EditorChatString.class));
        addSupplier(int.class , new PrimitiveSuppiler(EditorChatInt.class));
        addSupplier(Integer.class , new PrimitiveSuppiler(EditorChatInt.class));
        //addSupplier(List.class , new PrimitiveSuppiler(EditorChatList.class));
        //addSupplier(ArrayList.class , new PrimitiveSuppiler(EditorChatList.class));
        //addSupplier(p -> ItemStack.class.isAssignableFrom(p) , EditorChatItemStack::new);//TODO
        addSupplier(Double.class , new PrimitiveSuppiler(EditorChatDouble.class));
        addSupplier(double.class , new PrimitiveSuppiler(EditorChatDouble.class));
        addSupplier(Float.class , new PrimitiveSuppiler(EditorChatFloat.class));
        addSupplier(float.class , new PrimitiveSuppiler(EditorChatFloat.class));
        //addSupplier(Vector.class , new PrimitiveSuppiler(EditorChatVector.class));
        //addSupplier(ArmorStandModel.class , new PrimitiveSuppiler(String.class));
    }



    protected void removeEditor(Player p , EditorResult result) {
        IBaseObjectEditor e = editors.get(p.getUniqueId());
        if (e != null) {
            e.disable();
            editors.remove(p.getUniqueId());
        }
    }

    public IBaseObjectEditor getEditor(Player p) {
        return editors.get(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (editors.containsKey(e.getPlayer().getUniqueId())) {
            this.removeEditor(e.getPlayer() , EditorResult.PLAYER_QUIT);
        }

    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()== false)
            return false;
        if (strings.length == 0) {
            commandSender.sendMessage("liczba otwartych edytorow: " +editors.size());
//            TODO
        }
        else {
            TestEditor t = new TestEditor();
            IBaseObjectEditor<TestEditor> editor = this.createChatCommandEditor((Player) commandSender , t);
            editor.setExitConsumer((reson,ed) -> {
                if (reson.applayChanges()) {
                    ed.getPlayer().sendMessage("hahaha");
                }
                else {
                    SDIPlugin.instance.logInfo("jest mi bardzo smutno ale " + ed.getPlayer().getDisplayName() + " wyszedl z edytora :(, no i z serwera przy okazji");
                }
            });
            editor.sendMenu();
        }

        return true;
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

    /**
     * register class for editor polimorfizm mechanism , if the class was not mention in register, it will be not displayd in new class constructor of inhered base class
     * @param clazz
     */
    public void register(Class clazz) {
        Class c = clazz;
        NewInstanceData base = new NewInstanceData(c);
        while (c.getName().contentEquals(Object.class.getName()) == false) {
            registerIfNotExists(base , c);
            registerIfNotExists(base , c.getInterfaces());
            c = c.getSuperclass();
        }
    }

    private void registerIfNotExists(NewInstanceData base , Class in[]) {
        for (Class clazz : in) {
            registerIfNotExists(base , clazz);
        }
    }

    private boolean containsInClassesParentsCollection(String clazz , Collection<NewInstanceData> data) {
        return data.stream().filter( p-> p.Clazz.equals(clazz)).findFirst().isPresent();
    }

    private void registerIfNotExists(NewInstanceData base, Class in) {
        List<NewInstanceData> set = classParents.get(in);
        if (set == null) {
            set = new ArrayList<>();
            set.add(base);
            classParents.put(in.getName() , set);
        }
        else {
            if (containsInClassesParentsCollection(base.Clazz , set) == false)
                set.add(base);
        }
    }

    public List<NewInstanceData> getAllSubClasses(Class clazz) {
        String n = clazz.getName();
        if (! classParents.containsKey(n))
            register(clazz);
        return classParents.get(n);
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
        if (clazz.isEnum())
            return new EnumEditor(name,description , clazz);

        /* copy paste end*/
        return new EditorChatObject(editor , name,description , clazz );
    }




}
