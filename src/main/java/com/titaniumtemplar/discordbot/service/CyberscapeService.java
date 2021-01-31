package com.titaniumtemplar.discordbot.service;

import static java.util.Collections.singleton;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.discordbot.discord.GuildSettings;
import com.titaniumtemplar.discordbot.model.character.CharSkillsUpdate;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.character.Skill;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.model.monster.MonsterTemplate;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.repository.CyberscapeRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
/*
	Class: CyberscapeService
	Description: Utility functions for Discord operations
*/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeService {

	private final CyberscapeRepository repo;

	// Caches
	private StatConfig statConfig;
	private Map<String, CharStats> characterCache = new ConcurrentHashMap<>();
	private Map<String, GuildSettings> guildSettingsCache = new ConcurrentHashMap<>();
	private EnumeratedDistribution<MonsterTemplate> monsterCache;

	/*
		Method: getStatConfig
		Description: Returns StatConfig of Cyberscape Repository
		Input: User Object, String Message
		Output: StatConfig object
	*/
	public StatConfig getStatConfig() {
		if (statConfig == null) {
			statConfig = repo.getStatConfig();
		}
		return statConfig;
	}

	/*
		Method: getCharacter
		Description: Returns Character Object based on provided User ID
		Input: String userId
		Output: Character object
	*/
	public CharStats getCharacter(String userId) {
		CharStats character = characterCache.computeIfAbsent(
			userId,
			(uid) -> calcStats(repo.getCharacter(uid)));

		return character;
	}

	/*
		Method: createCharacter
		Description: Creates a new character
		Input: User ID, User Name
		Output: Character object
	*/
	public CharStats createCharacter(String userId, String username) {
		CharStats character = calcStats(repo.createCharacter(userId, username));
		characterCache.put(userId, character);
		return character;
	}

	/*
		Method: calcStats
		Description: Provide a CharStats for a character, calculate the new stats for the character
		Input: CharStats for a character
		Output: updated CharStats object
	*/
	private CharStats calcStats(CharStats character) {
		StatConfig statConfig = getStatConfig();
		character.calcStats(statConfig);
		return character;
	}

	/*
		Method: getRandomMonster
		Description: returns a random monster
		Output: MonsterTemplate
	*/
	public MonsterTemplate getRandomMonster() {
		if (monsterCache == null) {
			monsterCache = new EnumeratedDistribution<>(
				repo.getMonsters()
					.stream()
					.peek((monster) -> monster.calcStats(getStatConfig()))
					.map((monster) -> new Pair<>(monster, 1D / monster.getHpMax()))
					.collect(toList()));
		}
		return monsterCache.sample();
	}

	/*
		Method: awardXP
		Description: Given a collection of characters, award experience and return who leveled up
		Input: Collection of Characters, Int of expereince awarded
		Output: Set of Strings with leveled up characters
	*/
	public Set<String> awardXp(Collection<CharStats> participants, int xp) {
		Set<String> levelups = new HashSet<>();
		List<CharStats> adjustedParticipants = participants.stream()
			.filter(CharStats::canLevelUp)
			.peek((character) -> character.setXp(character.getXp() + xp))
			.peek((character) -> {
				if (character.levelUp()) {
					levelups.add(character.getUserId());
				}
			})
			.peek(this::calcStats)
			.collect(toList());
		repo.updateCharacters(adjustedParticipants);

		return levelups;
	}

	/*
		Method: getGuildSettings
		Description: Returns settings for provided guild
		Input: Guild ID
		Output: Guild Settings
	*/
	public GuildSettings getGuildSettings(String gid) {
		return guildSettingsCache.computeIfAbsent(gid, repo::getGuildSettings);
	}

	/*
		Method: addCombatChannel
		Description: Adds or removes a channel to or from a guild
		Input: Guild ID, Channel ID, Boolean where True is add
	*/
	public void addCombatChannel(String gid, String channelId, boolean add) {
		GuildSettings settings = getGuildSettings(gid);
		Set<String> combatChannels = new HashSet<>(settings.getCombatChannels());
		if (add && !combatChannels.contains(channelId)) {
			combatChannels.add(channelId);
			repo.addCombatChannel(gid, channelId);
		} else if (!add && combatChannels.remove(channelId)) {
			repo.removeCombatChannel(gid, channelId);
		}
		settings.setCombatChannels(combatChannels);
	}

	/*
		Method: updateCharSkills
		Description: Updates a character's skills
		Input: User ID, Updated Character skills, Flag if an admin set this
	*/
	public void updateCharSkills(String userId, CharSkillsUpdate charStats, boolean admin) {

		CharStats foundStats;
		try {
			foundStats = getCharacter(userId);
		} catch (NoSuchCharacterException ex) {
			if (!admin) throw ex;
			foundStats = getCharacter("~template~");
		}

		CharStats oldStats = foundStats;
		CharStats newStats = checkStats(userId, charStats, admin);
		StatConfig statConfig = getStatConfig();

		boolean noSpSpent = oldStats.getSpUsed() == newStats.getSpUsed();
		boolean spOverspent = newStats.getSpUsed() > newStats.getSpTotal();
		if (!admin && (noSpSpent || spOverspent)) {
			throw new IllegalArgumentException("I'll be having none of that.");
		}
		newStats.getSkills()
			.forEach((skillType, skill) -> {
				if (admin) {
					return; // They can do what they want
				}
				Skill oldSkill = oldStats.getSkills().get(skillType);
				if (skill.getRanks() < oldSkill.getRanks()
					|| skill.getSpec1Ranks() < oldSkill.getSpec1Ranks()
					|| skill.getSpec2Ranks() < oldSkill.getSpec2Ranks()
					|| !oldSkill.getSpec1Name().isEmpty() && !Objects.equals(skill.getSpec1Name(), oldSkill.getSpec1Name())
					|| !oldSkill.getSpec2Name().isEmpty() && !Objects.equals(skill.getSpec2Name(), oldSkill.getSpec2Name())
					|| (skill.getSpec1Name().isEmpty() != (skill.getSpec1Ranks() == 0))
					|| (skill.getSpec2Name().isEmpty() != (skill.getSpec2Ranks() == 0))
					|| skill.getSpec1Ranks() != 0 && skill.getRanks() < statConfig.getSpec1Start()
					|| skill.getSpec2Ranks() != 0 && skill.getRanks() < statConfig.getSpec2Start()
					|| skill.getSpec1Ranks() > skill.getRanks()
					|| skill.getSpec2Ranks() > skill.getRanks()
					|| skill.getSpec1Ranks() == 0 && skill.getSpec2Ranks() != 0) {
					throw new IllegalArgumentException("I'll be having none of that.");
				}
			});

		characterCache.put(userId, newStats);
		repo.updateCharacters(singleton(newStats));
	}

	/*
		Method: checkStats
		Description: Check the stats update for a character
		Input: User ID, Updated Character Stats, Flag for if an admin set this
		Output: new Character Stats
	*/
	public CharStats checkStats(String userId, CharSkillsUpdate charStats, boolean admin) {
		CharStats foundStats;
		try {
			foundStats = getCharacter(userId);
		} catch (NoSuchCharacterException ex) {
			if (!admin) throw ex;
			foundStats = getCharacter("~template~");
		}

		CharStats newCs = foundStats.clone();
		Map<SkillType, Skill> skills = newCs.getSkills();
		if (admin) {
			if (foundStats.getUserId().equals("~template~")) {
				newCs.setId(randomUUID());
			}
			newCs.setLevel(charStats.getLevel());
			newCs.setName(charStats.getName());
			newCs.setUserId(charStats.getUserId().toLowerCase());
		}
		charStats.getSkills()
			.forEach((skillType, skillDiff) ->
				skills.get(skillType)
					.apply(skillDiff));
		newCs.setHpCurrent(-1);
		newCs.setMpCurrent(-1);
		newCs.calcStats(statConfig);
		return newCs;
	}
}
