package app.model;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public final class Logger {

    private static String log = "";

    public static void log(String log) {
        if (log != null)
            log += log+"\n\n";
    }

    public static String getLog() {
        return log;
    }

    public static void printLog() {
        System.out.println(log);
    }

}
