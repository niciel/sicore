package com.niciel.superduperitems.core.zombie;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.gsonadapter.GsonSimpleSerialize;
import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;

public class ChestScheme implements GsonSerializable {


    @GsonSimpleSerialize
    @ChatEditable
    public double playerDistance;
    @GsonSimpleSerialize
    @ChatEditable
    public int respawnTime;


    @GsonSimpleSerialize
    @ChatEditable
    public String name;

    @GsonSimpleSerialize
    @ChatEditable
    public String random_ID;



}
