package com.titaniumtemplar.discordbot.repository;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.db.jooq.tables.records.CharacterRecord;
import com.titaniumtemplar.db.jooq.tables.records.MonsterRecord;
import com.titaniumtemplar.db.jooq.tables.records.SkillScaleRecord;
import com.titaniumtemplar.db.jooq.tables.records.VitalScaleRecord;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.character.Skill;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.model.monster.MonsterTemplate;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.model.stats.StatConfig.StatConfigBuilder;
import com.titaniumtemplar.discordbot.model.stats.StatLevelScale;
import com.titaniumtemplar.discordbot.model.stats.StatSkillScale;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.titaniumtemplar.db.jooq.tables.Character.CHARACTER;
import static com.titaniumtemplar.db.jooq.tables.Monster.MONSTER;
import static com.titaniumtemplar.db.jooq.tables.SkillScale.SKILL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.StatLevelScale.STAT_LEVEL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.StatSkillScale.STAT_SKILL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.VitalScale.VITAL_SCALE;
import static java.util.UUID.randomUUID;

@Repository
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
      .spCostPerRankGap(skillScale.getSpCostPerRankGap())
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
        STAT_SKILL_SCALE.SKILL,
        (record) -> new StatSkillScale(record.getStat(), record.getPerRank()))
      .forEach(statConfigBuilder::statSkillScale);

    return statConfigBuilder.build();
  }

  public CharStats getCharacter(String uid) {
    return db.selectFrom(CHARACTER)
	.where(CHARACTER.USER_ID.eq(uid))
	.fetchOptional(this::mapCharacter)
	.orElseThrow(NoSuchCharacterException::new);
  }

  public CharStats createCharacter(String uid) {
    CharacterRecord newChar = db.newRecord(CHARACTER);
    newChar.setId(randomUUID());
    newChar.setUserId(uid);
    newChar.setLevel(1);
    newChar.setXp(0);
    newChar.setHpCurrent(-1);
    newChar.setMpCurrent(-1);
    newChar.setFire(0);
    newChar.setFireSpec1Name("");
    newChar.setFireSpec1Value(0);
    newChar.setFireSpec2Name("");
    newChar.setFireSpec2Value(0);
    newChar.setWater(0);
    newChar.setWaterSpec1Name("");
    newChar.setWaterSpec1Value(0);
    newChar.setWaterSpec2Name("");
    newChar.setWaterSpec2Value(0);
    newChar.setLightning(0);
    newChar.setLightningSpec1Name("");
    newChar.setLightningSpec1Value(0);
    newChar.setLightningSpec2Name("");
    newChar.setLightningSpec2Value(0);
    newChar.setWind(0);
    newChar.setWindSpec1Name("");
    newChar.setWindSpec1Value(0);
    newChar.setWindSpec2Name("");
    newChar.setWindSpec2Value(0);
    newChar.setEarth(0);
    newChar.setEarthSpec1Name("");
    newChar.setEarthSpec1Value(0);
    newChar.setEarthSpec2Name("");
    newChar.setEarthSpec2Value(0);
    newChar.setSonic(0);
    newChar.setSonicSpec1Name("");
    newChar.setSonicSpec1Value(0);
    newChar.setSonicSpec2Name("");
    newChar.setSonicSpec2Value(0);
    newChar.setPersonal(0);
    newChar.setPersonalSpec1Name("");
    newChar.setPersonalSpec1Value(0);
    newChar.setPersonalSpec2Name("");
    newChar.setPersonalSpec2Value(0);
    newChar.setMaterial(0);
    newChar.setMaterialSpec1Name("");
    newChar.setMaterialSpec1Value(0);
    newChar.setMaterialSpec2Name("");
    newChar.setMaterialSpec2Value(0);
    newChar.setShift(0);
    newChar.setShiftSpec1Name("");
    newChar.setShiftSpec1Value(0);
    newChar.setShiftSpec2Name("");
    newChar.setShiftSpec2Value(0);
    newChar.setLife(0);
    newChar.setLifeSpec1Name("");
    newChar.setLifeSpec1Value(0);
    newChar.setLifeSpec2Name("");
    newChar.setLifeSpec2Value(0);
    newChar.setSpace(0);
    newChar.setSpaceSpec1Name("");
    newChar.setSpaceSpec1Value(0);
    newChar.setSpaceSpec2Name("");
    newChar.setSpaceSpec2Value(0);
    newChar.setGravity(0);
    newChar.setGravitySpec1Name("");
    newChar.setGravitySpec1Value(0);
    newChar.setGravitySpec2Name("");
    newChar.setGravitySpec2Value(0);

    newChar.store();

    return mapCharacter(newChar);
  }

  private CharStats mapCharacter(CharacterRecord record) {
    CharStats cs = new CharStats();
    cs.setId(record.getId());
    cs.setUserId(record.getUserId());
    cs.setHpCurrent(record.getHpCurrent());
    cs.setMpCurrent(record.getMpCurrent());
    cs.setXp(record.getXp());
    cs.setLevel(record.getLevel());

    Arrays.stream(SkillType.values())
	.forEach((skill )-> {
	  String skillName = skill.getLiteral();
	  cs.putSkill(skill, Skill.builder()
	      .ranks(record.get(skillName, Integer.class))
	      .spec1Name(record.get(skillName + "_spec1_name", String.class))
	      .spec1Ranks(record.get(skillName + "_spec1_value", Integer.class))
	      .spec2Name(record.get(skillName + "_spec2_name", String.class))
	      .spec2Ranks(record.get(skillName + "_spec2_value", Integer.class))
	      .build());
	});

    return cs;
  }

  public List<MonsterTemplate> getMonsters() {
    return db.selectFrom(MONSTER)
	.fetch(this::mapMonster);
  }

  private MonsterTemplate mapMonster(MonsterRecord record) {
    return MonsterTemplate.builder()
	.id(record.getId())
	.name(record.getName())
	.maxHp(record.getHp())
	.xp(record.getXp())
	.build();
  }

  public void awardXp(Collection<String> participantUids, int xp) {
    db.update(CHARACTER)
	.set(CHARACTER.XP, CHARACTER.XP.plus(xp))
	.where(CHARACTER.USER_ID.in(participantUids))
	.execute();
  }
}
