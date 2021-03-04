package com.niciel.superduperitems;

import java.util.UUID;

public interface Tickable {

    public void onTick();

    public UUID getUUID();
}
