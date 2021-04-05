package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.fieldwrapper.*;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import org.bukkit.util.Vector;

import java.util.List;

public class TestEditor {


    @GsonSimpleSerialize
    @ChatEditable(name = "jakas tam nazwa")
    private int intTest = 1;
    @GsonSimpleSerialize
    @ChatEditable
    private double doubleTest = 1.0;
    @GsonSimpleSerialize
    @ChatEditable
    private float floatTest = 1.0f;

    @GsonSimpleSerialize
    @ChatEditable
    private List<TestInner> list;

}
