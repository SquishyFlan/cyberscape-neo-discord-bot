package com.titaniumtemplar.discordbot.model.stats;

import com.titaniumtemplar.db.jooq.enums.StatType;
import lombok.Value;

/*
	Class: StatSkillScale
	Description: Provides stats for a skill and how it scales
*/
@Value
public class StatSkillScale
{
  StatType stat;
  int perSp;
}
