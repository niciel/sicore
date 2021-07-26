package com.niciel.superduperitems.inGameEditor.fieldwrapper;

import com.niciel.superduperitems.commandGui.IGuiTabCompliter;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.IObjectSelfEditable;
import com.niciel.superduperitems.inGameEditor.editors.EditorChatString;
import com.niciel.superduperitems.inGameEditor.editors.object.EditorChatObject;
import com.niciel.superduperitems.utils.RefCallBack;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FieldWorldWrapper implements IObjectSelfEditable {


    @GsonSimpleSerialize
    private String worldName;

    private EditorChatString stringEditor;
    private RefCallBack<String> ref;

    public World get() {
        return Bukkit.getWorld(worldName);
    }

    @Override
    public void onEnableEditor(EditorChatObject editor) {
        System.out.println("enabled");
        stringEditor = new EditorChatString("worldname" , "nazwaświata" );
        ref = new RefCallBack<>(worldName);
        ref.addCallBack(c-> {
            this.worldName = c.getValue();
            editor.getTreeRoot().sendMenu();
        });
        stringEditor.initialize(ref);
        stringEditor.enableEditor(editor);
        editor.getTreeRoot().commands().register(stringEditor.getCommandPointer(), new IGuiTabCompliter() {
            @Override
            public List<String> onTabComplite(Player sender, String[] args, int deep) {
                if (args.length >= deep) {
                    List<String> l = new ArrayList<>();
                    Bukkit.getServer().getWorlds().forEach( w-> l.add(w.getName()));
                    return l;
                }
                return null;
            }
        });
    }

    @Override
    public void onDisableEditor(EditorChatObject editor) {
        System.out.println("disable");
        stringEditor.disableEditor();
        stringEditor = null;
        ref = null;
    }

    @Override
    public void onSendItemMenu(Player p) {
        stringEditor.sendItem(p);
    }

}
