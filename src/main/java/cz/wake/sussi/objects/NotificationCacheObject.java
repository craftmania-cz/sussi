package cz.wake.sussi.objects;

import net.dv8tion.jda.api.interactions.InteractionHook;

public class NotificationCacheObject {

    private String notificationId;
    private String playerUUID;
    private String playerName;
    private String notificationType;
    private String notificationPriority;
    private String notificationServer;
    private String title;
    private String text;

    public NotificationCacheObject(
            String notificationId,
            String playerName,
            String playerUUID,
            String notificationType,
            String notificationPriority,
            String notificationServer) {
        this.notificationId = notificationId;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.notificationType = notificationType;
        this.notificationPriority = notificationPriority;
        this.notificationServer = notificationServer;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getNotificationPriority() {
        return notificationPriority;
    }

    public String getNotificationServer() {
        return notificationServer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }
}
