package cz.wake.sussi.objects.votes;

import java.util.UUID;

public class VotePlayer {

    private String nick;
    private UUID uuid;
    private int monthlyVotes;
    private int position;

    public VotePlayer(String nick, UUID uuid, int monthlyVotes, int position) {
        this.nick = nick;
        this.uuid = uuid;
        this.monthlyVotes = monthlyVotes;
        this.position = position;
    }

    public String getNick() {
        return nick;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMonthlyVotes() {
        return monthlyVotes;
    }

    public int getPosition() {
        return position;
    }
}
