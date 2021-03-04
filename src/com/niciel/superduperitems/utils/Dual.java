package com.niciel.superduperitems.utils;

public class Dual<T,K> {


    public Dual(T first , K second) {
        this.first = first;
        this.second = second;
    }

    public Dual() {
        first = null;
        second= null;
    }

    public T first;
    public K second;


}
