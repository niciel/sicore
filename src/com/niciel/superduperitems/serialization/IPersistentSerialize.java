package com.niciel.superduperitems.serialization;

import com.niciel.superduperitems.utils.Ref;

import java.lang.ref.Reference;

public interface IPersistentSerialize<T> {


    byte[] serialize(T t);
    T deserialize(byte[] bytes, Ref<Integer> size);
}
