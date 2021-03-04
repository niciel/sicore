package com.niciel.superduperitems.cfg;


import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ListBoxSerialization implements FieldSerializer<List> {


    @Override
    public void serialize(List o, ConfigurationSection section) {
       // ConfigurationSection innerSection ;
        for (int i = 0 ; i < o.size() ; i++) {
         //   innerSection = section.createSection(String.valueOf(i));
            ConfigApi.serialize(o.get(i) , String.valueOf(i) , section);
        }
    }

    @Override
    public List deserialize(ConfigurationSection section) {
        List<Object> list = new ArrayList<>();
        String path;
        for (int i = 0 ; i < section.getKeys(false).size() ; i++) {
            path = String.valueOf(i);
            if (section.contains(path)) {
                list.add(ConfigApi.deserialize(path , section));
                continue;
            }
            break;
        }
        return list;
    }

    @Override
    public String type() {
        return "list";
    }
}
