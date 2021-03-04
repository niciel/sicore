package com.niciel.superduperitems.utils;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;

public class Ref<T> {


    public Ref(T val) {
        value = val;
    }

    public Ref() {}


    @ChatEditable
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }




}
