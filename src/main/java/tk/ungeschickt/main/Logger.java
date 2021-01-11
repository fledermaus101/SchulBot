package tk.ungeschickt.main;

public class Logger {

    private final String className;
    private final String methodName;

    public Logger(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }


    public void logDebug(String msg, String className, String methodName) {
        if (Main.getInfo().isDebug()) {
            String template = Main.dgray + "[" + Main.blue + "Debug" + Main.dgray + "] Class " + Main.lgray + "%s" + Main.dgray + ": Method " + Main.lgray + "%s" + Main.dgray + ":" + Main.blue + " %s";
            System.out.printf((template) + "%n", className, methodName, msg);
        }
    }
}