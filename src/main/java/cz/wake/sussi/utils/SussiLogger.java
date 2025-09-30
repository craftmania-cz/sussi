package cz.wake.sussi.utils;

import cz.wake.sussi.Sussi;

public class SussiLogger {

    public static void greatMessage(String text) {
        Sussi.getLogger().info(AnsiColor.GREEN.applyTo("[SUCCESS]: ") + text);
    }

    public static void warnMessage(String text) {
        Sussi.getLogger().info(AnsiColor.MAGENTA.applyTo("[WARNING]: ") + text);
    }

    public static void errorMessage(String text) {
        Sussi.getLogger().info(AnsiColor.RED.applyTo("[ERROR]: ") + text);
    }

    public static void fatalMessage(String text) {
        Sussi.getLogger().info(AnsiColor.RED.applyTo("[FATAL ERROR]: ") + text);
    }

    public static void infoMessage(String text) {
        Sussi.getLogger().info(AnsiColor.YELLOW.applyTo("[INFO]: ") + text);
    }

    public static void debugMessage(String text) {
        Sussi.getLogger().info(AnsiColor.CYAN.applyTo("[DEBUG]: ") + text);
    }

    public static void commandMessage(String text) {
        Sussi.getLogger().info(AnsiColor.CYAN.applyTo("[COMMAND]: ") + text);
    }
}
