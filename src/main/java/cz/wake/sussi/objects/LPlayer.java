package cz.wake.sussi.objects;

public class LPlayer {

    private String name;
    private String uuid;
    private String IP;
    private String date;

    public LPlayer(String name, String uuid, String IP, String date) {
        this.name = name;
        this.uuid = uuid;
        this.IP = IP;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIP() {
        return IP;
    }

    public String getDate() {
        return date;
    }
}
