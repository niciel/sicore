package com.niciel.superduperitems.inGameEditor.editors.object;

import com.niciel.superduperitems.commandGui.CommandPointer;
import com.niciel.superduperitems.inGameEditor.IChatEditor;
import com.niciel.superduperitems.utils.Ref;

public  class ListEditorData {



    public Ref ref;
    public IChatEditor editor;
    public String numericID;
    public String command;
    public String fullCommand;

    public ListEditorData(int numericID,String command ,  Ref ref, IChatEditor editor) {
        this.numericID = String.valueOf(numericID);
        this.ref = ref;
        this.command = command;
        this.editor = editor;
        this.fullCommand = command + " " + numericID;
    }



}