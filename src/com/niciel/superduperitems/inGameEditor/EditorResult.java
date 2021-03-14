package com.niciel.superduperitems.inGameEditor;

public enum EditorResult {

    PLAYER_QUIT,
    FAIL,
    APPLAY_CHANGES(true),
    DISCARD_CHANGES;

    private boolean applay;


    EditorResult(boolean applay) {
        this.applay = applay;
    }


    EditorResult() {
        this.applay = false;
    }

    public boolean applayChanges() {
        return this.applay;
    }
}
