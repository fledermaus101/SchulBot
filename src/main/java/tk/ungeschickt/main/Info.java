package tk.ungeschickt.main;

import net.dv8tion.jda.api.entities.TextChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;

public class Info {
    public void setPrefix(String prefix) {
        logger.trace("Set var prefix to " + prefix);
        this.prefix = prefix;
    }

    private static final Logger logger = LoggerFactory.getLogger(Info.class);

    private String prefix;

    public String getBotToken() {
        return botToken;
    }

    private final String botToken;
    private final String websiteUsername;
    private final String websitePassword;

    public TextChannel getDebugChannel() {
        return debugChannel;
    }

    public void setDebugChannel(TextChannel debugChannel) {
        logger.trace("Set TextChannel debugChannel to " + prefix);
        this.debugChannel = debugChannel;
    }

    private TextChannel debugChannel;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        logger.trace("Set flag debug to " + debug);
        this.debug = debug;
    }

    private boolean debug = true;

    public static Info getInstance() throws ParseException, IOException {
        if (instance == null) {
            return new Info();
        } else return instance;
    }

    private static void setInstance(Info instance) {
        logger.trace("Set Info instance");
        Info.instance = instance;
    }

    private static Info instance;

    private Info() throws IOException, ParseException {
        String botToken1 = System.getenv("botToken");
        String websiteUsername1 = System.getenv("webUsername");
        String websitePassword1 = System.getenv("webPassword");
        logger.trace("Try to get credentials with environments vars");

        boolean botToken1Empty = botToken1 == null;
        if (botToken1 != null) botToken1Empty = botToken1.isEmpty();
        boolean webUsername1Empty = websiteUsername1 == null;
        if (websiteUsername1 != null)
            webUsername1Empty = websiteUsername1.isEmpty();
        boolean webPassword1Empty = websitePassword1 == null;
        if (websitePassword1 != null)
            webPassword1Empty = websitePassword1.isEmpty();
        if (botToken1Empty || webUsername1Empty || webPassword1Empty) {
            logger.trace("Some Environment vars are empty");
            File file = new File(String.valueOf(FileSystems.getDefault().getPath("secrets.json")));
            if (file.exists()) {
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(new FileReader(file));
                JSONObject jsonObject = (JSONObject) obj;
                logger.trace("Created JSONObject");

                if (botToken1Empty)
                    botToken1 = (String) jsonObject.get("botToken");
                if (webUsername1Empty)
                    websiteUsername1 = (String) jsonObject.get("webUsername");
                if (webPassword1Empty)
                    websitePassword1 = (String) jsonObject.get("webPassword");
            } else
                throw new FileNotFoundException("secrets.json not found.");
        }
        assert botToken1 != null;
        assert websiteUsername1 != null;
        assert websitePassword1 != null;
        if (botToken1.equals("") || websiteUsername1.equals("") || websitePassword1.equals(""))
            throw new RuntimeException("Secrets are empty. Please write those in secret.json.");

        this.botToken = botToken1;
        this.websiteUsername = websiteUsername1;
        this.websitePassword = websitePassword1;
        logger.info("Successfully acquired credentials for the bot!");
        setInstance(this);
        logger.trace("Set Instance Info");
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
