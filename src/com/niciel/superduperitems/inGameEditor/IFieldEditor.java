package com.niciel.superduperitems.inGameEditor;

import java.lang.reflect.Field;


/**
 * gdy klasa  {@link ChatEditor} posiada ten interfejs za kazdym razem po stworzeniu edytora na podstawie pola klasy powinna zostac wywolana metoda init(Field)
 */
public interface IFieldEditor {

    /**
     * metoda wywolywana po stworzeniu edytora opartego o pole klasy przekazujaca dodatkowe iformacje
     * @param f
     */
    void init(Field f);
}
