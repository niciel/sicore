package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.Material;

public class TestExtends extends TestEditor {


    @ChatEditable(name = "annotation name")
    private String test = "null";

    @ChatEditable
    private Material material ;

}
