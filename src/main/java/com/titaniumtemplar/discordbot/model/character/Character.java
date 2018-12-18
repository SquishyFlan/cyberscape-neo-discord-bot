package com.titaniumtemplar.discordbot.model.character;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.db.jooq.enums.StatType;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class Character
{
  // DB Values
  private UUID id;
  private String userId;
  private String name;
  private int hpCurrent;
  private int mpCurrent;
  private int xp;
  private int level;

  // Calculated values
  private int hpMax;
  private int mpMax;
  private int spTotal;
  private int spUsed;
  private Map<SkillType, Skill> skills;
  private Map<StatType, Integer> stats;

  public void calcStats(StatConfig config)
  {
    // TODO: Calc the stats!
  }
}
