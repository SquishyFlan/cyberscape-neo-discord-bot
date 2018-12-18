package com.titaniumtemplar.discordbot.model.stats;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StatLevelScale
{
  private int base;
  private int perLevel;
  private int perLevelGap;
  private int incAmount;
}
