package cz.wake.sussi.objects.votes;

import java.util.UUID;

public class RewardMonthVotePlayer {
    private String nick;
    private UUID uuid;
    private boolean linkedDiscord;
    private String discordID;
    private boolean delivered;
    private int position;
    private String rewardCode;

    public RewardMonthVotePlayer(String nick, UUID uuid, boolean linkedDiscord, String discordID, boolean delivered, int position, String rewardCode) {
        this.nick = nick;
        this.uuid = uuid;
        this.linkedDiscord = linkedDiscord;
        this.discordID = discordID;
        this.delivered = delivered;
        this.position = position;
        this.rewardCode = rewardCode;
    }

    public String getNick() {
        return nick;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isLinkedDiscord() {
        return linkedDiscord;
    }

    public String getDiscordID() {
        return discordID;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public int getPosition() {
        return position;
    }

    public String getRewardCode() {
        return rewardCode;
    }
}