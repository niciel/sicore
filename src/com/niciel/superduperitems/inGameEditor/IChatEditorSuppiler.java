package com.niciel.superduperitems.inGameEditor;


public interface IChatEditorSuppiler {

    ChatEditor get(IBaseObjectEditor editor, Class clazz, String name , String description) ;
}
