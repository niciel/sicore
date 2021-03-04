package com.niciel.superduperitems.cfg;

import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PotionEffectTypeSerializer implements FieldSerializer<PotionEffectType> {

    private static HashMap<String , PotionEffectType> nameToEffect ;

    static {
        File file = SDIPlugin.instance.getDataFolder();
        file = new File(file , "potionEffectTypes.yml");
        YamlConfiguration config = new YamlConfiguration();

        nameToEffect = new HashMap<>();
        List<String> effects = new ArrayList<>();
        for (Field f : PotionEffectType.class.getFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                if (PotionEffectType.class.isAssignableFrom(f.getType())) {
                    try {
                        PotionEffectType type = (PotionEffectType) f.get(null);
                        effects.add(type.getName());
                        nameToEffect.put(type.getName() , type);
                        nameToEffect.put(type.getName().toLowerCase() , type);
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        config.set("potionEffectTypes" , effects);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void serialize(PotionEffectType o, ConfigurationSection section) {
        section.set("type" , o.getName());
    }

    @Override
    public PotionEffectType deserialize(ConfigurationSection section) {
        return nameToEffect.get(section.get(section.getString("type"))) ;
    }

    @Override
    public String type() {
        return "potionEffectType";
    }
}
