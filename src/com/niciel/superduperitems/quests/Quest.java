package com.niciel.superduperitems.quests;

import com.niciel.superduperitems.inGameEditor.annotations.ChatEditable;
import com.niciel.superduperitems.quests.requirements.IRequirement;

import java.util.List;

public class Quest {



    @ChatEditable(name = "id" , excludeInEdit = true)
    private String typeID;

    @ChatEditable(name = "displayed")
    private String displayName;

    private List<IRequirement> requirements;



    private List<IQuestTrigger> triggers;





}
