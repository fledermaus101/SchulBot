package tk.ungeschickt.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tk.ungeschickt.events.ChangeName;
import tk.ungeschickt.events.Command_test;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
    public static void main(String[] args) throws LoginException, FileNotFoundException, InterruptedException {
        setInfo(new Info("!"));
        //Logger.logDebug("test", "Main", "main");
        String secretEnv = System.getenv("secret");
        JDABuilder builder;
        if (secretEnv == null || secretEnv.equals("")) {
            builder = JDABuilder.createDefault(getInfo().getBotToken());
        } else {
            builder = JDABuilder.createDefault(secretEnv);
        }
        builder.addEventListeners(new Command_test(getInfo()));
        builder.addEventListeners(new ChangeName(getInfo()));
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.MEMBER_OVERRIDES);
        builder.disableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        setJda(builder.build());

        getJda().awaitReady();
        getInfo().setDebugChannel(getJda().getTextChannelById(788525279236194345L));
        getInfo().setVerified(getJda().getRoleById(788520326975062037L));

        /*DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date0 = dateFormat.format(cal.getTime());
        Date date1 = dateFormatter.parse(date0 + " 13:00:00");
        Date date2 = dateFormatter.parse(date0 + " 15:00:00");
        Date date3 = dateFormatter.parse(date0 + " 18:00:00");

        Timer timer = new Timer();
        int period = 1000 * 60 * 60 * 24; //1 day
        timer.schedule(new replacementPlanTimer(info), date1, period);
        timer.schedule(new replacementPlanTimer(info), date2, period);
        timer.schedule(new replacementPlanTimer(info), date3, period);*/

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
