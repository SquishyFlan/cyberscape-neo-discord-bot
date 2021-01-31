package com.titaniumtemplar.discordbot.discord;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.ATTACK;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.BOLT;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.SHOOT;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.dv8tion.jda.core.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.core.Permission.MESSAGE_READ;
import static net.dv8tion.jda.core.Permission.MESSAGE_WRITE;
import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

import com.titaniumtemplar.discordbot.discord.commands.AlertCommand;
import com.titaniumtemplar.discordbot.discord.commands.AttackCommand;
import com.titaniumtemplar.discordbot.discord.commands.BoltCommand;
import com.titaniumtemplar.discordbot.discord.commands.ConfigCommand;
import com.titaniumtemplar.discordbot.discord.commands.DiscordCommand;
import com.titaniumtemplar.discordbot.discord.commands.HelpCommand;
import com.titaniumtemplar.discordbot.discord.commands.ProfileCommand;
import com.titaniumtemplar.discordbot.discord.commands.RegisterCommand;
import com.titaniumtemplar.discordbot.discord.commands.RoleCommand;
import com.titaniumtemplar.discordbot.discord.commands.ShootCommand;
import com.titaniumtemplar.discordbot.discord.commands.SkillsCommand;
import com.titaniumtemplar.discordbot.discord.commands.UnknownCommand;
import com.titaniumtemplar.discordbot.model.combat.Attack;
import com.titaniumtemplar.discordbot.model.combat.Combat;
import com.titaniumtemplar.discordbot.model.monster.Monster;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/*
	Class: Myra
	Description: Main Discord bot that handles Discord interactions
	Parent class: ListenerAdapter
*/
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Myra extends ListenerAdapter {

	//<editor-fold defaultstate="collapsed" desc="Static fields">
	private static final int COMBAT_ROUND_SECONDS = 30;
	private static final int COMBAT_END_COOLDOWN = 120;
	private static final int COMBAT_WAIT_LOWER = 300;
	private static final int COMBAT_WAIT_UPPER = 3600;
	private static final Random RAND = new Random();
	private static final Pattern COMMAND_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
	private static final String ATTACK_EMOJI = "⚔";
	private static final String SHOOT_EMOJI = "🏹";
	private static final String BOLT_EMOJI = "⚡";
//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Injected Fields">
	private final CyberscapeService service;
	private final JDA discord;
	private final ScheduledExecutorService combatThreadPool;
	private final String baseUrl;
//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Members">
	private final Map<String, Combat> combats = new HashMap<>();
	private final Map<String, Function<? super List<String>, ? extends DiscordCommand>> commands = new HashMap<>();
	private final Map<String, ScheduledFuture<?>> combatFutures = new HashMap<>();
//</editor-fold>

	/*
		Method: setup
		Description: Initializes Event Listeners
	*/
	@PostConstruct
	private void setup() {
		discord.addEventListener(this);
		commands.put(".register", RegisterCommand::withArgs);
		commands.put(".profile", ProfileCommand::withArgs);
		commands.put(".skills", SkillsCommand::withArgs);
		commands.put(".help", HelpCommand::withArgs);
		commands.put(".role", RoleCommand::withArgs);
		commands.put(".strike", AttackCommand::withArgs);
		commands.put(".shoot", ShootCommand::withArgs);
		commands.put(".bolt", BoltCommand::withArgs);
		commands.put(".config", ConfigCommand::withArgs);
		commands.put(".alert", AlertCommand::withArgs);
	}

	/*
		Method: destroy
		Description: Stops all combat
	*/
	@PreDestroy
	private void destroy() {
		combatThreadPool.shutdownNow();
	}

	/*
		Method: schedule
		Description: Schedule something to happen in the future
		Input: Runnable object to be executed, Integer for how many seconds to wait
		Output: ScheduledFuture object that contains item that's scheduled
	*/
	private ScheduledFuture<?> schedule(Runnable r, int timeSeconds) {
		return schedule(r, timeSeconds, () -> {});
	}

	/*
		Method: schedule
		Description: Schedule something to happen in the future
		Input: Runnable object to be executed, Integer for how many seconds to wait, Runnable object that occurs on failure
		Output: ScheduledFuture object that contains item that's scheduled to occur with fail function
	*/
	private ScheduledFuture<?> schedule(Runnable r, int timeSeconds, Runnable onFailure) {
		Runnable wrapped = () -> {
			try {
				r.run();
			} catch (Throwable t) {
				log.error("Uncaught error!", t);
				onFailure.run();
			}
		};
		return combatThreadPool.schedule(wrapped, timeSeconds, SECONDS);
	}
	
	/*
		Method: joinGuild
		Description: Start combat in the provided Guild
		Input: Guild object to handle combat
	*/
	private void joinGuild(Guild guild) {
		scheduleCombat(guild);

		guild.getRolesByName("Grinding For XP", true)
			.stream()
			.findAny()
			.ifPresent((grindingRole) ->
				guild.getMembersWithRoles(grindingRole)
					.stream()
					.peek((member) -> log.info("Removing stale Grinding role from " + member.getEffectiveName()))
					.forEach((member) ->
						guild.getController().removeSingleRoleFromMember(member, grindingRole).queue()));
	}

	/*
		Method: getEligibleChannels
		Description: Retrieve channels eligible for provided Guild
		Input: Guild object
		Output: List of eligible TextChannels
	*/
	private List<TextChannel> getEligibleChannels(Guild guild) {
		Member myraMember = guild.getSelfMember();
		Set<String> combatChannels = service.getGuildSettings(guild.getId())
			.getCombatChannels();

		return guild.getChannels()
			.stream()
			.filter((channel) -> channel.getType() == TEXT)
			.map((channel) -> (TextChannel) channel)
			.filter((channel) -> !combatChannels.contains(channel.getId()))
			.peek((channel) -> log.info("Checking channel {} with permissions {} for permissions", channel.getName(), channel.getPermissionOverrides()))
			.filter((channel) -> myraMember.hasPermission(channel, MESSAGE_READ, MESSAGE_WRITE))
			.peek((channel) -> log.info("Permitting channel {}", channel.getName()))
			.collect(toList());
	}

	/*
		Method: onReady
		Description: Overloaded function. Performs event when ready.
		Input: Event to process when ready.
	*/
	@Override
	public void onReady(ReadyEvent event) {
		event.getJDA()
			.getGuilds()
			.forEach(this::joinGuild);

		log.info("Ready!");
	}

	/*
		Method: onGuildLeave
		Description: Execute provide event when leaving Guild
		Input: Event that executes on leaving Guild
	*/
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		String guildId = event.getGuild().getId();
		combats.remove(guildId);
	}

	/*
		Method: onGuildJoin
		Description: Execute provided event when joining Guild
		Input: Event that executes on joining Guild
	*/
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Guild guild = event.getGuild();
		joinGuild(guild);
	}

	/*
		Method: onGuildMemberJoin
		Description: Execute event when Guild Member joins. Skips if bot.
		Input: Event that executes on Guild Member joining
	*/
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member member = event.getMember();
		if (member.getUser().isBot()) {
			return;
		}

		Guild guild = event.getGuild();

		guild.getController()
			.addRolesToMember(
				member, guild.getRolesByName("In Character Select", false))
			.queue();
	}

	/*
		Method: onGuildMessageReactionAdd
		Description: Handles users reacting to combat messages
		Input: Event of reaction
	*/
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		Combat combat = getCombatForReaction(event);
		if (combat == null) {
			return;
		}

		List<String> command = getCommandForEmoji(event.getReactionEmote());
		if (command.isEmpty()) {
			return;
		}

		runCommand(command, null, event.getUser(), event.getMember());
	}

	/*
		Method: onGuildMessageReactionRemove
		Description: Handle users remove reaction to combat messages
		Input: Event of removing reaction
	*/
	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
		Combat combat = getCombatForReaction(event);
		if (combat == null) {
			return;
		}

		String userId = event.getUser().getId();
		synchronized (combat) {
			combat.removeAttack(userId);
		}
		updateCombatMessage(combat);
	}

	/*
		Method: getCombatForReaction
		Description: Retrieve combat for provided reaction
		Input: Event that executes on leaving
	*/
	private Combat getCombatForReaction(GenericGuildMessageReactionEvent event) {
		if (event.getUser().isBot()) {
			return null;
		}

		Guild guild = event.getGuild();
		Combat combat = combats.get(guild.getId());
		if (combat == null) {
			return null;
		}

		Message message = combat.getMessage();
		if (!event.getMessageId().equals(message.getId())) {
			return null;
		}

		return combat;
	}

	/*
		Method: getCommandForEmoji
		Description: Return List with the emoji to command mapping
		Input: ReactionEmote object chosen
		Output: List contains action performed
	*/
	private List<String> getCommandForEmoji(ReactionEmote emote) {
		switch (emote.getName()) {
			case ATTACK_EMOJI:
				return singletonList(".strike");
			case SHOOT_EMOJI:
				return singletonList(".shoot");
			case BOLT_EMOJI:
				return singletonList(".bolt");
			default:
				return emptyList();
		}
	}

	/*
		Method: onPrivateMessageReceived
		Description: Handle private message received
		Input: Event of private message received
	*/
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		handleCommand(event.getMessage(), event.getAuthor(), null);
	}

	/*
		Method: onGuildMessageReceived
		Description: Handles Guild Message received
		Input: GuildMessageReceivedEvent object
	*/
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		handleCommand(event.getMessage(), event.getAuthor(), event.getMember());
	}

	/*
		Method: handleCommand
		Description: Handle the provided command
		Input: Message object, User originator object, Member object
	*/
	private void handleCommand(Message message, User author, Member member) {
		String content = message.getContentDisplay();

		if (!content.startsWith(".")) {
			return;
		}

		List<String> splitCommand = COMMAND_PATTERN.matcher(content)
			.results()
			.map(MatchResult::group)
			.collect(toList());

		runCommand(splitCommand, message, author, member);
	}

	/*
		Method: runCommand
		Description: Executes the provided command
		Input: List with commands, Message object, User originator object, Member object
	*/
	private void runCommand(
		List<String> splitCommand,
		Message message,
		User author,
		Member member) {
		commands.getOrDefault(splitCommand.get(0), UnknownCommand::withArgs)
			.apply(splitCommand)
			.run(service, this, message, author, member);
	}

  /**********
	 * COMBAT
   *********/
   	/*
		Method: scheduleCombat
		Description: Start combat for provided guild
		Input: Guild object
	*/
	private void scheduleCombat(Guild guild) {
		String guildId = guild.getId();
		if (combats.containsKey(guildId)) {
			log.info("Not scheduling duplicate combat for Guild {}", guild.getName());
		}

		int waitTime = getWaitTime();
		log.info("Scheduling combat for Guild {} in {}s", guild.getName(), waitTime);
		combatSchedule(guildId,
			() -> startCombat(guildId),
			waitTime,
			() -> {
				log.warn("Failed to start combat for Guild {}! Trying again...", guild.getName());
				scheduleCombat(guild);
			});
	}

   	/*
		Method: getWaitTime
		Description: Get randomized wait time
		Output: Integer with randomized wait time
	*/
	private int getWaitTime() {
		return RAND.nextInt(COMBAT_WAIT_UPPER - COMBAT_WAIT_LOWER) + COMBAT_WAIT_LOWER;
	}

   	/*
		Method: combatSchedule
		Description: Schedule next combat
		Input: String with Guild ID, Runnable object, Integer for time in future, Runnable object for failure
	*/
	private void combatSchedule(String guildId, Runnable r, int timeSeconds, Runnable onFailure) {
		combatFutures.put(guildId, schedule(r, timeSeconds, onFailure));
	}

   	/*
		Method: startCombat
		Description: Initiate combat for the provided guild
		Input: String with Guild ID
	*/
	private void startCombat(String guildId) {
		Guild guild = discord.getGuildById(guildId);

		if (combats.containsKey(guild.getId())) {
			log.warn("Combat already exists for guild {}! Ignoring...", guild.getName());
			return;
		}

		List<TextChannel> channels = getEligibleChannels(guild);
		// Channel for original post
		TextChannel channel = channels.get(RAND.nextInt(channels.size()));

		// Channel for combat
		Set<String> combatChannels = service.getGuildSettings(guild.getId())
			.getCombatChannels();
		TextChannel combatChannel;
		if (combatChannels.isEmpty()) {
			combatChannel = channel;
		} else {
			int index = RAND.nextInt(combatChannels.size());
			Iterator<String> iter = combatChannels.iterator();
			for (int i = 0; i < index; i++) {
				iter.next();
			}
			combatChannel = guild.getTextChannelById(iter.next());

			String grindingRoleMention = guild.getRolesByName("Grinding For XP", true)
				.stream()
				.findAny()
				.map(Role::getAsMention)
				.orElse("grinding for XP");

			channel.sendMessage("Attention to those " + grindingRoleMention
				+ " - Combat beginning in " + combatChannel.getAsMention() + "!")
				.queue((message) -> schedule(
				() -> deleteMessage(message),
				COMBAT_ROUND_SECONDS));
		}

		// TODO: Figure out how "active" the channel is to determine the size of the mob
		// Time-based EWMA of number of chat messages
		Monster monster = Monster.fromTemplate(service.getRandomMonster());

		Combat combat = new Combat();
		combat.setGuild(guild);
		combat.setMonster(monster);

		combats.put(guild.getId(), combat);

		combatChannel.sendMessage("Something threatening looms nearby...")
			.queue(
				(message) -> {
					combat.setMessage(message);
					combatRound(combat);
				},
				(error) -> {
					log.warn("Couldn't start combat for Guild {}! Trying again...", guild.getName(), error);
					combat.getMessage().delete().queue();
				});
	}

   	/*
		Method: combatRound
		Description: Handle the round for current combat
		Input: Combat object
	*/
	private void combatRound(Combat combat) {
		synchronized (combat) {
			Message prevMessage = combat.getMessage();
			MessageEmbed embed = getCombatEmbed(combat);
			prevMessage.getChannel()
				.sendMessage(embed)
				.queue(
					(newMessage) -> {
						combat.setMessage(newMessage);
						deleteMessage(prevMessage);
						newMessage.addReaction(ATTACK_EMOJI).queue();
						newMessage.addReaction(SHOOT_EMOJI).queue();
						newMessage.addReaction(BOLT_EMOJI).queue();
						combatSchedule(
							prevMessage.getGuild().getId(),
							() -> nextCombatRound(combat),
							COMBAT_ROUND_SECONDS,
							() -> {
								log.warn("Failed to run next combat round! Abandoning combat and rescheduling...");
								scheduleCombat(prevMessage.getGuild());
							});
					},
					(error) -> {
						log.warn("Error trying to manage a combat round! Trying again...", error);
						combatRound(combat);
					});
		}
	}

   	/*
		Method: getCombatEmbed
		Description: Generate message to embed into combat channel, complete round results
		Input: Combat object
	*/
	private MessageEmbed getCombatEmbed(Combat combat) {
		Monster monster = combat.getMonster();
		String combatants = combat.getCurrentRound()
			.getAttacks()
			.values()
			.stream()
			.map(Attack::getCombatantString)
			.collect(joining("\n"));
		int meleeCharge = monster.getDefenseCharge(ATTACK);
		int shootCharge = monster.getDefenseCharge(SHOOT);
		int boltCharge = monster.getDefenseCharge(BOLT);

		String meleeDef = monster.hasShield(ATTACK) ? "**Melee**" : "Melee";
		String rangedDef = monster.hasShield(SHOOT) ? "**Ranged**" : "Ranged";
		String magicDef = monster.hasShield(BOLT) ? "**Magic**" : "Magic";
		String defString = new StringJoiner("\n")
			.add(meleeDef)
			.add(rangedDef)
			.add(magicDef)
			.toString();

		MessageEmbed embed = new EmbedBuilder()
			.setTitle(monster.getName())
			.setDescription("**Round " + combat.getCurrentRound().getNumber() + "**\n"
				+ "Join the battle with \".strike\", \".shoot\", \".bolt\", or one of the emoji below!")
			.setColor(Color.RED)
			.addField("HP", monster.getCurrentHp() + "/" + monster.getMaxHp(), true)
			.addField("Defense", defString, true)
			.addField("Charge", meleeCharge + "\n" + shootCharge + "\n" + boltCharge, true)
			.addField("Previous Round", combat.getLastRoundText(), false)
			.addField("Combatants", combatants, false)
			.setTimestamp(Instant.now())
			.build();
		return embed;
	}

   	/*
		Method: nextCombatRound
		Description: Handle combat for next round
		Input: Combat object
	*/
	private void nextCombatRound(Combat combat) {
		log.info("Resolving combat in {} round {}", combat.getGuild().getName(), combat.getCurrentRound().getNumber());
		synchronized (combat) {
			combat.resolveRound();
		}

		if (combat.getMonster().isDead()) {
			endCombat(combat);
		} else if (combat.getIgnoredRounds() > 9) {
			// Run away after 5 uninteracted minutes
			escapeCombat(combat);
		} else {
			combatRound(combat);
		}
	}

   	/*
		Method: endCombat
		Description: Handles end of combat
		Input: Combat object
	*/
	private void endCombat(Combat combat) {
		synchronized (combat) {
			Message prevMessage = combat.getMessage();
			Guild guild = prevMessage.getGuild();
			Monster monster = combat.getMonster();
			combats.remove(guild.getId());

			Set<String> levelups = service.awardXp(combat.getParticipants().values(), monster.getXp());
			String combatants = combat.getParticipants()
				.keySet()
				.stream()
				.map((memId) -> Optional.of(memId)
					.map(guild::getMemberById)
					.map((member) -> {
						String memberId = member.getUser().getId();
						if (levelups.contains(memberId)) {
							return member.getEffectiveName() + " **LEVEL UP**";
						}
						return member.getEffectiveName();
					})
					.orElseGet(() -> service.getCharacter(memId).getName()))
				.collect(joining("\n"));

			resetCharHp(combat);

			MessageEmbed embed = new EmbedBuilder()
				.setTitle(monster.getName())
				.setDescription("**DEFEATED**")
				.addField("Previous Round", combat.getLastRoundText(), false)
				.addField("Rewards", monster.getXp() + " XP earned!", false)
				.addField("Combatants", combatants, false)
				.setColor(Color.GREEN)
				.setTimestamp(Instant.now())
				.build();
			prevMessage.getChannel()
				.sendMessage(embed)
				.queue((newMessage) ->
					schedule(() -> newMessage.delete().queue(), COMBAT_END_COOLDOWN));
			deleteMessage(prevMessage);
			scheduleCombat(combat.getGuild());
		}
	}

   	/*
		Method: escapeCombat
		Description: Handle monster escaping combat
		Input: Combat object
	*/
	private void escapeCombat(Combat combat) {
		synchronized (combat) {
			Message prevMessage = combat.getMessage();
			Monster monster = combat.getMonster();
			combats.remove(prevMessage.getGuild().getId());
			resetCharHp(combat);

			MessageEmbed embed = new EmbedBuilder()
				.setTitle(monster.getName())
				.setDescription("**ESCAPED**")
				.setColor(Color.YELLOW)
				.setTimestamp(Instant.now())
				.build();

			prevMessage.getChannel()
				.sendMessage(embed)
				.queue((newMessage) ->
					schedule(() -> newMessage.delete().queue(), COMBAT_END_COOLDOWN));
			deleteMessage(prevMessage);
			scheduleCombat(combat.getGuild());
		}
	}

   	/*
		Method: cancelCombat
		Description: Stops combat
		Input: String with Guild ID
	*/
	public void cancelCombat(String guildId) {
		var combat = combats.remove(guildId);
		if (combat != null) {
			resetCharHp(combat);

			combatFutures.remove(guildId).cancel(false);
			log.info("Canceled combat for guild {}", discord.getGuildById(guildId).getName());
		}
	}

   	/*
		Method: resetCharHp
		Description: Restores every combat participant to full HP
		Input: Combat object
	*/
	private void resetCharHp(Combat combat) {
		combat.getParticipants()
			.values()
			.forEach((character) -> character.setHpCurrent(character.getHpMax()));
	}

   	/*
		Method: forceCombat
		Description: Stops current combat, then start a new combat
		Input: String with Guild ID
	*/
	public void forceCombat(String guildId) {
		cancelCombat(guildId); // Prevent double-scheduling
		startCombat(guildId);
	}

	/******************
	 * COMMAND HELPERS
	 *****************/
   	/*
		Method: getCombat
		Description: Retrieve combat for current member
		Input: Member object
		Output: Combat object
	*/
	public Combat getCombat(Member member) {
		return combats.get(member.getGuild().getId());
	}

   	/*
		Method: getMember
		Description: Retrieve member object for current user
		Input: User object
		Output: Member object
	*/
	public Member getMember(User user) {
		return user.getMutualGuilds()
			.stream()
			.map((guild) -> guild.getMemberById(user.getId()))
			.findAny()
			.orElse(null);
	}

   	/*
		Method: updateCombatMessage
		Description: Alter combat message with update
		Input: Combat object
	*/
	public void updateCombatMessage(Combat combat) {
		synchronized (combat) {
			MessageEmbed combatEmbed = getCombatEmbed(combat);
			combat.getMessage()
				.editMessage(combatEmbed)
				.queue();
		}
	}

   	/*
		Method: getBaseUrl
		Description: Return base URL
		Output: String with baseURL
	*/
	public String getBaseUrl() {
		return baseUrl;
	}

	/******************
	 * API HELPERS
	 *****************/
	/*
		Method: isAdmin
		Description: Determines if Member is an admin
		Input: Member object
		Output: Boolean on if member is an admin
	*/
	public boolean isAdmin(Member member) {
		return member.getRoles()
			.stream()
			.anyMatch((role) -> role.hasPermission(ADMINISTRATOR));
	}

	/*
		Method: getUser
		Description: Retrieve user based on UID
		Input: String with UID
		Output: User object
	*/
	public User getUser(String uid) {
		return discord.getUserById(uid);
	}

	/*
		Method: getAdminGuilds
		Description: Retrieve any admin guilds associated with current User
		Input: User object
		Output: Mapping with related Admin Guilds
	*/
	public Map<String, Guild> getAdminGuilds(User user) {
		return user.getMutualGuilds()
			.stream()
			.filter((guild) -> isAdmin(guild.getMember(user)))
			.collect(toMap(Guild::getId, identity()));
	}

	/*
		Method: getNextCombats
		Description: Retrieve any combats upcoming for the provided Guilds
		Input: Collection of Guild objects
		Output: Mapping with related combats
	*/
	public Map<String, String> getNextCombats(Collection<Guild> guilds) {
		return guilds.stream()
			.map(Guild::getId)
			.collect(toMap(
				identity(),
				(gid) -> ISO_DATE_TIME.format(Instant.now()
					.plusMillis(combatFutures.get(gid).getDelay(MILLISECONDS))
					.atZone(UTC))));
	}

	/*
		Method: register
		Description: Registers current user to the game
		Input: String with User ID
	*/
	public void register(String userId) {
		runCommand(singletonList(".register"), null, getUser(userId), null);
	}

	/*
		Method: scheduleGrinding
		Description: Schedules time grinding for user
		Input: Member object, Integer with hours for grinding
	*/
	public void scheduleGrinding(Member member, int numHours) {
		schedule(() -> member.getRoles()
			.stream()
			.filter((role) -> role.getName().equalsIgnoreCase("Grinding For XP"))
			.forEach((role) -> member.getGuild()
			.getController()
			.removeSingleRoleFromMember(member, role)
			.queue()),
			(int) Duration.ofHours(numHours).toSeconds());
	}
}
