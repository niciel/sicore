package com.niciel.superduperitems.inGameEditor;

public interface IFieldUpdateCallBack {


    /**
     * called each time reference to field is Set
     * @param field
     */
    public void validate(String field);
}
