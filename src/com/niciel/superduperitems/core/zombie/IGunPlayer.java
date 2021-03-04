package com.niciel.superduperitems.core.zombie;

public interface IGunPlayer {


    public boolean isReloading();
    public void beginReload(GunComponent gc , int ammunition) ;
    public void cancelReloading() ;
    public int getLastShootTick();
    public void setLastShootTick(int i);
// na komponencie gc wykonuje sie onreloadtick


}
