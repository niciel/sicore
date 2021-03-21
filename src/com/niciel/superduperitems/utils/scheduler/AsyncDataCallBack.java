package com.niciel.superduperitems.utils.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncDataCallBack<T> extends AsyncCallBack<AsyncDataCallBack<T>> {


    private T computeData;

    public AsyncDataCallBack(JavaPlugin owner, Function<AsyncDataCallBack<T>, Boolean> async, Consumer<AsyncDataCallBack<T>> sync , T data) {
        super(owner, async, sync);
        this.computeData = data;
    }

    public T getData() {
        return computeData;
    }

    public void setData(T d) {
        this.computeData =d;
    }


    @Override
    public void run() {
        super.success = (super.runAsync.apply(this));
        super.asyncEnds = true;
        Bukkit.getScheduler().runTask(getOwner() , ()-> super.runSync.accept(this));
    }

    public static <T> AsyncDataCallBack<T> createAndRun(JavaPlugin plugin , Function<AsyncDataCallBack<T>, Boolean> async,Consumer<AsyncDataCallBack<T>> sync , T data) {
        AsyncDataCallBack<T> s = new AsyncDataCallBack<T>(plugin , async , sync , data);
        s.execute();
        return s;
    }

}
