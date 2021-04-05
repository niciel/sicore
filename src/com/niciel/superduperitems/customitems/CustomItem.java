package com.niciel.superduperitems.customitems;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.cfg.Cfg;
import com.niciel.superduperitems.customitems.event.EventCreateItem;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItem  {

    protected ItemManager manager  = SDIPlugin.instance.getManager(ItemManager.class);


    @ChatEditable(name = "nameID")
    @Cfg(path =  "nameID")
    public String nameID;
    @ChatEditable(name = "customDataID")
    @Cfg(path = "customDataID")
    public int customDataID = -1;
    @ChatEditable(name = "category")
    @Cfg(path = "category")
    public String category = "void";
    @ChatEditable(name = "material")
    @Cfg(path = "material")
    public Material material = Material.STONE;
    @ChatEditable(name = "components")
    @Cfg(path =  "components")
    public List<ItemComponent> allComponents;





    private Map<String , List<ItemComponentBox>> eventMapToListOfBoxes;

    public CustomItem () {
        allComponents = new ArrayList<>();
        eventMapToListOfBoxes = new HashMap<>();
    }

    protected void registerComponentEvents(ItemComponent component) {
        List<ItemComponentBox> list;
        for (Map.Entry<String , MethodHandle> e : manager.getScheme(component).eventNameToMethod.entrySet()) {
            list = eventMapToListOfBoxes.get(e.getKey());
            if (list == null) {
                list = new ArrayList<>();
                eventMapToListOfBoxes.put(e.getKey() , list);
            }
            list.add(new ItemComponentBox(component , e.getValue()));
        }
    }

    public <T extends ItemComponent> T getComponent(Class<T> clazz) {
        for (ItemComponent c : allComponents) {
            if (c.getClass().getName().contentEquals(clazz.getName()))
                return (T) c;
        }
        return null;
    }

    public int getCustomDataID() {
        return customDataID;
    }

    public void removeComponent(Class<ItemComponent> clazz) {
        String name = clazz.getName();
        remove(name , allComponents);
        for (List l : eventMapToListOfBoxes.values()) {
            remove(name , l);
        }
    }

    protected void remove(String clazz , List<ItemComponent> list) {
        for (int i = 0 ; i < list.size() ; i++) {
            if (list.get(i).getClass().getName().contentEquals(clazz)) {
                list.remove(i);
                return;
            }
        }
    }

    public void enable() {
        eventMapToListOfBoxes = new HashMap<>();
        allComponents.forEach( c-> c.onEnable(this));
        for (ItemComponent ic : allComponents) {
            registerComponentEvents(ic);
        }
    }

    public ItemStack createItem(int quantity) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(getCustomDataID());
        is.setItemMeta(im);

        EventCreateItem e = new EventCreateItem(is , im);
        Bukkit.getPluginManager().callEvent(e);
        is.setItemMeta(im);
        return is;
    }

    public void execute(Event e) {
        List<ItemComponentBox> list = eventMapToListOfBoxes.get(e.getClass().getName());
        if (list == null)
            return;
        try {
            for (ItemComponentBox c : list)  {
                c.handle.invoke(c.component , e);
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            // wtf zjebane cos wczesniej
        }
    }

}
