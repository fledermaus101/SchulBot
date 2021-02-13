package tk.ungeschickt.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.api.EmbedBuilder;
import tk.ungeschickt.main.Main;

public class DiscordChannelAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(final ILoggingEvent eventObject) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(13019674);
        builder.setDescription(eventObject.getFormattedMessage());
        builder.setFooter("Logged by Class: " + eventObject.getThreadName());

        Main.getInfo().getDebugChannel().sendMessage(builder.build()).queue();
    }
}
