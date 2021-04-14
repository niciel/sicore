package com.niciel.superduperitems.customitems;

import com.niciel.superduperitems.inventory.CustomInventory;

public class DataInventory<T> extends CustomInventory {

    private T[] data;

    public DataInventory(int labels, String title) {
        super(labels, title);
        data = (T[]) new Object[labels*9];
    }

    public void setData(int i , T d) {
        data[i] =d;
    }

    public T getData(int i) {
        return data[i];
    }

}
