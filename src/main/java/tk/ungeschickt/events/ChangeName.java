package tk.ungeschickt.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tk.ungeschickt.main.Info;

import java.util.ArrayList;

public class ChangeName extends ListenerAdapter {

    private final Info info;
    private final ArrayList<String> users = new ArrayList<>();

    public ChangeName(Info info) {
        this.info = info;
        /*if (info.isDebug())
            users.add("411561352201764866");*/
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        users.add(e.getMember().getUser().getId());
        if (info.isDebug())
            System.out.println("" + e.getMember().getUser().getId());
        TextChannel nameChangeChannel = e.getGuild().getTextChannelById(787711202993242122L);
        if (nameChangeChannel == null) {
            System.out.println("Warning in function onGuildMemberJoin in class " + this.getClass().getName() + " .getTextChannelById(787711202993242122L) returned null!\nReturning from function.");
            info.getDebugChannel().sendMessage("Warning in function onGuildMemberJoin in class " + this.getClass().getName() + " .getTextChannelById(787711202993242122L) returned null!\nReturning from function.").queue();
            return;
        }
        nameChangeChannel.sendMessage("Hallo " + e.getUser().getAsMention() + "!\nBitte gebe deinen Namen an.").queue();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;
        Member member = e.getMember();
        if (member == null) {
            System.out.println("Warning in function onGuildMessageReceived in class " + this.getClass().getName() + " e.getMember() returned null!\nReturning from function.");
            info.getDebugChannel().sendMessage("Warning in function onGuildMessageReceived in class " + this.getClass().getName() + " e.getMember() returned null!\nReturning from function.").queue();
            return;
        }
        if (users.contains(e.getMember().getUser().getId())) {
            TextChannel nameChangeChannel = e.getGuild().getTextChannelById(787711202993242122L);
            if (nameChangeChannel == null) {
                System.out.println("Warning in function onGuildMessageReceived in class " + this.getClass().getName() + " .getTextChannelById(787711202993242122L) returned null!\nReturning from function.");
                info.getDebugChannel().sendMessage("Warning in function onGuildMessageReceived in class " + this.getClass().getName() + " .getTextChannelById(787711202993242122L) returned null!\nReturning from function.").queue();
                return;
            }

            e.getGuild().addRoleToMember(e.getMember(), info.getVerified()).queue();
            nameChangeChannel.sendMessage("Dein name wurde zu Schüler | " + e.getMessage().getContentRaw() + " umbenannt!").queue();
            member.modifyNickname("Schüler | " + e.getMessage().getContentRaw()).queue();
            users.remove(e.getMember().getUser().getId());
        }
    }
}
