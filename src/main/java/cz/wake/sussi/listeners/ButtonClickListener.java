package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.mod.CheckIP;
import cz.wake.sussi.objects.NotificationCacheObject;
import cz.wake.sussi.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class ButtonClickListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if (!event.getComponentId().contains(":")) {
            return;
        }

        String[] id = event.getComponentId().split(":");
        String type = id[0];
        String authorId = id[1];
        String data = id[2];

        if (!authorId.equals(event.getUser().getId())){
            return;
        }
        event.deferEdit().queue();
        switch (type) {
            case "delete" -> // Delete button
                    event.getHook().deleteOriginal().queue();
            case "checkIp" -> {
                CheckIP check = new CheckIP();
                check.checkIP(data, event.getHook());
            }
            case "notification_create" -> {
                NotificationCacheObject notificationCacheObject = Sussi.getInstance().getNotificationCache().get(data);
                if (notificationCacheObject == null) {
                    event.getHook().sendMessage("NotificationCache je null, chyba!");
                    return;
                }
                Sussi.getInstance().getSql().createNotificationForPlayer(
                        notificationCacheObject.getPlayerUUID(),
                        notificationCacheObject.getNotificationType(),
                        notificationCacheObject.getNotificationPriority(),
                        notificationCacheObject.getNotificationServer(),
                        notificationCacheObject.getTitle(),
                        notificationCacheObject.getText()
                );
                MessageCreateBuilder messageData = new MessageCreateBuilder();
                messageData.addEmbeds(new EmbedBuilder().setColor(Constants.GREEN)
                        .setTitle("Notifikace byla vytvořena")
                        .setDescription("**Odesláno hráči:** " + notificationCacheObject.getPlayerName() + "\n**Vytvořil(a):** " + event.getUser().getAsTag() + "\n**Priorita:** " + notificationCacheObject.getNotificationPriority() + "\n**Server:** " + notificationCacheObject.getNotificationServer() + "\n**Nadpis:** " + notificationCacheObject.getTitle() + "\n**Text:** ```" + notificationCacheObject.getText() + "```")
                        .setFooter("Notifikace se zobrazí hráči do 5 minut pokud je online.")
                        .build());
                event.getChannel().sendMessage(messageData.build()).queue();
            }
        }
    }
}
