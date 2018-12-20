package com.titaniumtemplar.discordbot.discord;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static net.dv8tion.jda.core.Permission.MESSAGE_READ;
import static net.dv8tion.jda.core.Permission.MESSAGE_WRITE;
import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import com.titaniumtemplar.discordbot.discord.commands.AttackCommand;
import com.titaniumtemplar.discordbot.discord.commands.DiscordCommand;
import com.titaniumtemplar.discordbot.discord.commands.HelpCommand;
import com.titaniumtemplar.discordbot.discord.commands.ProfileCommand;
import com.titaniumtemplar.discordbot.discord.commands.RegisterCommand;
import com.titaniumtemplar.discordbot.discord.commands.RoleCommand;
import com.titaniumtemplar.discordbot.discord.commands.SkillsCommand;
import com.titaniumtemplar.discordbot.discord.commands.UnknownCommand;
import com.titaniumtemplar.discordbot.model.combat.Attack;
import com.titaniumtemplar.discordbot.model.combat.Combat;
import com.titaniumtemplar.discordbot.model.monster.Monster;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static com.titaniumtemplar.discordbot.model.combat.Attack.AttackType.ATTACK;
import static com.titaniumtemplar.discordbot.model.combat.Attack.AttackType.BOLT;
import static com.titaniumtemplar.discordbot.model.combat.Attack.AttackType.SHOOT;
import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Myra extends ListenerAdapter {

  //<editor-fold defaultstate="collapsed" desc="Static fields">
  private static final int COMBAT_ROUND_SECONDS = 10; //30;
  private static final int COMBAT_END_COOLDOWN = 5; //120;
  private static final int COMBAT_WAIT_LOWER = 3; //300;
  private static final int COMBAT_WAIT_UPPER = 6; //3600;
  private static final Random RAND = new Random();
//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Injected Fields">
  private final CyberscapeService service;
  private final ScheduledExecutorService combatThreadPool;
//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Members">
  private final Map<String, Guild> guildMap = new HashMap<>();
  private final Map<String, Combat> combats = new HashMap<>();
  private final Map<String, Function<? super String[], ? extends DiscordCommand>> commands = new HashMap<>();
//</editor-fold>

  @PostConstruct
  private void setup() {
    commands.put(".register", RegisterCommand::withArgs);
    commands.put(".profile", ProfileCommand::withArgs);
    commands.put(".skills", SkillsCommand::withArgs);
    commands.put(".help", HelpCommand::withArgs);
    commands.put(".role", RoleCommand::withArgs);
    commands.put(".attack", AttackCommand::withArgs);
    commands.put(".shoot", AttackCommand::withArgs);
    commands.put(".bolt", AttackCommand::withArgs);
  }

  @PreDestroy
  private void destroy() {
    combatThreadPool.shutdownNow();
  }

  private void joinGuild(Guild guild) {
    guildMap.put(guild.getId(), guild);
    scheduleCombat(guild);
  }

  private List<TextChannel> getEligibleChannels(Guild guild)
  {
    Member myraMember = guild.getSelfMember();
    return guild.getChannels()
        .stream()
        .filter((channel) -> channel.getType() == TEXT)
        .map((channel) -> (TextChannel) channel)
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
    guildMap.remove(event.getGuild().getId());
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    joinGuild(event.getGuild());
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    Member member = event.getMember();
    if (member.getUser().isBot())
      return;

    Guild guild = event.getGuild();

    guild.getController()
	.addRolesToMember(
	    member, guild.getRolesByName("In Character Select", false))
	.queue();
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
    String content = message.getContentStripped();

    if (!content.startsWith("."))
      return;

    // TODO: Commands for "combatRoom" and "announceCombatRoom"
    String[] splitCommand = content.split(" ");

    commands.getOrDefault(splitCommand[0], UnknownCommand::withArgs)
	.apply(splitCommand)
	.run(service, this, message, author, member);
  }

  /**********
   * COMBAT
   *********/
  private void scheduleCombat(Guild guild) {
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
    // TODO

    // TODO: Figure out how "active" the channel is to determine the size of the mob
    // Time-based EWMA of number of chat messages
    Monster monster = Monster.fromTemplate(service.getRandomMonster());

    Combat combat = new Combat();
    combat.setGuild(guild);
    combat.setMonster(monster);

    combats.put(guild.getId(), combat);

    channel.sendMessage("Something threatening looms nearby...")
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
	  .queue((newMessage) -> {
	    combat.setMessage(newMessage);
	    prevMessage.delete().queue();
	    combatThreadPool.schedule(
		() -> nextCombatRound(combat),
		COMBAT_ROUND_SECONDS,
		SECONDS);
	  });
    }
  }

  private MessageEmbed getCombatEmbed(Combat combat) {
    Monster monster = combat.getMonster();
    String combatants = combat.getCurrentRound()
	.getAttacks()
	.stream()
	.map(Attack::getCombatantString)
	.collect(joining("\n"));
    int meleeCharge = monster.getDefenseCharge(ATTACK);
    int shootCharge = monster.getDefenseCharge(SHOOT);
    int boltCharge = monster.getDefenseCharge(BOLT);
    MessageEmbed embed = new EmbedBuilder()
	.setTitle(monster.getName())
	.setDescription("**Round " + combat.getCurrentRound().getNumber() + "**\n"
	    + "Join the battle with \".attack\", \".shoot\", or \".bolt\"!")
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
    combat.resolveRound();
    if (combat.getMonster().isDead()) {
      endCombat(combat);
    } else if (combat.getCurrentRound().getNumber() > 1 &&
	combat.getCurrentRound().isEmpty() &&
	combat.getPreviousRound().isEmpty()) {
      // Run away after 2 consecutive noninteracted rounds
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
      String combatants = combat.getParticipantUids()
	  .stream()
	  .map(guild::getMemberById)
	  .filter(Objects::nonNull)
	  .map(Member::getEffectiveName)
	  .collect(joining("\n"));
      service.awardXp(combat.getParticipantUids(), monster.getXp());
      // TODO: Print out characters who leveled up
      // TODO: When do they level up..?
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
	  .queue((newMessage) -> {
	    prevMessage.delete().queue();
	    combatThreadPool.schedule(() -> newMessage.delete().queue(), COMBAT_END_COOLDOWN, SECONDS);
	  });
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
	  .queue((newMessage) -> {
	    prevMessage.delete().queue();
	    combatThreadPool.schedule(() -> newMessage.delete().queue(), COMBAT_END_COOLDOWN, SECONDS);
	  });
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
}
