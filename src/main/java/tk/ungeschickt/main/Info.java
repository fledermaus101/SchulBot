package tk.ungeschickt.main;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.Scanner;

public class Info {
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String prefix;

    public String getBotToken() {
        return botToken;
    }

    private final String botToken;
    private final String websiteUsername;
    private final String websitePassword;

    public Role getVerified() {
        return verified;
    }

    public void setVerified(Role verified) {
        this.verified = verified;
    }

    private Role verified;

    public TextChannel getDebugChannel() {
        return debugChannel;
    }

    public void setDebugChannel(TextChannel debugChannel) {
        this.debugChannel = debugChannel;
    }

    private TextChannel debugChannel;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private boolean debug = true;

    public static Info getInstance() throws FileNotFoundException {
        if (instance == null) {
            return new Info();
        } else return instance;
    }

    public static void setInstance(Info instance) {
        Info.instance = instance;
    }

    private static Info instance;

    private Info() throws FileNotFoundException {
        String botToken1 = System.getenv("botToken");
        String websiteUsername1 = System.getenv("webUsername");
        String websitePassword1 = System.getenv("webPassword");

        boolean botToken1Empty = botToken1 == null;
        if (botToken1 != null) botToken1Empty = botToken1.isEmpty();
        boolean webUsername1Empty = websiteUsername1 == null;
        if (websiteUsername1 != null)
            webUsername1Empty = websiteUsername1.isEmpty();
        boolean webPassword1Empty = websitePassword1 == null;
        if (websitePassword1 != null)
            webPassword1Empty = websitePassword1.isEmpty();
        if (botToken1Empty || webUsername1Empty || webPassword1Empty) {
            File file = new File(String.valueOf(FileSystems.getDefault().getPath("secrets.txt")));
            if (file.exists()) {
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(new FileReader(file));
                JSONObject jsonObject = (JSONObject) obj;
                logger.trace("Created JSONObject");

                botToken1 = (String) jsonObject.get("botToken");
                websiteUsername1 = (String) jsonObject.get("webUsername");
                websitePassword1 = (String) jsonObject.get("webPassword");
            } else
                throw new FileNotFoundException("secrets.txt not found.");
        }
        assert botToken1 != null;
        assert websiteUsername1 != null;
        assert websitePassword1 != null;
        if (botToken1.equals("") || websiteUsername1.equals("") || websitePassword1.equals(""))
            throw new RuntimeException("Secrets are empty. Please write those in secret.json.");

        this.botToken = botToken1;
        this.websiteUsername = websiteUsername1;
        this.websitePassword = websitePassword1;
        setInstance(this);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getWebsiteUsername() {
        return websiteUsername;
    }

    public String getWebsitePassword() {
        return websitePassword;
    }
}
