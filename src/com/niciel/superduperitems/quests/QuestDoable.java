package com.niciel.superduperitems.quests;

public abstract class QuestDoable extends QuestElement {

    private boolean hidden;
    private boolean enabled;

    public QuestDoable(QuestDoable e) {
        super(e);
        this.hidden = e.hidden;
        this.enabled = e.enabled;
    }


}
