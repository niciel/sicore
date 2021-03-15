package com.niciel.superduperitems.persistentHelper;

import com.niciel.superduperitems.utils.Ref;

public interface IPersistentSerialize<T> {


    byte[] serialize(T t);
    T deserialize(byte[] bytes, Ref<Integer> size);
}
