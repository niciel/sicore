package com.niciel.superduperitems.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RefCallBack<T> extends Ref<T> implements IRefSilent<T> {

    private List<Consumer<RefCallBack<T>>> callBack = new ArrayList<>();


    public RefCallBack(T value) {
        super(value);
    }

    public RefCallBack() {
        super();
    }


    public void addCallBack(Consumer<RefCallBack<T>> c) {
        callBack.add(c);
    }



    public void setValue(T value) {
        super.setValue(value);
        callBack.forEach(c-> c.accept(this));
    }

    @Override
    public void setSilently(T t) {
        super.setValue(t);
    }
}
