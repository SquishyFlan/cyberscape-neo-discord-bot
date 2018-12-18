package com.titaniumtemplar.discordbot.model.stats;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import lombok.Value;

@Value
public class StatSkillScale
{
  SkillType skill;
  int perRank;
}
