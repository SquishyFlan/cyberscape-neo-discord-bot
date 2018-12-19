package com.titaniumtemplar.discordbot.model.character;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Skill
{
  private int ranks;
  private String spec1Name;
  private int spec1Ranks;
  private String spec2Name;
  private int spec2Ranks;
  private int nextRankCost;
}
