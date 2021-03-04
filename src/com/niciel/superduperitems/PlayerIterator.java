package com.niciel.superduperitems;

import com.niciel.superduperitems.utils.Dual;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import java.lang.ref.WeakReference;
import java.rmi.UnexpectedException;
import java.util.*;
import java.util.function.Predicate;

public class PlayerIterator {

    private List<WeakReference<Player>> players = new ArrayList<>();

    private List<IPlayerIteratorConsumer> onAdd;
    private List<IPlayerIteratorConsumer> onRemove;



    public PlayerIterator() {
        this.onAdd = new ArrayList<>();
        this.onRemove = new ArrayList<>();
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }



    public void add(Player p) {
        Iterator<IPlayerIteratorConsumer> itr = onAdd.iterator();
        IPlayerIteratorConsumer cons ;
        Iterator<WeakReference<Player>> itrp = players.iterator();
        WeakReference<Player> weak;
        while (itrp.hasNext()) {
            weak = itrp.next();
            if (weak.isEnqueued() || weak.get() == null) {
                itrp.remove();
                continue;
            }
            if (weak.get().getName().contentEquals(p.getName())) {
                SDIPlugin.instance.logWarning(this , "BLAD nie mozna dwa razy dodac tego samego playersa");
                try {
                    throw new UnexpectedException("uneccepted");
                } catch (UnexpectedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        while (itr.hasNext()){
            cons = itr.next();
            if (! cons.validate())
                itr.remove();
            else
                cons.accept(p);
        }
        players.add(new WeakReference<>(p));
    }

    public void addListener(IPlayerIteratorConsumer cons ) {
        onAdd.add(cons);
    }

    public void removeListener(IPlayerIteratorConsumer itr ) {
        onRemove.add(itr);
    }


    public void remove(Player p) {
        WeakReference<Player> weak;
        Iterator<WeakReference<Player>> itr = players.iterator();
        while (itr.hasNext()) {
            weak = itr.next();
            if (weak.get() == null ) {
                itr.remove();
                continue;
            }
            if (weak.get().getName().equals(p.getName())) {
                Iterator<IPlayerIteratorConsumer> i = onRemove.iterator();
                IPlayerIteratorConsumer cons = null;
                while (i.hasNext()) {
                    cons = i.next();
                    if (! cons.validate())
                        i.remove();
                    else
                        cons.accept(p);
                }
                return;
            }
        }
    }

    public void validate() {
        clean(onAdd);
        clean(onRemove);
    }

    protected void clean(Collection<IPlayerIteratorConsumer> col) {
        Iterator<IPlayerIteratorConsumer> itr = col.iterator();
        while(itr.hasNext()){
            if (! itr.next().validate())
                itr.remove();
        }
    }

    public int size() {
        return players.size();
    }



    public void forEachOrRemove(Predicate<Player> pred) {
        Iterator<WeakReference<Player>> itr = players.iterator();
        WeakReference<Player> p;
        while(itr.hasNext()) {
            p = itr.next();
            if (p.get() == null || p.isEnqueued() || pred.test(p.get())) {
                itr.remove();
                continue;
            }
        }
    }

    public void forEach(Consumer<Player> cons) {
        Iterator<WeakReference<Player>> itr = players.iterator();
        WeakReference<Player> p;
        while(itr.hasNext()) {
            p = itr.next();
            if (p.get() == null || p.isEnqueued() || ! p.get().isOnline()) {
                itr.remove();
                continue;
            }
            cons.accept(p.get());
        }
    }


}
