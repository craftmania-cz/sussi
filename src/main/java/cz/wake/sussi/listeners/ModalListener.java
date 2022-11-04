package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.NotificationCacheObject;
import cz.wake.sussi.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class ModalListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getMember() == null) return;

        String[] modalKey = event.getModalId().split(":");

        switch (modalKey[0]) {
            case "notification_create" -> {
                String notificationCacheUUID = modalKey[1];
                NotificationCacheObject notificationCacheObject = Sussi.getInstance().getNotificationCache().get(notificationCacheUUID);
                if (notificationCacheObject == null) {
                    event.getHook().sendMessage("NotificationCache je null, chyba!");
                    return;
                }
                notificationCacheObject.setTitle(event.getValues().get(0).getAsString());
                notificationCacheObject.setText(event.getValues().get(1).getAsString());
                MessageCreateBuilder data = new MessageCreateBuilder();
                data.addEmbeds(
                        new EmbedBuilder().setTitle("Vytvoření notifikace - souhrn").setColor(Constants.GREEN)
                         .addField("Metadata",
                                 "**Jméno hráče:** " + notificationCacheObject.getPlayerName() + "\n" +
                                 "**Typ:** " + notificationCacheObject.getNotificationType() + "\n" +
                                 "**Priorita:** " + notificationCacheObject.getNotificationPriority() + "\n" +
                                 "**Server:** " + notificationCacheObject.getNotificationServer()
                                 , false)
                        .addField("Text notifikace",
                                "**Nadpis** " + event.getValues().get(0).getAsString() + "\n" +
                                "**Text:** \n ```" + event.getValues().get(1).getAsString() + "```"
                                , false)
                        .build());
                data.addComponents(ActionRow.of(Button.of(ButtonStyle.PRIMARY, "notification_create:" + event.getUser().getId() + ":" + notificationCacheUUID, "Vytvořit & odeslat")));
                event.reply(data.build()).setEphemeral(true).queue();
            }
        }
    }
}
