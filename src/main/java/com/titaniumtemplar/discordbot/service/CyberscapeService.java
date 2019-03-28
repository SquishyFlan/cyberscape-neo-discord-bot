package com.titaniumtemplar.discordbot.service;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.discordbot.discord.GuildSettings;
import com.titaniumtemplar.discordbot.model.character.CharSkillsUpdate;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.character.Skill;
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

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeService {

	private final CyberscapeRepository repo;

	// Caches
	private StatConfig statConfig;
	private Map<String, CharStats> characterCache = new ConcurrentHashMap<>();
	private Map<String, GuildSettings> guildSettingsCache = new ConcurrentHashMap<>();
	private EnumeratedDistribution<MonsterTemplate> monsterCache;

	public StatConfig getStatConfig() {
		if (statConfig == null) {
			statConfig = repo.getStatConfig();
		}
		return statConfig;
	}

	public CharStats getCharacter(String userId) {
		CharStats character = characterCache.computeIfAbsent(
			userId,
			(uid) -> calcStats(repo.getCharacter(uid)));

		return character;
	}

	public CharStats createCharacter(String userId, String username) {
		CharStats character = calcStats(repo.createCharacter(userId, username));
		characterCache.put(userId, character);
		return character;
	}

	private CharStats calcStats(CharStats character) {
		StatConfig statConfig = getStatConfig();
		character.calcStats(statConfig);
		return character;
	}

	public MonsterTemplate getRandomMonster() {
		if (monsterCache == null) {
			monsterCache = new EnumeratedDistribution<>(
				repo.getMonsters()
					.stream()
					.map((monster) -> new Pair<>(monster, 1D / monster.getMaxHp()))
					.collect(toList()));
		}
		return monsterCache.sample();
	}

	public Set<String> awardXp(Collection<String> participantUids, int xp) {
		Set<String> levelups = new HashSet<>();
		List<CharStats> adjustedParticipants = participantUids.stream()
			.map(this::getCharacter)
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

	public GuildSettings getGuildSettings(String gid) {
		return guildSettingsCache.computeIfAbsent(gid, repo::getGuildSettings);
	}

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

	public void updateCharSkills(String userId, CharSkillsUpdate charStats) {
		if (userId.equals("~template~")) {
			throw new IllegalArgumentException("I'll be having none of that.");
		}

		CharStats oldStats = getCharacter(userId);
		CharStats newStats = checkStats(userId, charStats);
		StatConfig statConfig = getStatConfig();

		boolean noSpSpent = oldStats.getSpUsed() == newStats.getSpUsed();
		boolean spOverspent = newStats.getSpUsed() > newStats.getSpTotal();
		if (noSpSpent || spOverspent) {
			throw new IllegalArgumentException("I'll be having none of that.");
		}
		newStats.getSkills()
			.forEach((skillType, skill) -> {
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

	public CharStats checkStats(String userId, CharSkillsUpdate charStats) {
		CharStats newCs = getCharacter(userId).clone();
		Map<SkillType, Skill> skills = newCs.getSkills();
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
