package tk.ungeschickt.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.Scanner;

public class Info {
    private final String prefix;

    public String getBotToken() {
        return botToken;
    }

    private final String botToken;
    private final String websiteUsername;
    private final String websitePassword;

    public boolean isDebug() {
        return debug;
    }

    private final boolean debug = true;

    public Info(String prefix) throws FileNotFoundException {
        String websiteUsername1 = null;
        String websitePassword1 = null;
        this.prefix = prefix;
        File file = new File(String.valueOf(FileSystems.getDefault().getPath("secret.txt")));
        String botToken1 = null;
        if (file.exists()) {
            Scanner scanner = new Scanner(file);
            if (scanner.hasNextLine())
                botToken1 = scanner.nextLine();
            if (scanner.hasNextLine())
                websiteUsername1 = scanner.nextLine();
            if (scanner.hasNextLine())
                websitePassword1 = scanner.nextLine();
        }
        this.websiteUsername = websiteUsername1;
        this.websitePassword = websitePassword1;
        this.botToken = botToken1;
        if (botToken1.equals("") || websiteUsername1.equals("") || websitePassword1.equals("")) {
            throw new RuntimeException("Secrets were empty. Please write those in secret.txt.\nFirst token. Then WebUsername. And lastly WebPassword.");
        }
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
