package com.niciel.superduperitems.cfg;

import org.bukkit.configuration.ConfigurationSection;

public class PrimitiveSerializer  implements FieldSerializer {


    @Override
    public String type() {
        return null;
    }

    @Override
    public void serialize(Object obj, ConfigurationSection section) {

    }

    @Override
    public Object deserialize(ConfigurationSection section) {
        return null;
    }
}
