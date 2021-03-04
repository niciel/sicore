package com.niciel.superduperitems.inGameEditor.editors;

import com.niciel.superduperitems.SDIPlugin;

import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.commandGui.GuiCommandManager;
import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.IChatEditorMenu;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.SpigotUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EditorChatItemStack extends IChatEditorMenu<ItemStack> {


    private static GuiCommandManager manager = SDIPlugin.instance.getManager(GuiCommandManager.class);

    ItemStack item;
    ItemMeta itemMeta;

    Ref<ItemStack> ref;
    WeakReference<ChatCommandEditor> editor;
    CommandPointer select;
    CommandPointer sendEdit;
    CommandPointer addName;
    CommandPointer reloadItem ;
    EditorChatList loreEditor;
    CommandPointer backToEditor;
    List<String> lore;

    private static Field loreField;

    static {
        try {
            loreField = EditorChatItemStack.class.getDeclaredField("lore");
            loreField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }



    public void createItem(Material mat) {
        item = new ItemStack(mat);
        itemMeta = SDIPlugin.instance.getServer().getItemFactory().asMetaFor(itemMeta , item);
        ref.setValue(getItem());
    }


    CommandPointer createItem;




    public ItemStack getItem() {
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }



    @Override
    public void sendMenu(Player p) {
        TextComponent tc ;
        TextComponent in ;
        p.sendMessage(new String[]{"" ,"" ,""});
        if (item != null) {
            tc = new TextComponent("przedmiot: ");
            in = new TextComponent("[" +item.getType().name().toLowerCase() +"]");
            in.setColor(ChatColor.BLUE);
            in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM , SpigotUtils.fromItemStack(getItem())));
            tc.addExtra(in);

            tc.addExtra(" ");
            in = new TextComponent("[zmien]");
            tc.addExtra(" ");
            in.setColor(ChatColor.GOLD);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , createItem.getCommand()+ " "));
            tc.addExtra(in);

            if (itemMeta.hasCustomModelData())
                tc.addExtra(" model: " + itemMeta.getCustomModelData());
            p.spigot().sendMessage(tc);

            tc = new TextComponent();
            in = new TextComponent("[nazwa]");
            in.setColor(ChatColor.GREEN);
            in.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND , addName.getCommand() + " "));
            tc.addExtra(in);
            tc.addExtra(" " + itemMeta.getDisplayName());
            p.spigot().sendMessage(tc);

            loreEditor.sendItem(p);
        }


        tc = new TextComponent("[reload]");
        tc.setColor(ChatColor.GREEN);
        tc.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND , reloadItem.getCommand()));
        p.spigot().sendMessage(tc);

    }

    @Override
    public void onSelect(ChatCommandEditor editor) {

    }

    @Override
    public void onDeselect(ChatCommandEditor editor) {

    }

    @Override
    public void enable(WeakReference<ChatCommandEditor> editor, String name, String description, Class type, Ref<ItemStack> refToObject) {
        this.editor = editor;
        this.ref = refToObject;
        this.lore = new ArrayList<>();

        if (refToObject.getValue() == null) {
            refToObject.setValue(new ItemStack(Material.WOODEN_AXE));
        }
        item = refToObject.getValue();
        itemMeta = item.getItemMeta();

        WeakReference<EditorChatItemStack> _instance = new WeakReference<>(this);


        select = manager.registerGuiCommand((p,a) -> {
            editor.get().select(_instance.get());
        } ,this.getClass() , SDIPlugin.instance);

        addName = manager.registerGuiCommand( (p,a) -> {
            String display = ChatColor.translateAlternateColorCodes('&' , a);
            _instance.get().itemMeta.setDisplayName(display);
            editor.get().send();
        } , this.getClass() , SDIPlugin.instance);


        reloadItem = manager.registerGuiCommand( (p , a) -> {
            editor.get().send();
        }, this.getClass() , SDIPlugin.instance);

        Ref<List> refLore = new Ref(lore);
        loreEditor = (EditorChatList) ChatCommandEditor.getEditor(List.class);

//        TODO
        loreEditor.enable(editor, "lore" , "", String.class , refLore);



        createItem = manager.registerGuiCommand( (p,a) -> {
            Material m = Material.matchMaterial(a.toUpperCase());
            if (m != null) {
                _instance.get().createItem(m);
                _instance.get().sendMenu(p);
            }
            else {
                _instance.get().sendMenu(p);
                p.sendMessage("niepoprawny material");
            }
        } , this.getClass() , SDIPlugin.instance);

        manager.registerGuiHelper(createItem , (p,a) -> {
            if (a.length == 1) {
                return SpigotUtils.findClosest(a[0] , 10);
            }
            return null;
        });



        backToEditor = manager.registerGuiCommand( (p,a) -> {
            _instance.get().editor.get().send();
        },this.getClass() , SDIPlugin.instance);

        sendEdit = manager.registerGuiCommand( (p,a) -> {
            _instance.get().editor.get().select(_instance.get());
        },this.getClass() , SDIPlugin.instance);
        createItem(refToObject.getValue().getType());
//        TODO
    }

    @Override
    public void sendItem(Player p) {
        TextComponent tc = new TextComponent("ItemStack ");
        TextComponent in = new TextComponent("[edytuj]");
        in.setColor(ChatColor.GREEN);
        if (item != null)
            in.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_ITEM , SpigotUtils.fromItemStack(getItem())));
        else
            in.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT , new ComponentBuilder("null").create()));
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , sendEdit.getCommand()));
        tc.addExtra(in);
        p.spigot().sendMessage(tc);
    }
}
