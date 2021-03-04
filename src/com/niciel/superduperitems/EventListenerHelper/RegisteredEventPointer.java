package com.niciel.superduperitems.EventListenerHelper;

import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.WeakReference;

public abstract class RegisteredEventPointer<T> {


    private CustomRegisteredListener listener;

    public <T> RegisteredEventPointer(Class<T> type , JavaPlugin plugin , EventPriority priority ,boolean ignoreCanceled) {
        WeakReference<RegisteredEventPointer> consumer = new WeakReference<>(this);

        this.listener = new CustomRegisteredListener( plugin ,priority , ignoreCanceled,type , c->  {
            if (consumer.get() == null || consumer.isEnqueued()) {
                return;
            }
            consumer.get().onEvent((T) c);
        });
    }

    public abstract void onEvent(T t) ;

    public void setSuspended(boolean suspended) {
        this.listener.setSuspended(suspended);;
    }

    public boolean isSuspended() {
        return this.listener.isSuspended();
    }



    public void remove() {
        this.listener.remove();
    }

    public void register() {
        this.listener.register();
    }


    @Override
    protected void finalize() throws Throwable {
        listener.remove();
        super.finalize();
    }
}
