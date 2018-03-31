package cz.wake.sussi.utils;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Constants {

    // Colors
    final public static Color BLUE = new Color(58, 95, 205);
    final public static Color RED = new Color(220, 20, 60);
    final public static Color GREEN = new Color(60, 179, 90);
    final public static Color PINK = new Color(205, 50, 120);
    final public static Color ORANGE = new Color(255, 165, 0);
    final public static Color GRAY = new Color(161, 161, 161);
    final public static Color DARK_GRAY = new Color(89,89,89);

    // Emoji
    final public static String BACK = "\u25C0";
    final public static String NEXT = "\u25B6";
    final public static String DELETE = "\u274C";

    // AT Rank Colors
    final public static Color MAJITEL = new Color(30, 144, 255);
    final public static Color HL_ADMIN = new Color(205, 0, 0);
    final public static Color ADMIN = new Color(255, 64, 64);
    final public static Color DEV = new Color(39, 64, 139);
    final public static Color EVENTER = new Color(238, 122, 233);
    final public static Color HELPER = new Color(0, 205, 102);
    final public static Color MOD = new Color(255, 165, 0);
    final public static Color BUILDER = new Color(145, 44, 238);

    // Numbers
    final public static String ONE = "\u0031\u20E3";
    final public static String TWO = "\u0032\u20E3";
    final public static String THREE = "\u0033\u20E3";
    final public static String FOUR = "\u0034\u20E3";
    final public static String FIVE = "\u0035\u20E3";
    final public static String SIX = "\u0036\u20E3";
    final public static String SEVEN = "\u0037\u20E3";
    final public static String EIGHT = "\u0038\u20E3";
    final public static String NINE = "\u0039\u20E3";
    final public static String TEN = "\uD83D\uDD1F";

    private static int getColorCode() {
        int choice = ThreadLocalRandom.current().nextInt(0, 1 + 1);
        switch (choice) {
            case 0:
                choice = ThreadLocalRandom.current().nextInt(200, 250 + 1);
                break;
            case 1:
                choice = ThreadLocalRandom.current().nextInt(100, 150 + 1);
                break;
        }
        return choice;
    }

    public static Color getRandomColor() {
        return new Color(getColorCode(), getColorCode(), getColorCode());
    }

}
