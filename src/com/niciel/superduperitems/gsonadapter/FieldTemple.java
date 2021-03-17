package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonElement;

import java.lang.invoke.MethodHandle;
import java.util.function.Function;

public class FieldTemple {


    String path;
    boolean isPrimitive;
    PrimitiveWrapper type;
    MethodHandle getter;
    MethodHandle setter;

    public  enum PrimitiveWrapper {
        INT((e) -> {
            return e.getAsInt();
        }
                ,Integer.class , int.class),
        DOUBLE(e-> {
            return e.getAsDouble();
        },Double.class, double.class),
        BOOLEAN(e-> {
            return e.getAsBoolean();
        },Boolean.class, boolean.class),
        NONE(null,new Class[]{});

        private String types[];
        public final Function<JsonElement,Object> deserializer;

        PrimitiveWrapper(Function<JsonElement, Object> deser , Class... classes) {
            types = new String[classes.length];
            for (int i = 0 ; i < classes.length ; i++) {
                types[i] = classes[i].getName();
            }
            this.deserializer = deser;
        }

        public static PrimitiveWrapper wrapper(String type) {
            for (PrimitiveWrapper pe : PrimitiveWrapper.values()) {
                for(String s : pe.types) {
                    if (s.contentEquals(type))
                        return pe;
                }
            }
            return null;
        }

    }

}