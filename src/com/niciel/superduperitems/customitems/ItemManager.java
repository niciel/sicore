package com.niciel.superduperitems.customitems;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.niciel.superduperitems.gsonadapter.GsonItemComponent;
import com.niciel.superduperitems.gsonadapter.GsonManager;
import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.cfg.ConfigApi;
import com.niciel.superduperitems.cfg.ObjectCfgSerializer;
import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.customitems.components.BlockDamageComponent;
import com.niciel.superduperitems.customitems.components.Durability;
import com.niciel.superduperitems.customitems.components.ItemInitialization;
import com.niciel.superduperitems.customitems.components.actions.ActionList;
import com.niciel.superduperitems.customitems.components.actions.ActionPotion;
import com.niciel.superduperitems.customitems.components.actions.ICustomItemAction;
import com.niciel.superduperitems.customitems.event.EventCreateItem;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorManager;
import com.niciel.superduperitems.managers.IManager;
import com.niciel.superduperitems.managers.SimpleCommandInfo;
import com.niciel.superduperitems.utils.*;
import com.niciel.superduperitems.utils.itemstack.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;


@SimpleCommandInfo(command = "itemmanager" , aliases =  {} , description = "jakos tak /[comenda]" , usage = "jakos tak /[comenda]" )
public class ItemManager  implements IManager, Listener , CommandExecutor {

    private WeakReference<ChatEditorManager> editor;
    public static String PERMISSION_COMMAND = "sdi.itemManager.admin";
    private File dataFile;
    public HashMap<String , ItemComponentScheme> classNameToItemComponent;
    public DataInventory<ItemCategory> categorys;
    private HashMap<String,  DataInventory<CustomItem>> categoryToInventory;

    private EnumMap<Material , HashMap<Integer , CustomItem>> materialMap;
    private HashMap<String , List<CustomItem>> categoryToItem;
    private HashMap<String , ItemCategory> nameToCategory;
    private HashMap<String , CustomItem> nameIDToItem;

    public HashMap<String , Class<ICustomItemAction>> nameToAction;

    private ItemCategory defaultCategory;

    private HashMap<String , HashSet<String>> excludedCustomItemFields;
    private ArrayList<TextComponent> commands;
    private ArrayList<CommandPointer> commandPointers;
    private CommandPointer pointerEditItem;
    private CommandPointer pointerRemoveItem;
    private ArrayList<ChatCommandEditor<ItemCategory>> categoryEditors;


    public ItemManager() {
//        customItems = new HashMap<>();
        materialMap = new EnumMap<Material, HashMap<Integer, CustomItem>>(Material.class);
        classNameToItemComponent = new HashMap<>();
        categoryToItem = new HashMap<>();
        nameToCategory = new HashMap<>();
        nameToAction = new HashMap<>();
        nameIDToItem = new HashMap<>();
        categoryEditors = new ArrayList<>();
        commands = new ArrayList<>();
        commandPointers = new ArrayList<>();
    }

    @Override
    public void onEnable() {
        SDIPlugin.instance.getGson().registerTypeAdapter(CustomItem.class ,new GsonCustomItem());
        SDIPlugin.instance.getGson().registerTypeAdapter(ItemComponent.class , new GsonItemComponent());


        ConfigApi.register(CustomItem.class , new ObjectCfgSerializer(CustomItem.class , "customItem"));
        ConfigApi.register(ActionList.class , new ObjectCfgSerializer(ActionList.class,"actionList"));
        ConfigApi.register(ItemCategory.class , new ObjectCfgSerializer(ItemCategory.class , "itemCategory"));


        registerItemComponent(Durability.class , "durability");
        registerItemComponent(ItemInitialization.class , "itemDefaults");
        registerItemComponent(BlockDamageComponent.class , "blockDamage");


        registerItemAction(ActionPotion.class , "potionEffect");

        //exclude uneditable fields from customItem
        excludedCustomItemFields = new HashMap<>();
        HashSet<String> excluded = new HashSet<>();
        excluded.add("nameID");
        excluded.add("customDataID");
        excludedCustomItemFields.put(CustomItem.class.getName() , excluded);

    }

    public ItemComponentScheme getScheme(ItemComponent ic ) {
        ItemComponentScheme scheme = classNameToItemComponent.get(ic.getClass().getName());
        if (scheme == null) {
            registerItemComponent(ic.getClass() , ic.getClass().getSimpleName());
            scheme = classNameToItemComponent.get(ic.getClass().getName());
            SDIPlugin.instance.logWarning(this , "FORCE register itemComponent: " + ic.getClass().getName());
        }
        return scheme;
    }


    public void registerItemAction(Class a , String type) {
        ConfigApi.register(a , new ObjectCfgSerializer(a , type));
        nameToAction.put(type , a);
    }

/*
    protected void _initalizeCommands() {
        GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

        WeakReference<ItemManager> _instance = new WeakReference<>(this);



        pointerEditItem = SDIPlugin.instance.getManager(GuiCommandManager.class).registerGuiCommand((p,a) -> {
            if (p.hasPermission(PERMISSION_COMMAND) == false) {
                p.sendMessage("brak pozwolen");
                return;
            }
            ChatCommandEditor editor = getEditor(p);
            if (editor != null) {
                p.sendMessage("nie mozesz tego zrobic gdy masz aktywny edytor");
                editor.send();
                return;
            }
            CustomItem item = _instance.get().getCustomItem(a);
            if (item == null) {
                p.sendMessage("nie istnieje podany customItem ! : '" + a + "'");
                return;
            }

            if (_instance.get().editItem(item , p , true) == false) {
                p.sendMessage("cos poszlo nie tak !");
            }

        } , this.getClass() , SDIPlugin.instance);

        pointerRemoveItem = SDIPlugin.instance.getManager(GuiCommandManager.class).registerGuiCommand((p,a) -> {
            if (p.hasPermission(PERMISSION_COMMAND) == false) {
                p.sendMessage("brak pozwolen");
                return;
            }
            CustomItem item = _instance.get().getCustomItem(a);
            if (item == null) {
                p.sendMessage("nie istnieje podany customItem ! : '" + a + "'");
                return;
            }
            if (_instance.get().removeCustomItem(a)) {
                p.sendMessage("usunięieto :( " + a);
            }
            else
                p.sendMessage("cos poszlo nie tak: " + a);
        } , this.getClass() , SDIPlugin.instance);

        //komendy
        TextComponent tc = new TextComponent("addItemEditor");
        CommandPointer cp = manager.registerGuiCommand( (p,a) -> {
            if (p.hasPermission(PERMISSION_COMMAND)) {
                if (! _instance.get().addCustomItemEditor(p)) {
                    p.sendMessage("masz otwarty edytor");
                    ChatCommandEditor o = getEditor(p);
                    if (o == null) {
                        p.sendMessage("nieprzewidzany blad");
                        return;
                    }
                    o.send();
                }
                else {
                    p.sendMessage("nieprzewidzany blad 201" );
                }
            }
            else {
                p.sendMessage("brak pozwolen");
            }
        } , this.getClass() , SDIPlugin.instance);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , cp.getCommand()));
        tc.setColor(ChatColor.GREEN);
        commands.add(tc);
        commandPointers.add(cp);

        tc = new TextComponent("addCategoryEditor");
        cp = manager.registerGuiCommand( (p,a) -> {
            if (p.hasPermission(PERMISSION_COMMAND)) {
                if (! _instance.get().addItemCategoryEditor(new ItemCategory() , p)) {
                    p.sendMessage("masz otwarty edytor");
                    ChatCommandEditor o = getEditor(p);
                    if (o == null) {
                        p.sendMessage("nieprzewidzany blad");
                        return;
                    }
                    o.send();
                }
            }
        } , this.getClass() , SDIPlugin.instance);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , cp.getCommand()));
        tc.setColor(ChatColor.GREEN);
        commands.add(tc);
        commandPointers.add(cp);



//        cp = manager.registerGuiCommand((p,a) -> {
//            if (p.hasPermission(PERMISSION_COMMAND)) {
//                CustomItem ci = _instance.get().getCustomItem(a);
//                if (ci == null) {
//                    int id;
//                    try {
//                        id = Integer.parseInt(a);
//                        ci = getCustomItem(id);
//                    }
//                    catch (NumberFormatException e) { }
//                }
//                if (ci == null) {
//                    p.sendMessage("nie odnaleziono przedmiotu wprowadz nameid lub modelid");
//                    return;
//                }
//            }
//        } , this.getClass() , SDIPlugin.instance);



        tc = new TextComponent("wyswietl");
        tc.setColor(ChatColor.GREEN);
        cp = manager.registerGuiCommand( (p,a) -> {
            if (p.hasPermission(PERMISSION_COMMAND)) {
                p.openInventory(_instance.get().categorys.getInventory());
                for (CustomItem cat : _instance.get().nameIDToItem.values()) {
                    p.sendMessage(cat.nameID + " " + cat.category);
                }
            }
        } , this.getClass() , SDIPlugin.instance);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , cp.getCommand()));
        commands.add(tc);
        commandPointers.add(cp);

        tc = new TextComponent("serialize");
        tc.setColor(ChatColor.GOLD);
        cp = manager.registerGuiCommand( (p,a) -> {
            if (p.hasPermission(PERMISSION_COMMAND)) {
                _instance.get().serialize();
            }
        } , this.getClass() , SDIPlugin.instance);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , cp.getCommand()));
        commands.add(tc);
        commandPointers.add(cp);

    }

 */

    @Override
    public void onLateEnable() {
        this.dataFile = new File (SDIPlugin.instance.getDataFolder() , "customItems");
        if (dataFile.exists() == false)
            dataFile.mkdirs();
        File file = new File(dataFile , "default.yml");
        FileConfiguration fc = new YamlConfiguration();
        CustomItem ci = new CustomItem();
        for (ItemComponentScheme c : classNameToItemComponent.values()) {
            ItemComponent ic = null;
            ic = c.get();
            ci.allComponents.add(ic);
        }
        ci.material = Material.STONE;
        ci.category = "defaultCategory";
        ci.customDataID = 1;
        ci.nameID = "defaultName";

        ConfigApi.serialize(ci, "item" ,fc);
        ConfigApi.serialize(new ItemCategory() , "category" ,fc);
        int actions = 0;
        for (Class c : nameToAction.values()) {
            try {
                ConfigApi.serialize(c.newInstance(), "actions" + actions , fc);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        try {
            fc.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor = new WeakReference<>(SDIPlugin.instance.getManager(ChatEditorManager.class));

        //_initalizeCommands();
        categorys = new DataInventory<ItemCategory>(6 , "categorys");
        defaultCategory = new ItemCategory();
        defaultCategory.name = "void";
        defaultCategory.represent = new ItemBuilder(Material.DIRT).setName("void").get();
        reloadCategorys();
        addCategory(defaultCategory);

        getPlugin().getLogger().log(Level.INFO , "\n\n\n");
        getPlugin().getLogger().log(Level.INFO , "-=-=-=-=-=-=-=-ItemManager-=-=-=-=-=-=-=-");
        getPlugin().getLogger().log(Level.INFO , "rozpoczynam wczytywanie");
//        TODO wczytywanie !!
        loadAll();
        getPlugin().getLogger().log(Level.INFO , "wczytane komponenty : " + classNameToItemComponent.size());
        getPlugin().getLogger().log(Level.INFO , "wczytane przedmioty : " + nameIDToItem.size());
        getPlugin().getLogger().log(Level.INFO , "\n\n\n");

    }

    public void addCategory(ItemCategory ic) {
        addOrUpdateCategory(ic);
    }


    protected void setItem(CustomItem ci) {
        HashMap<Integer , CustomItem> maps = materialMap.get(ci.material);
        if (maps == null) {
            maps = new HashMap<>();
            materialMap.put(ci.material , maps);
        }
        maps.put(ci.getCustomDataID() , ci);
        nameIDToItem.put(ci.nameID , ci);
        if (ci.category == null || ci.category.isEmpty()) {
            ci.category = "void";
        }

        ItemCategory category = nameToCategory.get(ci.category);
        if (category == null) {
            //TODO co wtedy :L

        }
        else {
            List<CustomItem> items = categoryToItem.get(ci.category);
            if (items == null) {
                //TODO ???

            }
            else {
                items.add(ci);
            }
        }
        ci.enable();
    }

    public boolean addItem(CustomItem ci) {
        addOrUpdateCustomItem(ci);
        return true;
    }

    public CustomItem getCustomItem(String nameID) {
        return nameIDToItem.get(nameID);
    }

    protected void doIfExistsCItem(ItemStack is , Consumer<CustomItem> cons) {
        if (is == null)
            return;
        CustomItem ci = getCustomItem(is);
        if (ci != null)
            cons.accept(ci);
    }

    public CustomItem getCustomItem(Material mat , int modelID) {
        HashMap<Integer , CustomItem> map = materialMap.get(mat);
        if (map == null)
            return null;
        return map.get(modelID);
    }

    public void registerItemComponent(Class clazz , String typeName) {
        ItemComponentScheme s = new ItemComponentScheme(clazz);
        classNameToItemComponent.put(clazz.getName() , s);
        ConfigApi.register(clazz , new ObjectCfgSerializer(clazz , typeName));
    }

    public CustomItem getCustomItem(ItemStack is , ItemMeta im) {
        if (im.hasCustomModelData() == false)
            return null;
        int i = im.getCustomModelData();
        return getCustomItem(is.getType() , i);
    }

    public CustomItem getCustomItem(ItemStack is) {
        if (is.hasItemMeta() == false)
            return null;
        return getCustomItem(is , is.getItemMeta());
    }



    public void serialize() {
        List<CustomItem> items = new ArrayList<>(nameIDToItem.values());
        JsonObject o = new JsonObject();

        JsonArray array = new JsonArray();
        for (CustomItem i : items) {
            array.add(GsonManager.getInstance().toJson(i));
        }
        o.add("items" , array);
        array = new JsonArray();
        for (ItemCategory c : nameToCategory.values())  {
            array.add(GsonManager.getInstance().toJson(c));
        }
        o.add("categorys" , array);

        File f = new File(dataFile , "save.sev");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(GsonManager.getInstance().toJson(o).toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadAll() {
        File f = new File(dataFile , "save.sev");
        if (f.exists() == false)
            return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(c -> sb.append(c));
            JsonObject o = new JsonParser().parse(sb.toString()).getAsJsonObject();
            JsonArray array = o.get("categorys").getAsJsonArray();
            array.forEach( j -> {
                addCategory((ItemCategory) GsonManager.getInstance().fromJson(j.getAsJsonObject()));
            });
            array = o.get("items").getAsJsonArray();
            array.forEach(c -> addItem((CustomItem) GsonManager.getInstance().fromJson(c.getAsJsonObject())));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(FileConfiguration fc , Ref<Integer> items , Ref<Integer> categorys) {
        ConfigurationSection section;
        int i = 0;
        Object object;
        CustomItem ci;
        for (String path : fc.getKeys(false)) {
            if (! fc.isConfigurationSection(path))
                continue;
            section = fc.getConfigurationSection(path);
            if (section.contains(ConfigApi.MARKER)) {
                object = ConfigApi.deserialize(path , fc);

                if (object instanceof CustomItem) {
                    items.setValue(items.getValue()+1);
                    addItem((CustomItem) object);
                }
                else if (object instanceof  ItemCategory) {
                    categorys.setValue(categorys.getValue() + 1);
                    addCategory((ItemCategory) object);
                }
                else {
//                    TODO
                }
            }
        }
    }
    public ItemCategory getCategory(String nameID) {
        return nameToCategory.get(nameID);
    }





    public boolean removeCustomItem(String nameid) {
        CustomItem item = getCustomItem(nameid);
        if (item == null)
            return false;
        HashMap<Integer , CustomItem> map = materialMap.get(item.material);
        if (map != null) {
            map.remove(item.getCustomDataID());
        }
        nameIDToItem.remove(item.nameID);
        removeCustomItemFromCategory(item.category , item.nameID);
        return true;
    }

    protected void removeCustomItemFromCategorys(String nameID) {
        for (String c : nameToCategory.keySet() ) {
            removeCustomItemFromCategory(c , nameID);
        }
    }

    protected void removeCustomItemFromCategory(String category ,String nameID) {
        List<CustomItem> categoryItems = categoryToItem.get(category);
        if (categoryItems != null) {
            for (int i = 0 ; i < categoryItems.size() ; i++) {
                if (categoryItems.get(i).nameID.contentEquals(nameID)) {
                    categoryItems.remove(i);
                    break;
                }
            }
        }
        DataInventory<CustomItem> inv = categoryToInventory.get(category);
        CustomItem in;
        if (inv != null) {
            for (int i = 0 ; i < inv.getInventory().getSize() ; i++) {
                in = inv.getData(i);
                if (in != null && in.nameID.contentEquals(nameID)) {
                    inv.set(i , null , null);
                    inv.setData(i , null);
                    break;
                }
            }
        }
    }

    /*

    public ChatCommandEditor getEditor(Player p) {
        return this.editor.get().getEditor(p);
    }


    public boolean addItemCategoryEditor(ItemCategory category , Player p) {
//        TODO
        if (this.editor.get().getEditor(p) != null)
            return false;

        ChatCommandEditor editor = this.editor.get().createChatCommandEditor(p , category);
        WeakReference<ItemCategory> _category = new WeakReference<>(category);
        WeakReference<ItemManager> _instance = new WeakReference<>(this);
        editor.addCommand("commit" , a -> {
            _instance.get().addCategory(_category.get());
            ((Player)a).sendMessage(new String[]{"" , "" , "" });
            _instance.get().editor.get().removeEditor((Player) a);
            _instance.get().sendCommands(((Player)a));
        });
        editor.addCommand("exit" , c-> {
            ItemManager manager = SDIPlugin.instance.getManager(ItemManager.class);
            ((Player)c).sendMessage("wywalono :D");
            ((Player)c).sendMessage(new String[]{"" , "" , "" });
            _instance.get().sendCommands((Player)c);
            _instance.get().editor.get().removeEditor((Player) c);

        });
        categoryEditors.add(editor);
        this.editor.get().enable(editor , true);

        return true;
    }


    public boolean editItem(CustomItem item, Player p , boolean edit) {
        if (item == null)
            return false;
        ChatCommandEditor editor = this.editor.get().createChatCommandEditor(p , item);
        editor.editMode = true;

        WeakReference<ItemManager> _instance = new WeakReference<>(this);
        WeakReference<CustomItem> _item = new WeakReference<>(item);
        editor.addCommand("saveChanges" , c -> {
            Player _p = (Player) c;
            ItemCategory category = _instance.get().getCategory(_item.get().category);
            if (category == null) {
                _p.sendMessage("nie znaleziono kategori !!!");
                return;
            }
            removeCustomItemFromCategorys(_item.get().category);
            _instance.get().addItem(_item.get());
            _p.sendMessage("udalo sie edytowac itemek");
            _instance.get().editor.get().removeEditor(_p);
            _p.sendMessage(new String[]{"" , "" , "" });
            _instance.get().sendCommands(_p);
        });
        editor.addCommand("exit" , c-> {
            Player _p = (Player) c;
            ItemManager manager = _instance.get();
            _instance.get().editor.get().removeEditor((Player) c);
            _p.sendMessage("wywalono :D");
            _p.sendMessage(new String[]{"" , "" , "" });
            manager.sendCommands(_p);

        });
        this.editor.get().enable(editor , false);
        return true;
    }

    public boolean addCustomItemEditor(Player p) {
        if (this.editor.get().getEditor(p) != null)
            return false;
        CustomItem  item = new CustomItem();
        ChatCommandEditor<CustomItem> editor = this.editor.get().createChatCommandEditor(p , item);
        WeakReference<ChatCommandEditor> _editorInstance = new WeakReference<>(editor);
        editor.addCommand("giveItem" , c-> {
            Player _p = (Player) c;
            ItemStack is = ((CustomItem) _editorInstance.get().mainObject).createItem(1);
            _p.getInventory().setItemInMainHand(is);
        });

        editor.addCommand("addItem" , c-> {
            Player _p = (Player) c;
            ItemManager manager = SDIPlugin.instance.getManager(ItemManager.class);
            CustomItem ci = ((CustomItem) _editorInstance.get().mainObject);
            ItemCategory category = manager.getCategory(ci.category);
            if (category == null) {
                _p.sendMessage("nie znaleziono kategori !!!");
                return;
            }
            if (ci.getCustomDataID() <= 0 )  {
                _p.sendMessage("niepoprawny modelID!! za maly");
                return;
            }
            else if (manager.getCustomItem(ci.material , ci.getCustomDataID()) != null) {
                _p.sendMessage("przemdiot z tym modelid juz istnieje !");
                return;
            }
            else if (manager.getCustomItem(ci.nameID) != null) {
                _p.sendMessage("przedmiot z ta nazwa juz istnieje !");
                return;
            }
            manager.addItem(ci);
            _p.sendMessage("udalo sie dodac itemek");
            _p.sendMessage(new String[]{"" , "" , "" });
            manager.sendCommands(_p);
            manager.editor.get().removeEditor(_p);
        });
        editor.addCommand("exit" , c-> {
            Player _p = (Player) c;
            ItemManager manager = SDIPlugin.instance.getManager(ItemManager.class);
            manager.editor.get().removeEditor(_p);
            _p.sendMessage("wywalono :D");
            _p.sendMessage(new String[]{"" , "" , "" });
            manager.sendCommands(_p);

        });

        this.editor.get().enable(editor , false);
        return true;
    }



     */

    public void reloadCategorys() {
        categorys = new DataInventory<>(6 , "przedmioty");
        categoryToInventory = new HashMap<>();
//        TODO
    }


    protected boolean addOrUpdateCustomItem(CustomItem item) {
        System.out.println("components " + item.allComponents.size());
        if (item.category == null || item.category.isEmpty())
            item.category = defaultCategory.name;

        CustomItem inside = getCustomItem(item.nameID);
        DataInventory<CustomItem> inv = categoryToInventory.get(item.category);
        int position = 0;
        if (inside != null) {
            if (inside.getCustomDataID() != item.getCustomDataID()) {
                return false;
            }
            inside = getCustomItem(item.material , item.customDataID);
            if (inside.nameID.contentEquals(item.nameID) == false) {
                return false;
            }
            if (inside.category.contentEquals(item.category) == false ) {
//                TODO usunac itema z innego inventory
            }

            position = Integer.MAX_VALUE;
            for (int i = 0 ; i < inv.getInventory().getSize() ; i++) {
                CustomItem ci = inv.getData(i);
                if (ci == null ) {
                    if (i < position) {
                        position = i;
                    }
                }
                else if (ci.nameID.contentEquals(item.nameID)) {
                    position = i;
                    break;
                }
            }
        }
        else if (getCustomItem(item.material , item.getCustomDataID()) == null) {
            position = Integer.MAX_VALUE;
            for (int i = 0 ; i < inv.getInventory().getSize() ; i++) {
                CustomItem ci = inv.getData(i);
                if (ci == null) {
                    if (i < position)
                        position = i;
                }
            }
        }
        else {
            return false;
        }
        HashMap<Integer , CustomItem> map = materialMap.get(item.material);
        if (map == null) {
            map = new HashMap<>();
            materialMap.put(item.material , map);
        }
        map.put(item.getCustomDataID() , item);
        nameIDToItem.put(item.nameID , item);
        item.enable();

        List<CustomItem> items = categoryToItem.get(item.category);
        if (items == null) {
            items = new ArrayList<>();
            categoryToItem.put(item.category , items);
        }
        items.add(item);

        WeakReference<CustomItem> _item = new WeakReference<>(item);
        WeakReference<DataInventory> _inventory = new WeakReference<>(inv);
        WeakReference<ItemManager> _instance = new WeakReference<>(this);
        inv.set(position , item.createItem(1) , s -> {
            s.setCancelled(true);
            s.setResult(Event.Result.DENY);
            if (_inventory.get().isHolderInventory(s.getClickedInventory())) {
                if (s.getClick() == ClickType.SHIFT_LEFT || s.getClick() == ClickType.SHIFT_RIGHT) {
                    TextComponent tc = new TextComponent("item nameid: ");
                    TextComponent in = new TextComponent(_item.get().nameID);
                    in.setColor(ChatColor.GREEN);
                    tc.addExtra(in);
                    tc.addExtra(" modelid ");
                    in = new TextComponent(_item.get().customDataID+"");
                    in.setColor(ChatColor.GREEN);
                    tc.addExtra(in);
                    tc.addExtra(" ");
                    in = new TextComponent("item");
                    in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM , SpigotUtils.fromItemStack(_item.get().createItem(1))));
                    tc.addExtra(in);
                    tc.addExtra(" ");

                    in = new TextComponent("[edit]");
                    in.setColor(ChatColor.GREEN);
                    in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,pointerEditItem.getCommand() + " " + _item.get().nameID));
                    tc.addExtra(in);

                    tc.addExtra(" [remove");
                    in = new TextComponent("*");
                    in.setColor(ChatColor.RED);
                    in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,pointerRemoveItem.getCommand() + " " + _item.get().nameID));
                    tc.addExtra(in);
                    tc.addExtra("]");
                    s.getWhoClicked().spigot().sendMessage(tc);

                }
                else
                    s.getWhoClicked().getInventory().addItem(_item.get().createItem(_item.get().material.getMaxStackSize()));
            }
        });
        inv.setData(position , item);
        return true;
    }

    protected void addOrUpdateCategory(ItemCategory category ) {
        int empty = Integer.MAX_VALUE;
        ItemCategory registeredCategory = null;
        for (int i = 0 ; i < categorys.getInventory().getSize() ; i++) {
            if (categorys.getData(i) == null) {
                if (i < empty) {
                    empty = i;
                }
            }
            else if (categorys.getData(i).name.contentEquals(category.name)) {
                registeredCategory = categorys.getData(i);
                empty = i;
                break;
            }
        }
        nameToCategory.put(category.name , category);
        DataInventory<CustomItem> itemsCategory = new DataInventory<>(6 , category.name + " items:");
        WeakReference<DataInventory<CustomItem>> _category = new WeakReference<>(itemsCategory);
        categoryToInventory.put(category.name , itemsCategory);

        itemsCategory.defaultSlot = s-> {
            s.setResult(Event.Result.DENY);
            s.setCancelled(true);
        };

        WeakReference<DataInventory> _instance = new WeakReference<>(categorys);
        categorys.set(empty , category.represent , s-> {
            _instance.get().cancelEvent(s);
            if (_instance.get().isHolderInventory(s.getClickedInventory())) {
                s.getWhoClicked().openInventory(_category.get().getInventory());
            }
        });
        categorys.setData(empty , category);
    }

    public void sendCommands(Player p) {
        for (TextComponent tc : commands) {
            p.spigot().sendMessage(tc);
        }
    }

    @Override
    public boolean onCommand(CommandSender send, Command c, String s, String[] strings) {
        if (send instanceof Player) {
            Player p = (Player) send;
            sendCommands(p);
        }
        return false;
    }



    public void passEvent(ItemStack is , Event e) {
        if (is == null)
            return;
        doIfExistsCItem(is , c -> {
            c.execute(e);
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.HAND) {
            passEvent(e.getPlayer().getInventory().getItemInMainHand() , e);
        }
    }




    @EventHandler
    public void onItemInitialize(EventCreateItem e) {
        passEvent(e.item , e);
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent e) {
        passEvent(e.getItemInHand() , e);
    }

    @EventHandler
    public void onItemSwapEvent(PlayerSwapHandItemsEvent e) {
        passEvent(e.getMainHandItem() , e);
        passEvent(e.getOffHandItem(), e);
    }

    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent e) {
        passEvent(e.getPlayer().getInventory().getItem(e.getPreviousSlot()) , e);
        passEvent(e.getPlayer().getInventory().getItem(e.getNewSlot()) , e);
    }

    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent e) {
        passEvent(e.getItemDrop().getItemStack() , e);
    }


}
