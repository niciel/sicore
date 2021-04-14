package com.niciel.superduperitems.inGameEditor;

//import com.niciel.superduperitems.fakeArmorstands.ArmorStandModel;
//import com.niciel.superduperitems.fakeArmorstands.ArmorStandModelEditor;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.inGameEditor.editors.*;
import com.niciel.superduperitems.inGameEditor.editors.object.*;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
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
        getEditor(player).sendMenu();
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
        addSupplier(List.class , (e,clazz,name,desciption) -> {
            return new EditorChatList(e,name,desciption);
        });
        addSupplier(ArrayList.class , (e,clazz,name,desciption) -> {
            return new EditorChatList(e,name,desciption);
        });
        addSupplier(Vector.class , (e, clazz,name , description) -> {
            return new EditorChatVector(e ,name , description , EditorChatVector.class);
        });
        addSupplier(ItemStack.class ,(e,clazz,name,description) -> {
            return new ItemStackEditor(e,name,description);
        } );
        //addSupplier(ArmorStandModel.class , new PrimitiveSuppiler(String.class));



        GsonManager m = IManager.getManager(GsonManager.class);

        register(TestEditor.class);
        register(TestExtends.class);

    }



    protected void removePlayerFromEditorMap(Player p) {
        if (editors.containsKey(p.getUniqueId()))
            editors.remove(p.getUniqueId());
    }

    public IBaseObjectEditor getEditor(Player p) {
        return editors.get(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (editors.containsKey(e.getPlayer().getUniqueId())) {
            IBaseObjectEditor editor = editors.remove(e.getPlayer().getUniqueId());
            editor.disable(EditorResult.PLAYER_QUIT);
        }
    }

    private String world = "testN";


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()== false)
            return false;
        if (strings.length == 0) {
            commandSender.sendMessage("liczba otwartych edytorow: " +editors.size());
//            TODO
        }
        File toSave = new File(SDIPlugin.instance.getDataFolder() , "test.yml");
        Object o = null;
        TestExtends t = new TestExtends();
        if (strings.length >= 2){
            try {
                FileReader reader = new FileReader(toSave);
                BufferedReader r = new BufferedReader(reader);
                final StringBuilder sb = new StringBuilder();
                String st = r.readLine();
                int iteration = 0;
                while (st != null) {
                    sb.append(st);
                    st = r.readLine();
                }
                r.close();
                reader.close();
                o = GsonManager.getInstance().fromJson(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            t = (TestExtends) o;
            IBaseObjectEditor<TestEditor> editor = this.createChatCommandEditor((Player) commandSender , t);
            editor.setExitConsumer((reson,ed) -> {
                System.out.println("exitcode " + reson);
                if (reson.applayChanges()) {
                    try {
                        FileWriter w = new FileWriter(toSave);
                        w.write(GsonManager.getInstance().toJson(ed.getReference().getValue()).toString());
                        w.flush();
                        w.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    private void registerIfNotExists(NewInstanceData base, Class in) {
        List<NewInstanceData> set = classParents.get(in.getName());
        if (set == null) {
            set = new ArrayList<>();
            set.add(base);
            classParents.put(in.getName() , set);
        }
        else {
            if (containsInClassesParentsCollection(base.Clazz , set) == false) {
                set.add(base);
            }
        }
    }



    private boolean containsInClassesParentsCollection(String clazz , Collection<NewInstanceData> data) {
        return data.stream().filter( p-> p.Clazz.contentEquals(clazz)).findFirst().isPresent();
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

    public ChatEditor getEditor(IBaseObjectEditor editor , Class clazz , String name , String description) {
        Dual<Class , IChatEditorSuppiler> dual = classNameToEditor.get(clazz.getName());
        if (dual != null) {
            return dual.second.get(editor,clazz,name,description);
        }
        for (Dual<Predicate<Class> , IChatEditorSuppiler> d : suppilerss) {
            if (d.first.test(clazz))
                return d.second.get(editor,clazz,name,description);
        }

        if (clazz.isEnum())
            return new EnumEditor(clazz,name,description );

        return new EditorChatObject(editor , name,description , clazz );
    }




}
