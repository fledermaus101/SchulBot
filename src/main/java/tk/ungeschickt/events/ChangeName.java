package tk.ungeschickt.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

public class ChangeName extends ListenerAdapter {

    private final ArrayList<String> users = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        users.add(e.getMember().getUser().getId());
        if (logger.isInfoEnabled())
            logger.info("Member " + e.getMember().getUser().getId() + " joined.");
        TextChannel nameChangeChannel = e.getGuild().getTextChannelById(787711202993242122L);
        if (nameChangeChannel == null) {
            logger.warn("TextChannel nameChangeChannel is null!");
            return;
        }
        nameChangeChannel.sendMessage("Hallo " + e.getUser().getAsMention() + "!\nBitte gebe deinen Namen an.").queue();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            logger.trace("Received message from a bot. Ignoring.");
            return;
        }
        Member member = e.getMember();
        if (member == null) {
            logger.warn("Member member is null!");
            return;
        }

        if (users.contains(member.getUser().getId())) {
            TextChannel nameChangeChannel = e.getGuild().getTextChannelById(787711202993242122L);
            if (nameChangeChannel == null) {
                logger.warn("TextChannel nameChangeChannel is null!");
                return;
            }

            final String usernamePrefix = "Schüler | ";
            if (e.getMessage().getContentRaw().length() > 32 - usernamePrefix.length()) {
                if (logger.isInfoEnabled())
                    logger.info("Member " + member.getUser().getName() + " has chosen a too long name. Printing error.");
                EmbedBuilder builder = new EmbedBuilder();

                builder.setColor(13019674);
                builder.setDescription("Nickname: " + e.getMessage().getContentRaw() + " ist zu lang!");
                builder.setFooter("@" + member.getUser().getAsTag());

                nameChangeChannel.sendMessage(builder.build()).queue();
            }

            e.getGuild().addRoleToMember(member, Objects.requireNonNull(e.getMessage().getGuild().getRoleById(788520326975062037L))).queue();
            nameChangeChannel.sendMessage("Dein name wurde zu " + usernamePrefix  + e.getMessage().getContentRaw() + " umbenannt!").queue();
            member.modifyNickname(usernamePrefix + e.getMessage().getContentRaw()).queue();
            if (logger.isInfoEnabled())
                logger.info("Member " + member.getUser().getName() + " has successfully chose a name.");
            users.remove(member.getUser().getId());
        }
    }
}
