package com.niciel.superduperitems.customitems.editor;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.customitems.CustomItem;
import com.niciel.superduperitems.customitems.ItemComponent;
import com.niciel.superduperitems.customitems.ItemManager;
import com.niciel.superduperitems.inventory.CustomInventory;
import com.niciel.superduperitems.inventory.Slot;
import com.niciel.superduperitems.utils.itemstack.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomItemEditor extends CustomInventory {


    private static ItemManager manager = SDIPlugin.instance.getManager(ItemManager.class);

    CustomItem customItem;
    public ItemStack is;
    public ItemMeta im;


    ItemComponent component;


    public CustomItemEditor(Material material) {
        super(6, "edytor");
        is = new ItemStack(material);
        im = is.getItemMeta();
        customItem = new CustomItem();
        customItem.material = material;
        set(4 , is);


        //zmiana edytuj usun komponent
        set(9, edytuj, new Slot() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if (remove == true)
                {
                    remove = false;
                    set(9 , edytuj);
                }
                else {
                    remove = true;
                    set(9 , removeItem);
                }
            }
        });
    }

    public List<String> getInfo() {
        List<String> list = new ArrayList<>();

        list.add("modelID: " + customItem.getCustomDataID());
        list.add("liczba komponentow: " +list.size());
        return list;
    }
    public ItemStack removeItem = new ItemBuilder(Material.BARRIER).setName("usun").get();
    public ItemStack edytuj = new ItemBuilder(Material.GOLDEN_AXE).setName("edytuj").get();

    public boolean remove = false;

    public void performedAction_Component(int slot) {
        if (remove) {
            customItem.allComponents.remove(slot);
        }
        else {
            component = customItem.allComponents.get(slot);
        }
    }

    public void initComponents() {
        int slot;
        for (int i = 0 ; i < customItem.allComponents.size() ; i++) {
            slot = 18+i;
            int finalI = i;
            set(slot , new ItemBuilder(Material.STONE_AXE).get() , (e) -> {
                performedAction_Component(finalI);
            } );
        }
    }

    public void sendEditFunction() {
        for (Field f : component.getClass().getDeclaredFields()) {

        }
    }


}
