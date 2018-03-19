package cz.wake.sussi.objects;

public class LBan {

    private String uuid;
    private String reason;
    private String bannedBy;
    private long time;
    private long until;
    private boolean ipban;

    public LBan(String uuid, String reason, String bannedBy, long time, long until, boolean ipban) {
        this.uuid = uuid;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.time = time;
        this.until = until;
        this.ipban = ipban;
    }

    public String getUuid() {
        return uuid;
    }

    public String getReason() {
        return reason;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public long getTime() {
        return time;
    }

    public long getUntil() {
        return until;
    }

    public boolean isIpban() {
        return ipban;
    }
}
