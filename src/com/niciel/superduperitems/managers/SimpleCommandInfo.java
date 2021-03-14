package com.niciel.superduperitems.managers;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface SimpleCommandInfo {

    String command();
    String usage();
    String description();
    String[] aliases();

}
