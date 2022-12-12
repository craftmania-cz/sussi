package cz.wake.sussi.objects.ats;

import lombok.Getter;

public class AtsRatingObject {

    @Getter
    private String name;

    @Getter
    private long discordId;

    @Getter
    private int rank;

    @Getter
    private String headEmoji;

    public AtsRatingObject(String name, long discordId, int rank, String headEmoji) {
        this.name = name;
        this.discordId = discordId;
        this.rank = rank;
        this.headEmoji = headEmoji;
    }
}
