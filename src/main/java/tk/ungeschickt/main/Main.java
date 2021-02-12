package tk.ungeschickt.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tk.ungeschickt.events.ChangeName;
import tk.ungeschickt.events.Command_test;
import tk.ungeschickt.timedEvents.replacementPlanTimer;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Timer;

//import tk.ungeschickt.timedEvents.replacementPlanTimer;

// TODO: Embeded Messages, Log4j or something
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

    public static String reset = (char) 27 + "[0m";
    public static String bold = (char) 27 + "[1m";
    public static String dim = (char) 27 + "[2m";
    public static String underline = (char) 27 + "[4m";
    public static String blink = (char) 27 + "[5m";
    public static String defaultColor = (char) 27 + "[39m";

    public static String red = (char) 27 + "[31m";
    public static String green = (char) 27 + "[32m";
    public static String yellow = (char) 27 + "[33m";
    public static String blue = (char) 27 + "[34m";
    public static String magenta = (char) 27 + "[35m";
    public static String cyan = (char) 27 + "[36m";
    public static String lgray = (char) 27 + "[37m";
    public static String dgray = (char) 27 + "[90m";
    public static String lred = (char) 27 + "[91m";
    public static String lgreen = (char) 27 + "[92m";
    public static String lyellow = (char) 27 + "[93m";
    public static String lblue = (char) 27 + "[94m";
    public static String lmagenta = (char) 27 + "[95m";
    public static String lcyan = (char) 27 + "[96m";
    public static String white = (char) 27 + "[97m";

    //TODO: OnMemberJoin ask username and change nickname accordingly
    public static void main(String[] args) throws LoginException, FileNotFoundException, InterruptedException/*, ParseException*/ {
        setInfo(Info.getInstance());
        getInfo().setPrefix("!");
        //Logger logger = new Logger(Main.class.getSimpleName(), new Object() {}.getClass().getEnclosingMethod().getName());
        JDABuilder builder = JDABuilder.createDefault(getInfo().getBotToken());
        builder.addEventListeners(new Command_test(getInfo()));
        builder.addEventListeners(new ChangeName(getInfo()));
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.disableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        builder.setActivity(Activity.watching("timetable and homework"));
        setJda(builder.build());

        getJda().awaitReady();
        getInfo().setDebugChannel(getJda().getTextChannelById(788525279236194345L));
        getInfo().setVerified(getJda().getRoleById(788520326975062037L));
        //logger.logDebug("test", "Bot");

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
                if (command[0].equalsIgnoreCase("exit"))
                    System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
