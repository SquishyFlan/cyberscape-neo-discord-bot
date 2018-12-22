package com.titaniumtemplar.discordbot.model.character;

import static com.titaniumtemplar.db.jooq.enums.StatType.int_;
import static com.titaniumtemplar.db.jooq.enums.StatType.vit;
import static com.titaniumtemplar.db.jooq.enums.StatType.wis;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.db.jooq.enums.StatType;
import com.titaniumtemplar.discordbot.model.combat.Specialization;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.model.stats.StatLevelScale;
import com.titaniumtemplar.discordbot.model.stats.StatSkillScale;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CharStats {

	private static final int MAX_LEVEL = 50;
	private static final int BASE_XP = 200;
	private static final float XP_EXPONENT = 1.53f;
	private static final AtomicInteger TOTAL_XP = new AtomicInteger();
	private static final List<Integer> NEXT_LEVELS = IntStream.range(0, MAX_LEVEL)
		.map((level) -> (int) (BASE_XP * Math.pow(level, XP_EXPONENT)))
		.peek(TOTAL_XP::addAndGet)
		.boxed()
		.collect(toList());

	// DB Values
	private UUID id;
	private String userId;
	private int hpCurrent;
	private int mpCurrent;
	private int xp;
	private int level;
	private Map<SkillType, Skill> skills = new HashMap<>();

	@JsonIgnore
	private Set<Specialization> specs = new HashSet<>();

	// Derived values
	private int hpMax;
	private int mpMax;
	private int xpNext;
	private int spTotal;
	private int spUsed;
	private int spLeft;
	private Map<StatType, AtomicInteger> stats = new HashMap<>();

	// Set from Discord/Auth
	private String name;

	public void calcStats(StatConfig config) {
		spTotal = 0;
		int spPerLevel = config.getSpPerLevel();
		int spLevelGap = config.getSpPerLevelGap();
		int spInc = config.getSpIncAmount();
		for (int l = 2; l < level + 1; l++) {
			if (l % spLevelGap == 0) {
				spPerLevel += spInc;
			}
			spTotal += spPerLevel;
		}

		spUsed = 0;
		int spCostPerRank = config.getSpCostPerRank();
		int spCostRankGap = config.getSpCostPerRankGap();
		int spCostIncAmount = config.getSpCostIncAmount();
		int spec1Start = config.getSpec1Start();
		int spec2Start = config.getSpec2Start();
		Map<StatType, StatLevelScale> statLevelScales = config.getStatLevelScales();
		Map<SkillType, List<StatSkillScale>> statSkillScales = config.getStatSkillScales();

		Arrays.stream(StatType.values())
			.forEach((stat) -> {
				StatLevelScale levelScale = statLevelScales.get(stat);
				int statVal = levelScale.getBase();
				int statPerLevel = levelScale.getPerLevel();
				int statLevelGap = levelScale.getPerLevelGap();
				int statInc = levelScale.getIncAmount();

				int statLevel = level;
				while (statLevel > 0) {
					statVal += statPerLevel * (statLevel - 1);
					statLevel -= statLevelGap;
					statPerLevel = statInc;
				}

				stats.put(stat, new AtomicInteger(statVal));
			});

		Arrays.stream(SkillType.values())
			.forEach((skillType) -> {
				Skill skill = skills.get(skillType);
				int ranks = skill.getRanks();
				int skillSpUsed = 0;
				if (ranks >= spec1Start) {
					int specRanks = skill.getSpec1Ranks();
					int costPerRank = spCostPerRank + ((spCostIncAmount * spec1Start / spCostRankGap));
					while (specRanks > 0) {
						skillSpUsed += costPerRank * specRanks;
						specRanks -= spCostRankGap;
						costPerRank = spCostIncAmount;
					}
				}

				if (ranks >= spec2Start) {
					int specRanks = skill.getSpec2Ranks();
					int costPerRank = spCostPerRank + ((spCostIncAmount * spec2Start / spCostRankGap));
					while (specRanks > 0) {
						skillSpUsed += costPerRank * specRanks;
						specRanks -= spCostRankGap;
						costPerRank = spCostIncAmount;
					}
				}

				int costPerRank = spCostPerRank;
				while (ranks > 0) {
					skillSpUsed += costPerRank * ranks;
					ranks -= spCostRankGap;
					costPerRank = spCostIncAmount;
					if (spCostRankGap < 1) {
						break;
					}
				}

				spUsed += skillSpUsed;

				int totalSkillSpUsed = skillSpUsed;
				statSkillScales.get(skillType)
					.forEach((scale) -> stats.get(scale.getStat())
					.addAndGet(totalSkillSpUsed * scale.getPerSp()));
//
//				skill.setNextRankCost(
//					spCostPerRank + (spCostIncAmount * ranks / spCostRankGap));
			});

		spLeft = spTotal - spUsed;

		hpMax = config.getHpBase()
			+ stats.get(vit).get() * config.getHpPerVit();
		mpMax = config.getMpBase()
			+ stats.get(int_).get() * config.getMpPerInt()
			+ stats.get(wis).get() * config.getMpPerWis();

		while (level < MAX_LEVEL && xp > NEXT_LEVELS.get(level)) {
			xp -= NEXT_LEVELS.get(level);
			level++;
			hpCurrent = -1;
			mpCurrent = -1;
		}
		if (level != MAX_LEVEL) {
			xpNext = NEXT_LEVELS.get(level);
		} else {
			xp = -1;
			xpNext = -1;
		}

		if (hpCurrent == -1) {
			hpCurrent = hpMax;
		}
		if (mpCurrent == -1) {
			mpCurrent = mpMax;
		}
	}

	public void putSkill(SkillType skill, Skill values) {
		skills.put(skill, values);
		if (values.getSpec1Name() != null) {
			specs.add(Specialization.fromString(values.getSpec1Name()));
			if (values.getSpec2Name() != null) {
				specs.add(Specialization.fromString(values.getSpec2Name()));
			}
		}
	}

	public boolean levelUp() {
		if (xp < xpNext) {
			return false;
		}
		return true;
	}

	public boolean maxLevel() {
		return level == MAX_LEVEL;
	}
}
