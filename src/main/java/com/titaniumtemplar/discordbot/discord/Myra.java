package com.titaniumtemplar.discordbot.discord;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.ATTACK;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.BOLT;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.SHOOT;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.dv8tion.jda.core.Permission.MESSAGE_READ;
import static net.dv8tion.jda.core.Permission.MESSAGE_WRITE;
import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

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
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
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

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Myra extends ListenerAdapter {

	//<editor-fold defaultstate="collapsed" desc="Static fields">
	private static final int COMBAT_ROUND_SECONDS = 10;
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
	private final ScheduledExecutorService combatThreadPool;
	private final String baseUrl;
//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Members">
	private final Map<String, Guild> guildMap = new HashMap<>();
	private final Map<String, Combat> combats = new HashMap<>();
	private final Map<String, Function<? super List<String>, ? extends DiscordCommand>> commands = new HashMap<>();
//</editor-fold>

	@PostConstruct
	private void setup() {
		commands.put(".register", RegisterCommand::withArgs);
		commands.put(".profile", ProfileCommand::withArgs);
		commands.put(".skills", SkillsCommand::withArgs);
		commands.put(".help", HelpCommand::withArgs);
		commands.put(".role", RoleCommand::withArgs);
		commands.put(".attack", AttackCommand::withArgs);
		commands.put(".shoot", ShootCommand::withArgs);
		commands.put(".bolt", BoltCommand::withArgs);
		commands.put(".config", ConfigCommand::withArgs);
	}

	@PreDestroy
	private void destroy() {
		combatThreadPool.shutdownNow();
	}

	private void joinGuild(Guild guild) {
		guildMap.put(guild.getId(), guild);
		scheduleCombat(guild);
	}

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

	@Override
	public void onReady(ReadyEvent event) {
		event.getJDA()
			.getGuilds()
			.forEach(this::joinGuild);

		log.info("OnReady. Guilds {}", guildMap.size());
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		String guildId = event.getGuild().getId();
		guildMap.remove(guildId);
		combats.remove(guildId);
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Guild guild = event.getGuild();
		joinGuild(guild);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member member = event.getMember();
		if (member.getUser().isBot()) {
			return;
		}

		Guild guild = event.getGuild();

		guild.getController()
			.addRolesToMember(
				member, guild.getRolesByName("Playing Cyberscape Neo", false))
			.queue();
	}

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

	private List<String> getCommandForEmoji(ReactionEmote emote) {
		switch (emote.getName()) {
			case ATTACK_EMOJI:
				return singletonList(".attack");
			case SHOOT_EMOJI:
				return singletonList(".shoot");
			case BOLT_EMOJI:
				return singletonList(".bolt");
			default:
				return emptyList();
		}
	}

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		handleCommand(event.getMessage(), event.getAuthor(), null);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		handleCommand(event.getMessage(), event.getAuthor(), event.getMember());
	}

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
	private void scheduleCombat(Guild guild) {
		if (combats.containsKey(guild.getId())) {
			log.info("Not scheduling duplicate combat for Guild {}", guild.getName());
		}

		int waitTime = getWaitTime();
		log.info("Scheduling combat for Guild {} in {}s", guild.getName(), waitTime);
		combatThreadPool.schedule(
			() -> startCombat(guild),
			waitTime,
			SECONDS);
	}

	private int getWaitTime() {
		return RAND.nextInt(COMBAT_WAIT_UPPER - COMBAT_WAIT_LOWER) + COMBAT_WAIT_LOWER;
	}

	private void startCombat(Guild guild) {
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

			channel.sendMessage("Combat beginning in " + combatChannel.getAsMention() + "!")
				.queue((message) -> combatThreadPool.schedule(
				() -> message.delete().queue(),
				COMBAT_ROUND_SECONDS,
				SECONDS));
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
						combatThreadPool.schedule(
							() -> nextCombatRound(combat),
							COMBAT_ROUND_SECONDS,
							SECONDS);
					},
					(error) -> {
						log.warn("Error trying to manage a combat round! Trying again...", error);
						combatRound(combat);
					});
		}
	}

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
		MessageEmbed embed = new EmbedBuilder()
			.setTitle(monster.getName())
			.setDescription("**Round " + combat.getCurrentRound().getNumber() + "**\n"
				+ "Join the battle with \".attack\", \".shoot\", \".bolt\", or one of the emoji below!")
			.setColor(Color.RED)
			.addField("HP", monster.getCurrentHp() + "/" + monster.getMaxHp(), true)
			.addField("Defense", "Melee\nRanged\nMagic", true)
			.addField("Charge", meleeCharge + "\n" + shootCharge + "\n" + boltCharge, true)
			.addField("Previous Round", combat.getLastRoundText(), false)
			.addField("Combatants", combatants, false)
			.setTimestamp(Instant.now())
			.build();
		return embed;
	}

	private void nextCombatRound(Combat combat) {
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

	private void endCombat(Combat combat) {
		synchronized (combat) {
			Message prevMessage = combat.getMessage();
			Guild guild = prevMessage.getGuild();
			Monster monster = combat.getMonster();
			combats.remove(prevMessage.getGuild().getId());

			Set<String> levelups = service.awardXp(combat.getParticipantUids(), monster.getXp());
			String combatants = combat.getParticipantUids()
				.stream()
				.map(guild::getMemberById)
				.filter(Objects::nonNull)
				.map((member) -> {
					String memberId = member.getUser().getId();
					if (levelups.contains(memberId)) {
						return member.getEffectiveName() + " **LEVEL UP**";
					}
					return member.getEffectiveName();
				})
				.collect(joining("\n"));

			MessageEmbed embed = new EmbedBuilder()
				.setTitle(monster.getName())
				.setDescription("**DEFEATED**")
				.addField("Rewards", monster.getXp() + " XP earned!", false)
				.addField("Combatants", combatants, false)
				.setColor(Color.GREEN)
				.setTimestamp(Instant.now())
				.build();
			prevMessage.getChannel()
				.sendMessage(embed)
				.queue((newMessage) ->
					combatThreadPool.schedule(() -> newMessage.delete().queue(), COMBAT_END_COOLDOWN, SECONDS));
			deleteMessage(prevMessage);
			scheduleCombat(combat.getGuild());
		}
	}

	private void escapeCombat(Combat combat) {
		synchronized (combat) {
			Message prevMessage = combat.getMessage();
			Monster monster = combat.getMonster();
			combats.remove(prevMessage.getGuild().getId());
			MessageEmbed embed = new EmbedBuilder()
				.setTitle(monster.getName())
				.setDescription("**ESCAPED**")
				.setColor(Color.YELLOW)
				.setTimestamp(Instant.now())
				.build();
			prevMessage.getChannel()
				.sendMessage(embed)
				.queue((newMessage) ->
					combatThreadPool.schedule(() -> newMessage.delete().queue(), COMBAT_END_COOLDOWN, SECONDS));
			deleteMessage(prevMessage);
			scheduleCombat(combat.getGuild());
		}
	}

	/******************
	 * COMMAND HELPERS
	 *****************/

	public Combat getCombat(Member member) {
		return combats.get(member.getGuild().getId());
	}

	public Member getMember(User user) {
		return user.getMutualGuilds()
			.stream()
			.map((guild) -> guild.getMemberById(user.getId()))
			.findAny()
			.orElse(null);
	}

	public void updateCombatMessage(Combat combat) {
		synchronized (combat) {
			MessageEmbed combatEmbed = getCombatEmbed(combat);
			combat.getMessage()
				.editMessage(combatEmbed)
				.queue();
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}
}
