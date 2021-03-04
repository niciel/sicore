package com.niciel.superduperitems.book;

import com.niciel.superduperitems.customitems.CustomItem;

import java.lang.reflect.Field;

public class BookClassEditor extends BookGui {



    public BookClassEditor(CustomItem ci) {

        BookEditable editable;
        for (Field f : ci.getClass().getDeclaredFields()) {
            editable = f.getAnnotation(BookEditable.class);
            if (editable == null)
                continue;



        }
    }





}
