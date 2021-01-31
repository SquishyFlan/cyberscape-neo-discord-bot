package com.titaniumtemplar.discordbot.model.stats;

import lombok.Builder;
import lombok.Value;

/*
	Class: StatLevelScale
	Description: Provides stats with base value and how it scales per level
*/
@Value
@Builder
public class StatLevelScale
{
  private int base;
  private int perLevel;
  private int perLevelGap;
  private int incAmount;
}
