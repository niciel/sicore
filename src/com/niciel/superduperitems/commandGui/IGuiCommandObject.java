package com.niciel.superduperitems.commandGui;

public interface IGuiCommandObject {

    /**
     * after object inhereds from GuiCommandAdd is executed, pass fullCommand without spacing " " on the end of string
     * @param command pass fullCommand without spacing " " on the end of string
     */
    void init(String command);
}
