package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class WikiSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        String optionId = event.getOption("id").getAsString();

        switch (optionId) {
            case "pravidla":
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                        .setTitle("Pravidla serveru").setDescription("[https://wiki.craftmania.cz/pravidla-serveru/](https://wiki.craftmania.cz/pravidla-serveru/)").build()).queue();
                break;
            case "povolene-mody":
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                        .setTitle("Povolené a zakázané módy").setDescription("[https://wiki.craftmania.cz/povolene-a-zakazane-mody/](https://wiki.craftmania.cz/povolene-a-zakazane-mody/)").build()).queue();
                break;
            case "jak-se-pripojit":
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                        .setTitle("Povolené a zakázané módy").setDescription("[https://wiki.craftmania.cz/faq/jak-se-pripojit-na-server/](https://wiki.craftmania.cz/faq/jak-se-pripojit-na-server/)").build()).queue();
                break;
            case "problemy-s-resource-packem":
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                        .setTitle("Wiki: Problémy s Resource Packem").setDescription("[https://wiki.craftmania.cz/faq/problem-s-resource-packem/](https://wiki.craftmania.cz/faq/problem-s-resource-packem/)").build()).queue();
                break;
            case "navod-discord":
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                        .setTitle("Wiki: Jak na CraftMania Discord").setDescription("[https://wiki.craftmania.cz/discord/](https://wiki.craftmania.cz/discord/)").build()).queue();
                break;
            case "navod-screeny-a-logy":
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                        .setTitle("Wiki: Jak najít screenshoty, Logy a RP").setDescription("[https://wiki.craftmania.cz/jak-najit-logs-a-screenshoty/](https://wiki.craftmania.cz/jak-najit-logs-a-screenshoty/)").build()).queue();
                break;
        }
    }

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public String getDescription() {
        return "Získání odkazů k CraftMania Wiki";
    }

    @Override
    public String getHelp() {
        return "/wiki [téma]";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }
}
