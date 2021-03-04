package com.niciel.superduperitems.quests;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import org.apache.commons.lang.RandomStringUtils;

public abstract class QuestElement implements GsonSerializable {

    private String randomID;

    public QuestElement(QuestElement e) {
        this.randomID = e.randomID;
    }

    protected void generateRandom() {
        randomID = prefix()+"-"+ RandomStringUtils.randomAlphabetic(5);
    }

    public abstract String prefix() ;



}
