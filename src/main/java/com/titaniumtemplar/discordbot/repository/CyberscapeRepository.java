package com.titaniumtemplar.discordbot.repository;

import com.titaniumtemplar.db.jooq.tables.records.SkillScaleRecord;
import com.titaniumtemplar.db.jooq.tables.records.VitalScaleRecord;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.model.stats.StatConfig.StatConfigBuilder;
import com.titaniumtemplar.discordbot.model.stats.StatLevelScale;
import com.titaniumtemplar.discordbot.model.stats.StatSkillScale;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import static com.titaniumtemplar.db.jooq.tables.SkillScale.SKILL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.StatLevelScale.STAT_LEVEL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.StatSkillScale.STAT_SKILL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.VitalScale.VITAL_SCALE;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeRepository
{
  private final DSLContext db;

  public StatConfig getStatConfig()
  {
    StatConfigBuilder statConfigBuilder = StatConfig.builder();

    SkillScaleRecord skillScale = db.selectFrom(SKILL_SCALE)
      .fetchOne();

    VitalScaleRecord vitalScale = db.selectFrom(VITAL_SCALE)
      .fetchOne();

    statConfigBuilder
      .spec1Start(skillScale.getSpec1Start())
      .spec2Start(skillScale.getSpec2Start())
      .spPerLevel(skillScale.getSpPerLevel())
      .spPerLevelGap(skillScale.getSpPerLevelGap())
      .spIncAmount(skillScale.getSpIncAmount())
      .spCostPerRank(skillScale.getSpCostPerRank())
      .spCostIncAmount(skillScale.getSpCostIncAmount())
      .hpBase(vitalScale.getHpBase())
      .hpPerVit(vitalScale.getHpPerVit())
      .mpBase(vitalScale.getMpBase())
      .mpPerInt(vitalScale.getMpPerInt())
      .mpPerWis(vitalScale.getMpPerWis());

    db.selectFrom(STAT_LEVEL_SCALE)
      .forEach((record) -> statConfigBuilder.statLevelScale(
        record.getStat(),
        StatLevelScale.builder()
          .base(record.getBase())
          .perLevel(record.getPerLevel())
          .perLevelGap(record.getPerLevelGap())
          .incAmount(record.getIncAmount())
          .build()));

    db.selectFrom(STAT_SKILL_SCALE)
      .fetchGroups(
        STAT_SKILL_SCALE.STAT,
        (record) -> new StatSkillScale(record.getSkill(), record.getPerRank()))
      .forEach(statConfigBuilder::statSkillScale);

    return statConfigBuilder.build();
  }
}
