package cz.wake.sussi.listeners;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import cz.wake.sussi.utils.LoadingProperties;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogFlowListener extends ListenerAdapter {

    private final AIDataService aiDataService;
    private final LoadingProperties config;
    private final ArrayList<String> channels;

    public DialogFlowListener(AIDataService aiDataService) {
        this.aiDataService = aiDataService;
        this.config =  new LoadingProperties();
        this.channels = new ArrayList<>();
        this.channels.add("236749682229903360"); // at_pokec
        this.channels.add("440906414248951809"); // mc_problemy
        this.channels.add("276422086757711873"); // nahlaseni_hracu
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        if (!config.isDialogFlowEnabled()) {
            return;
        }

        if (e.getAuthor().isBot()) {
            return;
        }

        if (!this.channels.contains(e.getChannel().getId())) {
            return;
        }

        String text = e.getMessage().getContentStripped();

        try {
            AIResponse response = aiDataService.request(new AIRequest(text));

            if (response.getStatus().getCode() == 200) {
                if (response.getResult().getAction().equals("shutup")) {
                    System.out.println("Shut up intent received, returning.");
                    return;
                }
                String out = response.getResult().getFulfillment().getSpeech();
                if (out != null && !out.isEmpty()) {
                    e.getChannel().sendTyping().queue();
                    out = rePlaceHolders(out);
                    TextChannel textChannel = e.getChannel();
                    if (textChannel != null && !textChannel.canTalk()) {
                        System.out.println("Can't talk in channel, skipping response: " + out);
                        return;
                    }
                    System.out.println(String.format("\nUser input: %s\nBot output: %s", text, out));
                    e.getChannel().sendMessage(out).queue();
                } else {
                    System.out.println("Empty response received.");
                }
            } else {
                System.out.println(response.getStatus().getErrorDetails());
            }
        } catch (AIServiceException ex) {
            String message = "No error details provided";
            AIResponse response = ex.getResponse();
            if (response != null) {
                message = response.getStatus().getErrorDetails();
            }
            System.out.println(message);
        }
    }

    //careful, the keys support regex characters
    private static final Map<String, String> placeHolders = new HashMap<>();

    static {
        placeHolders.put("<br>", "\n");
    }

    private static String rePlaceHolders(String input) {
        String result = input;
        for (Map.Entry<String, String> entry : placeHolders.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
