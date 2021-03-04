package com.niciel.superduperitems.inGameEditor.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)

public @interface ChatObjectName {

    /**
     * @return  visible name of type object above fields if not added Simple name of class
     */
    String name();
}
