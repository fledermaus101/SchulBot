package tk.ungeschickt.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.ungeschickt.main.Info;

public class Command_test extends ListenerAdapter {

    private final Info info;

    public Command_test(Info info) {
        this.info = info;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!(e.getAuthor().isBot())) {
            if (e.getMessage().getContentRaw().equalsIgnoreCase(info.getPrefix() + "test")) {
                e.getMessage().getTextChannel().sendMessage("test!").queue();
            }
        }
    }
}
