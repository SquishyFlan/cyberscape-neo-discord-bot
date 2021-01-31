package com.titaniumtemplar.discordbot.model.character;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import java.util.Map;
import lombok.Data;

/*
	Class: CharSkillsUpdate
	Description: Object that lists what the skills are to be updated to
*/
@Data
public class CharSkillsUpdate {

	Map<SkillType, Skill> skills;
	String name;
	String userId;
	int level;
}
