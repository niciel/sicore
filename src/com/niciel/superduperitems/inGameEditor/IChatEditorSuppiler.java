package com.niciel.superduperitems.inGameEditor;

import com.niciel.superduperitems.inGameEditor.IChatEditor;


public interface IChatEditorSuppiler {

    IChatEditor get(IBaseObjectEditor editor, Class clazz,String name , String description) ;
}
