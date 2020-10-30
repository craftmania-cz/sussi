package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.HalloweenStatsHandler;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public class HalloweenStats implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String nick = "";
        if(args.length < 1) {
            if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
                MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `,hs MrWakeCZ`", channel);
                return;
            } else nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
        }

        if (args.length > 0 || message.getMentions(Message.MentionType.USER).size() > 0) {
            if (args[0].startsWith("<@") && args[0].endsWith(">")) {
                List<IMentionable> mentions = message.getMentions(Message.MentionType.USER);
                if (mentions.size() > 0) {
                    if (Sussi.getInstance().getSql().isAlreadyLinkedByID(mentions.get(0).getId()))
                        nick = Sussi.getInstance().getSql().getLinkedNickname(mentions.get(0).getId());
                    else {
                        MessageUtils.sendErrorMessage("Uživatel " + mentions.get(0).getAsMention() + " nemá propojený MC účet.", channel);
                        return;
                    }

                }
            }
        }

        HalloweenStatsHandler.HalloweenStats stats;
        String finalName;
        if (nick.length() == 0) {
            finalName = args[0];
            stats = HalloweenStatsHandler.getStatistics(args[0]);
        } else {
            finalName = nick;
            stats = HalloweenStatsHandler.getStatistics(nick);
        }

        if (stats == null) {
            MessageUtils.sendErrorMessage("Hráč `" + finalName + "` nebyl nalezen.", channel);
            return;
        }

        showStats(sender, message, channel, w, stats, finalName);
    }

    private void showStats(User s, Message message, MessageChannel ch, EventWaiter w, HalloweenStatsHandler.HalloweenStats stats, String nick) {
        EmbedBuilder embedBuilder = MessageUtils.getEmbed(Constants.HALLOWEEN)
                .setTitle("Halloween statistiky hráče: " + nick)
                .setThumbnail("https://mc-heads.net/head/" + nick + "/128.png")
                .addField("Statistiky za killera",
                        "Výhry: " + stats.getKiller_wins() + "\n" +
                        "Smrtelné zasažení: " + stats.getKiller_downs() + "\n" +
                        "Zasažení: " + stats.getKiller_hits() + "\n" +
                        "Killy: " + stats.getKiller_kills(), false)
                .addField("Statistiky za survivora",
                        "Výhry: " + stats.getSurvivor_wins() + "\n" +
                        "Opravené generátory: " + stats.getSurvivor_generators_powered() + "\n" +
                        "Vložené baterie: " + stats.getSurvivor_fuels_filled() + "\n" +
                        "Oživení spoluhráči: " + stats.getSurvivor_players_revived(), false)
                .addField("Všeobecné statistiky",
                        "Odehrané hry: " + stats.getGames_played() + "\n" +
                        "Playtime: " + DurationFormatUtils.formatDuration(stats.getPlaytime(), "HH'h' mm'm' ss's'"), false)
                .setFooter("CraftMania.cz Stats")
                .setTimestamp(Instant.from(ZonedDateTime.now()));

        ch.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getCommand() {
        return "hs";
    }

    @Override
    public String getDescription() {
        return "Vypíše statistiky hráče z halloween minihry 2020.";
    }

    @Override
    public String getHelp() {
        return ",hs [nick] - hráčove statistiky z halloween minihry 2020";
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
    public String[] getAliases() {
        return new String[]{"halloween", "halloweenstats"};
    }
}
