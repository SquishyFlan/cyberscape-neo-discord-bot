package com.titaniumtemplar.discordbot.model.stats;

import com.titaniumtemplar.db.jooq.enums.StatType;
import lombok.Value;

@Value
public class StatSkillScale
{
  StatType stat;
  int perSp;
}
