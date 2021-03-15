package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;

public class TestEditor {

    @ChatEditable(name = "jakas tam nazwa")
    private int intTest = 1;
    @ChatEditable
    private double doubleTest = 1.0;
    @ChatEditable
    private float floatTest = 1.0f;
    @ChatEditable
    private String stringTest = "string test";

    @ChatEditable
    private TestInner objectTest = new TestInner();

}
