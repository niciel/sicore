package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonElement;
import com.niciel.superduperitems.utils.Ref;

public interface GsonSimpleSerializer<T> {
    JsonElement serialize(T o);
    void deserialize(Ref<T> ref , JsonElement object);
}
