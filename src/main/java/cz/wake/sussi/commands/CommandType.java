package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;

import java.util.List;

@Deprecated
public enum CommandType {

    GENERAL,
    MODERATION,
    FUN,
    GAME_CHANNEL,
    ADMINISTARTOR,
    BOT_OWNER;

    CommandType() {
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandType[] getTypes() {
        return new CommandType[]{GENERAL, FUN, MODERATION, GAME_CHANNEL, ADMINISTARTOR, BOT_OWNER};
    }

    public String formattedName() {
        return toString();
    }
}
