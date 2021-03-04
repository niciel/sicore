package com.niciel.superduperitems.cfg;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(value = RetentionPolicy.RUNTIME)
public @interface Cfg {

    String path ();
}
