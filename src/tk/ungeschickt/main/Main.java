package tk.ungeschickt.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.ungeschickt.commands.Command_test;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO: Embeded Messages
public class Main extends ListenerAdapter {
    private static JDA jda;

    public static void main(String[] args) throws LoginException, FileNotFoundException {
        Info info = new Info("!");
        String secretEnv = System.getenv("secret");
        if (secretEnv == null || secretEnv.equals("")) {
            jda = JDABuilder.createDefault(info.getBotToken()).build();
        } else
            jda = JDABuilder.createDefault(secretEnv).build();

        jda.addEventListener(new Command_test(info));

        /*jda.addEventListener(new CommandClear(info));
        jda.addEventListener(new OnReaction(info));*/

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String[] command = br.readLine().split("");
                if (command[0].equalsIgnoreCase("exit"))
                    System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
