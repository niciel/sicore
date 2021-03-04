package com.niciel.superduperitems.EventListenerHelper;

import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class CustomRegisteredListener<T extends Event> extends RegisteredListener implements Listener {

    private boolean suspended = false;
    private Consumer<T> consumer;
    private UUID uuid = UUID.randomUUID();
    private HandlerList handlers;
    private boolean removed = true;


    private CustomRegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        super(listener, executor, priority, plugin, ignoreCancelled);
    }


    /**
     *
     * @param plugin owner
     * @param priority priority of event
     * @param ignoreCanceled igonring if canceled
     * @param type event type
     * @param con consumer
     */
    public CustomRegisteredListener(JavaPlugin plugin , EventPriority priority , boolean ignoreCanceled,Class<T> type, Consumer<T> con) {
        super(null , null , priority , plugin , ignoreCanceled);
        this.consumer = con;

        try {
            Method m;
            m = type.getMethod("getHandlerList");
            handlers = (HandlerList) m.invoke(null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isSuspended() {
        return suspended;
    }


    public void remove() {
        if (! removed) {
            removed = true;
            handlers.unregister((RegisteredListener) this);
        }
    }

    public void register() {
        if (!removed)
            return;
        handlers.register((RegisteredListener) this);
        removed = false;
    }

    @Override
    public void callEvent(Event event) throws EventException {
        if (! suspended)
            consumer.accept((T) event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomRegisteredListener<?> that = (CustomRegisteredListener<?>) o;
        return uuid.equals(that.uuid);
    }


    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
