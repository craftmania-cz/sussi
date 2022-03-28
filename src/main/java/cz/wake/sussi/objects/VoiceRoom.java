package cz.wake.sussi.objects;

import java.util.List;

public class VoiceRoom {

    private Long userId;
    private String name;
    private Integer limit;
    private Boolean locked;
    private Integer bitrate;
    private List<String> addedMembers;
    private List<String> bannedMembers;

    public VoiceRoom(Long userId, String name, Integer limit, Boolean locked, Integer bitrate, List<String> addedMembers, List<String> bannedMembers) {
        this.userId = userId;
        this.name = name;
        this.limit = limit;
        this.locked = locked;
        this.bitrate = bitrate;
        this.addedMembers = addedMembers;
        this.bannedMembers = bannedMembers;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Integer getLimit() {
        return limit;
    }

    public Boolean getLocked() {
        return locked;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public List<String> getAddedMembers() {
        return addedMembers;
    }

    public List<String> getBannedMembers() {
        return bannedMembers;
    }
}
