package com.niciel.superduperitems.quests.requirements;

import com.niciel.superduperitems.gsonadapter.GsonSerializable;
import com.niciel.superduperitems.quests.QuestPlayer;

public interface  IRequirement extends GsonSerializable {


    public boolean meetRequirements(QuestPlayer player);
    public boolean finalizeRequirements(QuestPlayer player);

    public String getRequirementsDescription(QuestPlayer player);
    public String getRequirementsDenyMessage(QuestPlayer player);




}
