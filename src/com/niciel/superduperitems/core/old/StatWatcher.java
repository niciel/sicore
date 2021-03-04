package com.niciel.superduperitems.core.old;

import java.util.UUID;

public class StatWatcher {

    public double value;
    public ModifierType modifier;
    public final UUID uuid;

    public final int power;

    public StatWatcher(double value, ModifierType modifier, UUID uuid, int power) {
        this.value = value;
        this.modifier = modifier;
        this.uuid = uuid;
        this.power = power;
    }

    public boolean isActive() {
        return true;
    }

    public double modify(StatisticValue statistic  , double value) {
        return modifier.modify(statistic , this , value);
    }

    public enum ModifierType {
        MULTIPLY(){
            @Override
            public double modify(StatisticValue statistic, StatWatcher watcher, double value) {
                return value*watcher.value;
            }
        },
        ADD(){
            @Override
            public double modify(StatisticValue statistic, StatWatcher watcher, double value) {
                return value + watcher.value;
            }
        };

        public double modify(StatisticValue statistic , StatWatcher watcher , double value) {
            return value;
        }

    }


}
