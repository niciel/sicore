package com.niciel.superduperitems.persistentHelper;

import com.niciel.superduperitems.SDIPlugin;
import com.niciel.superduperitems.utils.Ref;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataHelper<T> extends ObjectSerializer<T> implements PersistentDataType<byte[] , T> {

    public static PersistentSerializerManager serializer = SDIPlugin.instance.getManager(PersistentSerializerManager.class);

    public NamespacedKey key;

    public PersistentDataHelper(Class<T> clazz , String key) {
        super(clazz, serializer);

        this.key = new NamespacedKey(SDIPlugin.instance , key);
    }

    @Nullable
    public T get(PersistentDataContainer container ) {
        if (container.has(key , this))
            return container.get(key , this);
        return null;
    }


    public void set(PersistentDataContainer container , T data ) {
        container.set(key , this , data);
    }



    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<T> getComplexType() {
        return getSerializedClass();
    }

    @Override
    public byte[] toPrimitive(T t, PersistentDataAdapterContext persistentDataAdapterContext) {
        return serialize(t);
    }

    @Override
    public T fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        return deserialize(bytes , new Ref<Integer>());
    }
}
