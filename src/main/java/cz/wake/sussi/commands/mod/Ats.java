package cz.wake.sussi.commands.mod;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.TimeUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Ats implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - ats :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (sender.getId().equals("177516608778928129") && member.isOwner()) {
                Sussi.getInstance().getSql().resetATS("surv_chat_body");
                Sussi.getInstance().getSql().resetATS("surv_played_time");
                Sussi.getInstance().getSql().resetATS("sky_chat_body");
                Sussi.getInstance().getSql().resetATS("sky_played_time");
                Sussi.getInstance().getSql().resetATS("crea_chat_body");
                Sussi.getInstance().getSql().resetATS("crea_played_time");
                Sussi.getInstance().getSql().resetATS("prison_chat_body");
                Sussi.getInstance().getSql().resetATS("prison_played_time");
                Sussi.getInstance().getSql().resetATS("vanilla_chat_body");
                Sussi.getInstance().getSql().resetATS("vanilla_played_time");
                Sussi.getInstance().getSql().resetATS("minigames_chat_body");
                Sussi.getInstance().getSql().resetATS("minigames_played_time");
                Sussi.getInstance().getSql().resetATS("vanillasb_played_time");
                Sussi.getInstance().getSql().resetATS("vanillasb_chat_body");
            } else {
                MessageUtils.sendErrorMessage("Toto může provádět pouze Wake!", channel);
            }
        } else {
            String name = args[0];

            if (!Sussi.getInstance().getSql().isAT(name)) {
                MessageUtils.sendErrorMessage("Požadovaný člen není v AT nebo nebyl nalezen!", channel);
                return;
            }

            long opravnyCas = 7200000; //2h

            firstPage(sender, message, channel, w, name, false);
        }
    }

    @Override
    public String getCommand() {
        return "ats";
    }

    @Override
    public String getDescription() {
        return "Příkaz na zjištění aktivity AT na CM (verze 2)";
    }

    @Override
    public String getHelp() {
        return ",ats <nick> - Zjištění aktivity pro zadaný nick\n" +
                ",ats reset - Vyresetování ATS (Wake)";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private void firstPage(User s, Message message, MessageChannel ch, EventWaiter w, String name, boolean generated){

        int rank = Sussi.getInstance().getSql().getStalkerStats(name, "rank");
        int pristup_build = Sussi.getInstance().getSql().getStalkerStats(name, "pristup_build");
        int min_hours = Sussi.getInstance().getSql().getStalkerStats(name, "min_hours");
        int survChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "surv_chat_body");
        int survTime = Sussi.getInstance().getSql().getStalkerStats(name, "surv_played_time");
        int skyChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "sky_chat_body");
        int skyTime = Sussi.getInstance().getSql().getStalkerStats(name, "sky_played_time");
        int creaChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "crea_chat_body");
        int creaTime = Sussi.getInstance().getSql().getStalkerStats(name, "crea_played_time");
        int prisChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "prison_chat_body");
        int prisTime = Sussi.getInstance().getSql().getStalkerStats(name, "prison_played_time");
        int vanChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "vanilla_chat_body");
        int vanTime = Sussi.getInstance().getSql().getStalkerStats(name, "vanilla_played_time");
        int miniGChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "minigames_chat_body");
        int minGTime = Sussi.getInstance().getSql().getStalkerStats(name, "minigames_played_time");
        int vsbChatBody = Sussi.getInstance().getSql().getStalkerStats(name, "vanillasb_chat_body");
        int vsbTime = Sussi.getInstance().getSql().getStalkerStats(name, "vanillasb_played_time");

        int total_hours = survTime + skyTime + prisTime + creaTime + vanTime + minGTime + vsbTime;
        int total_activity = survChatBody + skyChatBody + creaChatBody + prisChatBody + vanChatBody + miniGChatBody + vsbChatBody;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Přehled ATS pro - " + name);
        embed.setThumbnail("https://visage.surgeplay.com/head/128/" + name);
        embed.setColor(getColorByRank(rank));
        embed.addField("Rank", getRankByID(rank), true);
        embed.addField("Přístup na Build", getResult(pristup_build), true);
        embed.addField("Celkem hodin", TimeUtils.formatTime("%d dni, %hh %mm", total_hours, false), true);
        embed.addField("Celkem aktivita", String.valueOf(total_activity) + " bodů", true);
        embed.addField("Možné body", String.valueOf(getChangePoints(total_hours, total_activity)), true);
        embed.addField("Min. počet hodin", min_hours + " hodin", true);
        embed.setFooter("Platné pro: " + getDate(System.currentTimeMillis()), null);

        if(generated){
            message.delete().queue();
            ch.sendMessage(embed.build()).queue((Message m) -> {
                m.addReaction(Constants.BACK).queue();
                m.addReaction(Constants.NEXT).queue();
                m.addReaction(Constants.DELETE).queue();
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(Constants.DELETE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    message.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(Constants.NEXT));
                }, (MessageReactionAddEvent ev) -> {
                    secondPage(s,m,ch,w,name);
                }, 60, TimeUnit.SECONDS, null);
            });
        } else {
            ch.sendMessage(embed.build()).queue((Message m) -> {
                m.addReaction(Constants.BACK).queue();
                m.addReaction(Constants.NEXT).queue();
                m.addReaction(Constants.DELETE).queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(Constants.DELETE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    message.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(Constants.NEXT));
                }, (MessageReactionAddEvent ev) -> {
                    secondPage(s,m,ch,w,name);
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

    private void secondPage(User s, Message message, MessageChannel ch, EventWaiter w, String name){

        int rank = Sussi.getInstance().getSql().getStalkerStats(name, "rank");

        int survival_chat = Sussi.getInstance().getSql().getStalkerStats(name, "surv_chat_body");
        int survival_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "surv_played_time");
        long survival_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "surv_pos_aktivita");

        int skyblock_chat = Sussi.getInstance().getSql().getStalkerStats(name, "sky_chat_body");
        int skyblock_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "sky_played_time");
        long skyblock_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "sky_pos_aktivita");

        int creative_chat = Sussi.getInstance().getSql().getStalkerStats(name, "crea_chat_body");
        int creative_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "crea_played_time");
        long creative_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "crea_pos_aktivita");

        int prison_chat = Sussi.getInstance().getSql().getStalkerStats(name, "prison_chat_body");
        int prison_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "prison_played_time");
        long prison_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "prison_pos_aktivita");

        int vanilla_chat = Sussi.getInstance().getSql().getStalkerStats(name, "vanilla_chat_body");
        int vanilla_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "vanilla_played_time");
        long vanilla_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "vanilla_pos_aktivita");

        int minigames_chat = Sussi.getInstance().getSql().getStalkerStats(name, "minigames_chat_body");
        int minigames_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "minigames_played_time");
        long minigames_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "minigames_pos_aktivita");

        int vanillasb_chat = Sussi.getInstance().getSql().getStalkerStats(name, "vanillasb_chat_body");
        int vanillasb_odehrano = Sussi.getInstance().getSql().getStalkerStats(name, "vanillasb_played_time");
        long vanillasb_posledni_aktivita = Sussi.getInstance().getSql().getStalkerStatsTime(name, "vanillasb_pos_aktivita");

        message.editMessage(MessageUtils.getEmbed(getColorByRank(rank)).setTitle("Přehled ATS pro - " + name)
                .addField("Survival", "**Chat**: " + survival_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", survival_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(survival_posledni_aktivita), true)
                .addField("Skyblock", "**Chat**: " + skyblock_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", skyblock_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(skyblock_posledni_aktivita), true)
                .addField("Creative", "**Chat**: " + creative_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", creative_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(creative_posledni_aktivita), true)
                .addField("Prison", "**Chat**: " + prison_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", prison_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(prison_posledni_aktivita), true)
                .addField("Vanilla", "**Chat**: " + vanilla_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", vanilla_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(vanilla_posledni_aktivita), true)
                .addField("MiniGames", "**Chat**: " + minigames_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", minigames_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(minigames_posledni_aktivita), true)
                .addField("Vanilla Skyblock", "**Chat**: " + vanillasb_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", vanillasb_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(vanillasb_posledni_aktivita), true)
                .setFooter("Platné pro: " + getDate(System.currentTimeMillis()), null).build()).queue((Message m) -> {

            w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(Constants.DELETE));
            }, (MessageReactionAddEvent ev) -> {
                m.delete().queue();
                message.delete().queue();
            }, 60, TimeUnit.SECONDS, null);

            w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(Constants.BACK));
            }, (MessageReactionAddEvent ev) -> {
                firstPage(s,message,ch,w,name, true);
            }, 60, TimeUnit.SECONDS, null);
        });
    }

    private int getChangePoints(int hours, int total_activity){
        int total_hours = hours / 60; // Jelikoz je to v minutach
        int points = 0;
        if(total_hours == 0 || total_activity == 0){
            points = -5;
        } else if (total_hours > 0 && total_hours < 12){
            points = -3;
        } else if (total_hours > 12 && total_hours < 24){
            points = -2;
        } else if (total_hours > 24 && total_hours < 36){
            points = -1;
        } else if (total_hours > 36 && total_hours < 48){
            points = 2;
        } else if (total_hours > 48 && total_hours < 72){
            points = 3;
        } else {
            points = 4;
        }

        //Calculate acitivy
        if(total_activity >= 0 && total_activity <= 201){
            points = points - -2;
        } else if(total_activity >= 200 && total_activity <= 600){
            points = points - -1;
        } else if(total_activity >= 600 && total_activity <= 1200){
            points = points + 1;
        } else if(total_activity >= 1200 && total_activity <= 2400){
            points = points + 2;
        }

        return points;
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }

    private String getResult(int result){
        if(result == 1){
            return "Ano";
        }
        return "Ne";
    }

    private Color getColorByRank(int rank){
        if(rank == 12){
            return Constants.MAJITEL;
        } else if (rank == 10){
            return Constants.HL_ADMIN;
        } else if (rank == 9){
            return Constants.DEV;
        } else if (rank == 2 || rank == 3){
            return Constants.HELPER;
        } else if (rank == 4 || rank == 5){
            return Constants.ADMIN;
        } else if (rank == 7){
            return Constants.EVENTER;
        } else if (rank == 8){
            return Constants.MOD;
        } else if (rank == 6){
            return Constants.BUILDER;
        } else {
            return Constants.GRAY;
        }
    }

    private String getRankByID(int rank){
        if(rank == 12){
            return "Majitel";
        } else if (rank == 10){
            return "Hl.Admin";
        } else if (rank == 9){
            return "Developer";
        } else if (rank == 2){
            return "Helper";
        } else if (rank == 3){
            return "Helperka";
        } else if (rank == 4){
            return "Admin";
        } else if (rank == 5){
            return "Adminka";
        } else if (rank == 7){
            return "Eventer";
        } else if (rank == 8){
            return "Moderátor";
        } else if (rank == 6){
            return "Builder";
        } else {
            return "Hajzlík s chybným ID!";
        }
    }
}
