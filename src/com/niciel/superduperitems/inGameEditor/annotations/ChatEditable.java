package com.niciel.superduperitems.inGameEditor.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ChatEditable {


    /**
     * @return description showed somewhere in chat
     */
    String description() default "";

    /**
     * @return name used to represent field
     */
    String name() default "";

    /**
     * @return exclude from editing
     */
    boolean excludeInEdit() default false;

}
