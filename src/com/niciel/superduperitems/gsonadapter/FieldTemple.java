package com.niciel.superduperitems.gsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

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
        },
                (o)-> {
                    return new JsonPrimitive((int) o);
                }
                ,Integer.class , int.class),
        DOUBLE(
                e-> {
                     return e.getAsDouble();
        },
                s->{
                    return new JsonPrimitive((double) s);
                },
                Double.class, double.class),
        BOOLEAN(    e-> {
            return e.getAsBoolean();
        },
                s->{
                    return new JsonPrimitive((double) s);
                },
                Boolean.class, boolean.class),
        STRING(     e-> {
            return e.getAsString();
        } ,
                s-> {
            return new JsonPrimitive((String) s);
                } , String.class),
        NONE(null,null,new Class[]{});



        private String types[];
        public final Function<JsonElement,Object> deserializer;
        public final Function<Object , JsonElement> serializer;

        PrimitiveWrapper(Function<JsonElement, Object> deser , Function<Object , JsonElement> serializer ,Class... classes) {
            types = new String[classes.length];
            for (int i = 0 ; i < classes.length ; i++) {
                types[i] = classes[i].getName();
            }
            this.deserializer = deser;
            this.serializer = serializer;
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