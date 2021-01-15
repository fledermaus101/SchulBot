package tk.ungeschickt.main;

import net.dv8tion.jda.api.EmbedBuilder;

public class Logger {

    private final String className;
    private final String methodName;

    public Logger(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public void logDebug(String msg, String executor) {
        final Info info = Main.getInfo();
        if (info.isDebug()) {
            String template = Main.dgray + "[" + Main.blue + "Debug" + Main.dgray + "] Class " + Main.lgray + "%s" + Main.dgray + ": Method " + Main.lgray + "%s" + Main.dgray + ":" + Main.blue + " %s";
            System.out.printf((template) + "%n", className, methodName, msg);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(13019674);
            builder.setDescription(msg);
            builder.setFooter("Executed by " + executor);

            info.getDebugChannel().sendMessage(builder.build()).queue();
        }
    }

    public void logDebug(String msg) {
        if (Main.getInfo().isDebug()) {
            String template = Main.dgray + "[" + Main.blue + "Debug" + Main.dgray + "] Class " + Main.lgray + "%s" + Main.dgray + ": Method " + Main.lgray + "%s" + Main.dgray + ":" + Main.blue + " %s";
            System.out.printf((template) + "%n", className, methodName, msg);
        }
    }
}