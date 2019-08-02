package com.titaniumtemplar.discordbot.repository;

import static com.titaniumtemplar.db.jooq.tables.Character.CHARACTER;
import static com.titaniumtemplar.db.jooq.tables.CharacterSkill.CHARACTER_SKILL;
import static com.titaniumtemplar.db.jooq.tables.GuildCombatChannels.GUILD_COMBAT_CHANNELS;
import static com.titaniumtemplar.db.jooq.tables.GuildSettings.GUILD_SETTINGS;
import static com.titaniumtemplar.db.jooq.tables.Monster.MONSTER;
import static com.titaniumtemplar.db.jooq.tables.MonsterSkill.MONSTER_SKILL;
import static com.titaniumtemplar.db.jooq.tables.SkillScale.SKILL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.StatLevelScale.STAT_LEVEL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.StatSkillScale.STAT_SKILL_SCALE;
import static com.titaniumtemplar.db.jooq.tables.VitalScale.VITAL_SCALE;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.jooq.lambda.Seq.seq;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.db.jooq.tables.CharacterSkill;
import com.titaniumtemplar.db.jooq.tables.records.CharacterRecord;
import com.titaniumtemplar.db.jooq.tables.records.CharacterSkillRecord;
import com.titaniumtemplar.db.jooq.tables.records.GuildCombatChannelsRecord;
import com.titaniumtemplar.db.jooq.tables.records.MonsterRecord;
import com.titaniumtemplar.db.jooq.tables.records.MonsterSkillRecord;
import com.titaniumtemplar.db.jooq.tables.records.SkillScaleRecord;
import com.titaniumtemplar.db.jooq.tables.records.VitalScaleRecord;
import com.titaniumtemplar.discordbot.discord.GuildSettings;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.character.Skill;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.model.monster.MonsterTemplate;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.model.stats.StatConfig.StatConfigBuilder;
import com.titaniumtemplar.discordbot.model.stats.StatLevelScale;
import com.titaniumtemplar.discordbot.model.stats.StatSkillScale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeRepository {

	private final DSLContext db;

	private static final com.titaniumtemplar.db.jooq.tables.Character C_EXCLUDED = CHARACTER.as("excluded");
	private static final CharacterSkill CS_EXCLUDED = CHARACTER_SKILL.as("excluded");

	private static <R extends Record> void bulkInsert(List<R> recordList, Table<R> table, DSLContext dbConnection) {
		if (recordList.isEmpty()) {
			return;
		}

		seq(recordList.subList(0, recordList.size() - 1))
			.foldLeft(
				dbConnection.insertInto(table),
				(insertQuery, record) -> insertQuery.set(record).newRecord())
			.set(recordList.get(recordList.size() - 1))
			.execute();
	}

	public StatConfig getStatConfig() {
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

	@Transactional
	public CharStats createCharacter(String uid, String name) {
		CharacterRecord newChar = db.newRecord(CHARACTER);
		UUID charId = randomUUID();
		newChar.setId(charId);
		newChar.setUserId(uid);
		newChar.setName(name);
		newChar.setLevel(1);
		newChar.setXp(0);
		newChar.setHpCurrent(-1);
		newChar.setMpCurrent(-1);
		newChar.setAvatarUrl("");
		newChar.store();

		List<CharacterSkillRecord> skillRecords = Arrays.stream(SkillType.values())
			.map((skillType) -> {
				CharacterSkillRecord r = db.newRecord(CHARACTER_SKILL);
				r.setCharacterId(charId);
				r.setSkill(skillType);
				r.setRanks(0);
				r.setSpec1Name("");
				r.setSpec1Ranks(0);
				r.setSpec2Name("");
				r.setSpec2Ranks(0);
				return r;
			})
			.collect(toList());
		bulkInsert(skillRecords, CHARACTER_SKILL, db);

		return mapCharacter(newChar, skillRecords);
	}

	private CharStats mapCharacter(CharacterRecord record) {

		Result<CharacterSkillRecord> skillRecords = db.selectFrom(CHARACTER_SKILL)
			.where(CHARACTER_SKILL.CHARACTER_ID.eq(record.getId()))
			.fetch();

		return mapCharacter(record, skillRecords);
	}

	private CharStats mapCharacter(
		CharacterRecord record,
		Collection<CharacterSkillRecord> skillRecords) {
		CharStats cs = new CharStats();
		cs.setId(record.getId());
		cs.setUserId(record.getUserId());
		cs.setName(record.getName());
		cs.setHpCurrent(record.getHpCurrent());
		cs.setMpCurrent(record.getMpCurrent());
		cs.setXp(record.getXp());
		cs.setLevel(record.getLevel());
		cs.setAvatarUrl(record.getAvatarUrl());

		skillRecords.forEach((r) -> {
			SkillType skill = r.getSkill();
			String skillName = skill.getLiteral();
			cs.putSkill(skill, Skill.builder()
				.ranks(r.getRanks())
				.spec1Name(r.getSpec1Name())
				.spec1Ranks(r.getSpec1Ranks())
				.spec2Name(r.getSpec2Name())
				.spec2Ranks(r.getSpec2Ranks())
				.build());
		});

		return cs;
	}

	@Transactional
	public void updateCharacters(
		Collection<CharStats> characters) {

		if (characters.isEmpty()) {
			return;
		}

		List<CharacterRecord> records = new ArrayList<>(characters.size());
		List<CharacterSkillRecord> csRecords = characters.stream()
			.peek((c) -> records.add(mapRecord(c)))
			.flatMap(this::mapSkillRecords)
			.collect(toList());

		seq(records.subList(0, records.size() - 1))
			.foldLeft(
				db.insertInto(CHARACTER),
				(insert, record) -> insert.set(record).newRecord())
			.set(records.get(records.size() - 1))
			.onDuplicateKeyUpdate()
			.set(CHARACTER.LEVEL, C_EXCLUDED.LEVEL)
			.set(CHARACTER.XP, C_EXCLUDED.XP)
			.set(CHARACTER.HP_CURRENT, C_EXCLUDED.HP_CURRENT)
			.set(CHARACTER.MP_CURRENT, C_EXCLUDED.MP_CURRENT)
			.execute();

		seq(csRecords.subList(0, csRecords.size() - 1))
			.foldLeft(
				db.insertInto(CHARACTER_SKILL),
				(insert, record) -> insert.set(record).newRecord())
			.set(csRecords.get(csRecords.size() - 1))
			.onDuplicateKeyUpdate()
			.set(CHARACTER_SKILL.RANKS, CS_EXCLUDED.RANKS)
			.set(CHARACTER_SKILL.SPEC1_NAME, CS_EXCLUDED.SPEC1_NAME)
			.set(CHARACTER_SKILL.SPEC1_RANKS, CS_EXCLUDED.SPEC1_RANKS)
			.set(CHARACTER_SKILL.SPEC2_NAME, CS_EXCLUDED.SPEC2_NAME)
			.set(CHARACTER_SKILL.SPEC2_RANKS, CS_EXCLUDED.SPEC2_RANKS)
			.execute();
	}

	public GuildSettings getGuildSettings(String guildId) {
		GuildSettings.GuildSettingsBuilder builder = db.selectFrom(GUILD_SETTINGS)
			.where(GUILD_SETTINGS.GUILD_ID.eq(guildId))
			.fetchOptional((record) -> GuildSettings.builder()
			.defaultRoleId(record.getDefaultRoleId()))
			.orElseGet(GuildSettings::builder);

		db.selectFrom(GUILD_COMBAT_CHANNELS)
			.where(GUILD_COMBAT_CHANNELS.GUILD_ID.eq(guildId))
			.forEach((cc) -> builder.combatChannel(cc.getChannelId()));

		return builder.build();
	}

	private CharacterRecord mapRecord(CharStats c) {
		CharacterRecord r = db.newRecord(CHARACTER);

		r.setId(c.getId());
		r.setUserId(c.getUserId());
		r.setName(c.getName());
		r.setHpCurrent(c.getHpCurrent());
		r.setMpCurrent(c.getMpCurrent());
		r.setXp(c.getXp());
		r.setLevel(c.getLevel());

		return r;
	}

	private Stream<CharacterSkillRecord> mapSkillRecords(CharStats c) {

		return Arrays.stream(SkillType.values())
			.map((skillType) -> {
				CharacterSkillRecord r = db.newRecord(CHARACTER_SKILL);
				String skillName = skillType.getLiteral();
				Skill skill = c.getSkills().get(skillType);

				r.setCharacterId(c.getId());
				r.setSkill(skillType);
				r.setRanks(skill.getRanks());
				r.setSpec1Name(skill.getSpec1Name());
				r.setSpec1Ranks(skill.getSpec1Ranks());
				r.setSpec2Name(skill.getSpec2Name());
				r.setSpec2Ranks(skill.getSpec2Ranks());

				return r;
			});
	}

	public List<MonsterTemplate> getMonsters() {
		Map<UUID, List<Skill>> skills = db.selectFrom(MONSTER_SKILL)
			.fetchGroups(MONSTER_SKILL.MONSTER_ID, this::mapSkill);

		return db.selectFrom(MONSTER)
			.fetch((r) -> mapMonster(r, skills));
	}

	private MonsterTemplate mapMonster(MonsterRecord record, Map<UUID, List<Skill>> skills) {
		return MonsterTemplate.builder()
			.id(record.getId())
			.name(record.getName())
			.hpMax(record.getHp())
			.xp(record.getXp())
			.build();
	}

	private Skill mapSkill(MonsterSkillRecord r) {
		return Skill.builder()
			.ranks(r.getRanks())
			.spec1Name(r.getSpec1Name())
			.spec1Ranks(r.getSpec1Ranks())
			.spec2Name(r.getSpec2Name())
			.spec2Ranks(r.getSpec2Ranks())
			.build();
	}

	public void addCombatChannel(String gid, String channelId) {
		GuildCombatChannelsRecord gcc = db.newRecord(GUILD_COMBAT_CHANNELS);
		gcc.setGuildId(gid);
		gcc.setChannelId(channelId);
		gcc.insert();
	}

	public void removeCombatChannel(String gid, String channelId) {
		db.deleteFrom(GUILD_COMBAT_CHANNELS)
			.where(
				GUILD_COMBAT_CHANNELS.GUILD_ID.eq(gid),
				GUILD_COMBAT_CHANNELS.CHANNEL_ID.eq(channelId))
			.execute();
	}
}
