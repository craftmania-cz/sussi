package cz.wake.sussi.listeners;

import cz.wake.sussi.commands.mod.CheckIP;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
        }
    }
}
