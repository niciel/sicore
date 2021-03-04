package com.niciel.superduperitems.cfg;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public interface FieldSerializer<T> {



    void serialize(T o, ConfigurationSection section);


    T deserialize(ConfigurationSection section);

    String type();



}
