package com.titaniumtemplar.discordbot.discord;

import static java.util.stream.Collectors.toList;
import static net.dv8tion.jda.core.Permission.MESSAGE_READ;
import static net.dv8tion.jda.core.Permission.MESSAGE_WRITE;
import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.DSLContext;

import com.titaniumtemplar.discordbot.discord.commands.AttackCommand;
import com.titaniumtemplar.discordbot.discord.commands.DiscordCommand;
import com.titaniumtemplar.discordbot.discord.commands.HelpCommand;
import com.titaniumtemplar.discordbot.discord.commands.ProfileCommand;
import com.titaniumtemplar.discordbot.discord.commands.RegisterCommand;
import com.titaniumtemplar.discordbot.discord.commands.RoleCommand;
import com.titaniumtemplar.discordbot.discord.commands.SkillsCommand;
import com.titaniumtemplar.discordbot.discord.commands.UnknownCommand;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Myra extends ListenerAdapter {

  //<editor-fold defaultstate="collapsed" desc="Static fields">
  private static final int COMBAT_ROUND_SECONDS = 30;
//</editor-fold>


  //<editor-fold defaultstate="collapsed" desc="Injected Fields">
  private final DSLContext dslContext; // TODO: Replace with a Service
  private final ScheduledExecutorService combatThreadPool;
//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Members">
  private final Map<String, Guild> guildMap = new HashMap<>();
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
  }

  private List<Channel> getPublicChannels(Guild guild)
  {
    Member myraMember = guild.getSelfMember();
    return guild.getChannels()
        .stream()
        .filter((channel) -> channel.getType() == TEXT)
        .peek((channel) -> log.info("Checking channel {} with permissions {} for permissions", channel.getName(), channel.getPermissionOverrides()))
        .filter((channel) -> myraMember.hasPermission(channel, MESSAGE_READ, MESSAGE_WRITE))
        .peek((channel) -> log.info("Adding channel {}", channel.getName()))
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

    String[] splitCommand = content.split(" ");

    commands.getOrDefault(splitCommand[0], UnknownCommand::withArgs)
	.apply(splitCommand)
	.run(message, author, member);
  }
}
