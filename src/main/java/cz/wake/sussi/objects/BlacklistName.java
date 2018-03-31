package cz.wake.sussi.objects;

public class BlacklistName {

    private String name;
    private String reason;
    private String bannedBy;
    private long timeStart;
    private long timeEnd;

    public BlacklistName(String name) {
        this.name = name;
    }

    public BlacklistName(String name, String reason, String bannedBy, long timeStart, long timeEnd) {
        this.name = name;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setBannedBy(String bannedBy) {
        this.bannedBy = bannedBy;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }
}
