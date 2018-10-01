package cz.wake.sussi.utils;

import cz.wake.sussi.Sussi;

public class SussiLogger {

    public static void greatMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.GREEN.applyTo("✔ success") + "  " + text);
    }

    public static void warnMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.YELLOW.applyTo("⚠ warn") + "      " + text);
    }

    public static void dangerMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.RED.applyTo("✖ danger") + "    " + text);
    }

    public static void fatalMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.RED.applyTo("✖ fatal") + "     " + text);
    }

    public static void infoMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.BLUE.applyTo("ℹ info") + "     " + text);
    }

    public static void debugMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.MAGENTA.applyTo("… debug") + "     " + text);
    }

    public static void commandMessage(String text) {
        Sussi.LOGGER.info(AnsiColor.CYAN.applyTo("❯ command") + "   " + text);
    }
}
