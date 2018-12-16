package com.titaniumtemplar.discordbot.discord;

import com.titaniumtemplar.discordbot.discord.commands.AttackCommand;
import com.titaniumtemplar.discordbot.discord.commands.DiscordCommand;
import com.titaniumtemplar.discordbot.discord.commands.HelpCommand;
import com.titaniumtemplar.discordbot.discord.commands.ProfileCommand;
import com.titaniumtemplar.discordbot.discord.commands.RegisterCommand;
import com.titaniumtemplar.discordbot.discord.commands.RoleCommand;
import com.titaniumtemplar.discordbot.discord.commands.SkillsCommand;
import com.titaniumtemplar.discordbot.discord.commands.UnknownCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jooq.DSLContext;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Myra extends ListenerAdapter {

  private final DSLContext dslContext;

  private Map<String, Guild> guildMap = new HashMap<>();
  private Map<String, List<Channel>> channelsByGuild;

  private Map<String, Function<? super String[], ? extends DiscordCommand>> commands = new HashMap<>();

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

  @Override
  public void onReady(ReadyEvent event) {
    channelsByGuild = event.getJDA()
	.getGuilds()
	.stream()
	.peek((guild) -> guildMap.put(guild.getId(), guild))
	.flatMap((guild) -> guild.getChannels()
	    .stream())
	.filter((channel) -> channel.getType() == TEXT)
	// TODO: filter by public channels
	.peek((channel) -> log.info("Adding channel {} with permissions {}", channel.getName(), channel.getPermissionOverrides()))
	.collect(groupingBy((channel) -> channel.getGuild().getId()));

    log.info("OnReady. Guilds {} Channels {}", guildMap.size(), channelsByGuild.size());
  }

  @Override
  public void onGuildLeave(GuildLeaveEvent event) {
    guildMap.remove(event.getGuild().getId());
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    Guild guild = event.getGuild();
    String id = guild.getId();
    guildMap.put(id, guild);
    List<Channel> channels = guild.getChannels()
	.stream()
	.filter((channel) -> channel.getType() == TEXT)
	.collect(toList());
    channelsByGuild.put(id, channels);
  }

  @Override
  public void onTextChannelCreate(TextChannelCreateEvent event) {
    channelsByGuild.get(event.getGuild().getId()).add(event.getChannel());
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
