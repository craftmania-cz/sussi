package cz.wake.sussi.utils;

import cz.wake.sussi.Sussi;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Constants {

    // Colors
    final public static Color LIGHT_BLUE = new Color(93, 133, 252);
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
    final public static String GREEN_MARK = "\u2705";
    final public static String CROSS_MARK = "\u274C";
    final public static String THUMB_UP = "\uD83D\uDC4D";
    final public static String THUMB_DOWN = "\uD83D\uDC4E";
    
    // AT Rank Colors
    final public static Color MAJITEL = new Color(61, 130, 250);
    final public static Color MANAGER = new Color(215, 27, 100);
    final public static Color HL_ADMIN = new Color(205, 0, 0);
    final public static Color ADMIN = new Color(229, 72, 79);
    final public static Color DEV = new Color(252, 200, 71);
    final public static Color EVENTER = new Color(255, 113, 177);
    final public static Color HELPER = new Color(64, 200, 108);
    final public static Color MOD = new Color(248, 135, 38);
    final public static Color BUILDER = new Color(86, 113, 181);

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

    // Roles
    final public static String BOOSTER_ROLE = "580680123091648523";
    final public static String BOOSTER_COLOR_1 = "665574449541677057";
    final public static String BOOSTER_COLOR_2 = "665574758011633686";
    final public static String BOOSTER_COLOR_3 = "665575430677463060";
    final public static String BOOSTER_COLOR_4 = "665575722542301214";
    final public static String BOOSTER_COLOR_5 = "665576138277388288";
    final public static String BOOSTER_COLOR_6 = "759740234232889364";

    // Others
    final public static String CM_GUILD_ID = Sussi.config.getCmGuildID().toString();
    final public static long CM_AT_REPUTATION_CHANNEL = 1051917159674155038L;
    final public static String CATEGORY_KECARNA_ID = Sussi.config.getKecarnyCategoryID().toString();
    final public static String VOICE_CREATE_ID = Sussi.config.getVytvoritVoiceID().toString();
    final public static String CHANNEL_BOT_COMMANDS_ID = Sussi.config.getBotPrikazyChannelID().toString();
    final public static Color HALLOWEEN = new Color(252, 161, 3);

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
