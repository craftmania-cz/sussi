package cz.wake.sussi.metrics;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.core.OnlineStatus;

public class BotMetrics {

    private long memberCount;
    private long memberOnlineCount;
    private long ping;

    public boolean count() {
        this.memberCount = Sussi.getJda().getGuildById("207412074224025600").getMembers().size();
        this.memberOnlineCount = Sussi.getJda().getGuildById("207412074224025600").getMembers().stream().filter(u -> !u.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count();
        this.ping = Sussi.getJda().getPing();
        return true;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public long getMemberOnlineCount() {
        return memberOnlineCount;
    }

    public long getPing() {
        return ping;
    }
}