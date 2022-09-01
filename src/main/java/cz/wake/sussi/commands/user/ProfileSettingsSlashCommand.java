package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ProfileSettingsSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        String subcommandName = event.getSubcommandName();

        if (subcommandName == null) {
            return;
        }

        // Check zda má hráč link s učtem, jinak nn prd prd
        if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Nemáš propojený profil! Tuto funkci lze používat pouze pokud si propojíš učet s Discordem. Navštiv kanál **#propojeni_profilu**").build()).queue();
            return;
        }

        String minecraftNick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());

        switch (subcommandName) {
            case "status":
                String defaultMessage = "Tento hráč nemá nastavený status...";
                String statusToSet = event.getOption("text").getAsString();
                if (statusToSet.length() >= 100) {
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription("Nelze nastavit status delší jak 100 znaků.").build()).queue();
                    return;
                }
                if (statusToSet.equals("clear")) {
                    // set default
                    Sussi.getInstance().getSql().updatePlayerStatus(minecraftNick, defaultMessage);
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Tvůj status byl úspěšně vyresetován.").build()).queue();
                    return;
                }
                Sussi.getInstance().getSql().updatePlayerStatus(minecraftNick, statusToSet);
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Tvůj status byl nastaven na: `" + statusToSet + "`.").setFooter("Pokud chceš status smazat, napiš text jako *clear*.").build()).queue();
                break;
            case "gender":
                long gengerId = event.getOption("type").getAsLong();
                Sussi.getInstance().getSql().updatePlayerGenderId(minecraftNick, (int) gengerId);
                if (gengerId == 1) {
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Tvoje pohlaví na profilu bylo nastaveno na: **Muž**.").build()).queue();
                } else if (gengerId == 2){
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Tvoje pohlaví na profilu bylo nastaveno na: **Žena**.").build()).queue();
                } else {
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Tvoje pohlaví na profilu bylo nastaveno na: **Neuvedeno**.").build()).queue();
                }
                break;
        }
    }

    @Override
    public String getName() {
        return "profile-settings";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return "/profile-settings";
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
