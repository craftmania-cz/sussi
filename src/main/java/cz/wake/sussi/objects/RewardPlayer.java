package cz.wake.sussi.objects;

public class RewardPlayer {

    private String nick;
    private boolean vybrano;
    private String server;
    private long time;

    public RewardPlayer(String nick, boolean vybrano, String server, long time) {
        this.nick = nick;
        this.vybrano = vybrano;
        this.server = server;
        this.time = time;
    }

    public String getNick() {
        return nick;
    }

    public boolean isVybrano() {
        return vybrano;
    }

    public String getServer() {
        return server;
    }

    public long getTime() {
        return time;
    }
}
