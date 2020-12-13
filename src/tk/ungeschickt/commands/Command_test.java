package tk.ungeschickt.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;
import tk.ungeschickt.main.Info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                try {
                    String cookie = getCookie();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }


        }
    }

    @Nullable
    public String getCookie() throws IOException {
        URL url = new URL("http://www.friedrichgymnasium-altenburg.de/interne-bereiche");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
        connection.setRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while (true) {
            String input = in.readLine();
            if (input == null) {
                break;
            }
            // Searching for the form where the cookie lies
            Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"([a-z]|[0-9]){32}\" value=\"1\" />", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                System.out.println("found: " + matcher.group());
                Pattern pattern2 = Pattern.compile("([a-z]|[0-9]){32}", Pattern.CASE_INSENSITIVE);
                Matcher matcher2 = pattern2.matcher(matcher.group());
                if (matcher2.find()) {
                    // Getting the value idk for what it is used - Current guess a session cookie
                    // But we need it to authenticate
                    System.out.println("Got token: " + matcher2.group());
                    return matcher2.group();
                }
                break;
            }
        }

        //in.close();
        //connection.disconnect();
        //connection.setRequestProperty("REFERER", "http://www.friedrichgymnasium-altenburg.de/interne-bereiche");
        /*String body = "";
        byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
        OutputStream os = connection.getOutputStream();
        os.write(outputInBytes);
        os.close();*/
        return null;
    }

    // TODO: Authenticate
    private void authenticate() {

    }
}
