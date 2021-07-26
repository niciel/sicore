package com.niciel.superduperitems.customitems;

import com.google.common.base.Function;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemComponentScheme {

    public Map<String , MethodHandle> eventNameToMethod;
    public Class itemComponent;

    public ItemComponentScheme(Class itc) {
        itemComponent = itc;
        eventNameToMethod = new HashMap<String , MethodHandle>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class eventType;
        MethodHandle handle;
        for (Method m : itc.getDeclaredMethods()) {
            if (m.isAnnotationPresent(EventHandler.class) == false)
                continue;
            if (m.getParameterCount() != 1)
                continue;
            eventType = m.getParameterTypes()[0];
            if (Event.class.isAssignableFrom(eventType) == false)
                continue;
            try {
                handle = lookup.unreflect(m);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            eventNameToMethod.put(eventType.getName() , handle);
        }
    }


    public ItemComponent get() {
        try {
            return (ItemComponent) itemComponent.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
