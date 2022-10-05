package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.Constants;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public enum Rank {

    USER(1),
    PREMIUM(2),
    MODERATOR(3),
    ADMINISTRATOR(4),
    BOT_OWNER(5);

    private final int rankWeight;

    Rank(int rankWeight) {
        this.rankWeight = rankWeight;
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static Rank[] getTypes() {
        return new Rank[]{USER, PREMIUM, MODERATOR, ADMINISTRATOR, BOT_OWNER};
    }

    public String formattedName() {
        return toString();
    }

    public int getRankWeight() {
        return rankWeight;
    }

    public static Rank getPermLevelForUser(User user, TextChannel textChannel) {
        Member guildMember = Sussi.getJda().getGuildById(Constants.CM_GUILD_ID).getMember(user);
        assert guildMember != null;
        if (user.getIdLong() == Sussi.getConfig().getOwnerID()) {
            return BOT_OWNER;
        }
        if (PermissionUtil.checkPermission(textChannel, guildMember, Permission.ADMINISTRATOR)) {
            return ADMINISTRATOR;
        }
        if (PermissionUtil.checkPermission(textChannel, guildMember, Permission.BAN_MEMBERS)) {
            return MODERATOR;
        }
        return USER;
    }

    public boolean isAtLeast(Rank other) {
        return rankWeight >= other.rankWeight;
    }
}
