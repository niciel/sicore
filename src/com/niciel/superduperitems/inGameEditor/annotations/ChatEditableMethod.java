package com.niciel.superduperitems.inGameEditor.annotations;


import net.md_5.bungee.api.ChatColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ChatEditableMethod {


}
