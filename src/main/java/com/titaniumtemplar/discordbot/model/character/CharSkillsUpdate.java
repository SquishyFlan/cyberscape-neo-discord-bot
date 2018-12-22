package com.titaniumtemplar.discordbot.model.character;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import java.util.Map;
import lombok.Data;

@Data
public class CharSkillsUpdate {

	Map<SkillType, Skill> skills;
}
