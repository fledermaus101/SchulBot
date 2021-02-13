package tk.ungeschickt.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.ungeschickt.events.ChangeName;
import tk.ungeschickt.events.Command_test;
import tk.ungeschickt.timedEvents.replacementPlanTimer;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Calendar;
import java.util.Timer;

//import tk.ungeschickt.timedEvents.replacementPlanTimer;

// TODO: Embeded Messages
// Credits: the internet
public class Main {
    public static JDA getJda() {
        return jda;
    }

    public static void setJda(JDA jda) {
        Main.jda = jda;
    }

    private static JDA jda;

    public static Info getInfo() {
        return info;
    }

    private static void setInfo(Info info) {
        Main.info = info;
    }

    private static Info info;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    //TODO: OnMemberJoin ask username and change nickname accordingly
    public static void main(String[] args) throws LoginException, InterruptedException, ParseException, IOException {
        setInfo(Info.getInstance());
        getInfo().setPrefix("!");
        logger.trace("Set Info info");
        //Logger logger = new Logger(Main.class.getSimpleName(), new Object() {}.getClass().getEnclosingMethod().getName());
        JDABuilder builder = JDABuilder.createDefault(getInfo().getBotToken());
        logger.trace("Created JDABuilder");
        builder.addEventListeners(new Command_test(getInfo()));
        builder.addEventListeners(new ChangeName());
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.disableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        logger.trace("Configured caches and intents");
        builder.setActivity(Activity.watching("timetable and homework"));
        setJda(builder.build());
        logger.debug("Set var JDA");

        getJda().awaitReady();
        getInfo().setDebugChannel(getJda().getTextChannelById(788525279236194345L));

        //logger.logDebug("test", "Bot");

        logger.trace("Setup timed events");
        Calendar date = Calendar.getInstance();
        date.set(
                Calendar.DAY_OF_WEEK,
                Calendar.MONDAY
        );
        // 8 14 20
        date.set(Calendar.HOUR, 8);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Timer timer = new Timer();
        int period = 86400000; //1 day
        timer.schedule(new replacementPlanTimer(info), date.getTime(), period);
        date.set(Calendar.HOUR, 14);
        timer.schedule(new replacementPlanTimer(info), date.getTime(), period);
        date.set(Calendar.HOUR, 20);
        timer.schedule(new replacementPlanTimer(info), date.getTime(), period);
        logger.debug("Finished setup timed events");

        logger.trace("Start interactive commandline");
        logger.info("Startup completed!");
        if (System.in == null)
            return;
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String str = br.readLine();
                if (str == null)
                    continue;
                String[] command = str.split(" ");
                if (command.length == 0)
                    continue;
                if (command[0].equalsIgnoreCase("exit") || command[0].equalsIgnoreCase("quit"))
                    System.exit(0);
            } catch (IOException e) {
                logger.warn("Tried to read from stdin", e);
            }
        }
    }
}
