package com.titaniumtemplar.discordbot.model.stats;

import com.titaniumtemplar.db.jooq.enums.StatType;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class StatConfig
{
  private int spec1Start;
  private int spec2Start;
  private int spPerLevel;
  private int spPerLevelGap;
  private int spIncAmount;
  private int spCostPerRank;
  private int spCostPerRankGap;
  private int spCostIncAmount;
  private int hpBase;
  private int hpPerVit;
  private int mpBase;
  private int mpPerInt;
  private int mpPerWis;

  @Singular
  private Map<StatType, StatLevelScale> statLevelScales;

  @Singular
  private Map<StatType, List<StatSkillScale>> statSkillScales;
}
