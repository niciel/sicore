package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.inGameEditor.ChatCommandEditor;
import com.niciel.superduperitems.inGameEditor.ChatEditorMenu;
import com.niciel.superduperitems.inGameEditor.IBaseObjectEditor;
import com.niciel.superduperitems.inGameEditor.editors.EditorChatInt;
import com.niciel.superduperitems.inGameEditor.editors.EditorChatString;
import com.niciel.superduperitems.inGameEditor.editors.EnumEditor;
import com.niciel.superduperitems.utils.IRefSilent;
import com.niciel.superduperitems.utils.Ref;
import com.niciel.superduperitems.utils.RefCallBack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackEditor extends ChatEditorMenu<ItemStack> {


    public ItemStackEditor(IBaseObjectEditor owner, String name, String description) {
        super(owner, name, description);
    }


    private ItemStack is;
    private ItemMeta im;

    private EditorChatString displayName;
    private EditorChatInt count;
    private EnumEditor enumEditor;


    @Override
    public void sendMenu() {
        Player p = getTreeRoot().getPlayer();
        p.sendMessage("zmiana typu usunie wszystkie zmiany w przedmiocie dotyczace itemmeta !");
        enumEditor.sendItem(p);
        p.sendMessage("zmiana typu usunie wszystkie zmiany w przedmiocie dotyczace itemmeta !");
        if (is != null) {
            count.sendItem(p);
            displayName.sendItem(p);
        }
    }

    @Override
    public void initialize(Ref<ItemStack> reference) {
        super.initialize(reference);
        if (reference.getValue() == null) {
            ((IRefSilent) reference).setSilently(new ItemStack(Material.CHEST));
        }
        is = reference.getValue();
        im = is.getItemMeta();
    }

    @Override
    public void onSelect(ChatEditorMenu menu) {
        displayName = new EditorChatString("name","displaedNameOfItem");
        RefCallBack<String> displaynameCallBack = new RefCallBack<>(im.getDisplayName());
        displaynameCallBack.addCallBack(r-> {
            im.setDisplayName(r.getValue());
            is.setItemMeta(im);
        });
        displayName.initialize(displaynameCallBack);
        displayName.enableEditor(menu);

        count = new EditorChatInt("count" , " ilosc");
        RefCallBack<Integer> amountCallback = new RefCallBack<>(is.getAmount());
        amountCallback.addCallBack(r-> {
            is.setAmount(r.getValue());
        });
        count.initialize(amountCallback);
        count.enableEditor(menu);

        enumEditor = new EnumEditor(Material.class ,"material" , "none");
        RefCallBack<Material> refM;
        if (is != null)
            refM = new RefCallBack<>(is.getType());
        else
            refM = new RefCallBack<>(Material.STONE);
        refM.addCallBack(r ->{
            ItemStack was = is;
            is = new ItemStack(r.getValue());
            im = is.getItemMeta();
            if (was != null) {
                ItemMeta wasIm = was.getItemMeta();
                is.setAmount(was.getAmount());
                if (wasIm.hasLore())
                    im.setLore(wasIm.getLore());
                if (wasIm.hasDisplayName())
                    im.setDisplayName(wasIm.getDisplayName());
            }
            is.setItemMeta(im);
            getReference().setValue(is);
        });
        enumEditor.initialize((Ref) refM);
        enumEditor.enableEditor(this);

    }

    @Override
    public void onDeselect() {
        displayName.disableEditor();
        count.disableEditor();
        enumEditor.disableEditor();
    }




    @Override
    public void sendItem(Player p) {
        TextComponent tc;
        TextComponent in ;
        tc = new TextComponent("type: " + is.getType().toString().toLowerCase() + " item: " );

        if (is != null) {
            tc.addExtra(is.getType().toString().toLowerCase() + " item: ");
            if (im.hasDisplayName()) {
                in = new TextComponent(im.getDisplayName());
            }
            else {
                in = new TextComponent(is.getType().toString().toLowerCase());
            }
            in.setColor(ChatColor.BLUE);
            if (im.hasLore()) {
                StringBuilder sb = new StringBuilder();
                im.getLore().stream().forEach(s-> sb.append(s).append("\n"));
                in.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT , new Text(sb.toString())));
            }
            tc.addExtra(in);
        }
        else {
            tc.addExtra("brak! ");
        }
        tc.addExtra(",   ");
        in = new TextComponent("[edit]");
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , this.selectEditorCommand));
        tc.addExtra(in);
        p.spigot().sendMessage(tc);

    }

    @Override
    public void disableEditor() {

    }
}
