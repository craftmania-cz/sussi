package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.ats.ATS;
import cz.wake.sussi.objects.ats.ATSManager;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.apache.commons.lang3.tuple.Triple;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NewAts implements ICommand {


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {
            if (Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
                String name = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
                if (!Sussi.getATSManager().isInATS(name)) {
                    MessageUtils.sendErrorMessage("Nelze použít ,ats pokud nejsi člen AT!", channel);
                    return;
                }
                ATS ats = Sussi.getATSManager().getATS(name);
                if (ats == null) {
                    MessageUtils.sendErrorMessage("Nelze použít ,ats pokud nejsi člen AT!", channel);
                    return;
                }
                firstPage(sender, message, channel, w, ats, false);
                return;
            }
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - ats :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
            return;
        } else if (args[0].equalsIgnoreCase("evaluate")) {
            if (sender.getId().equalsIgnoreCase("234700305831428106")) {
                if (args.length > 1) {
                    String name = args[1];
                    if (name.equalsIgnoreCase("all")) {
                        if (args.length >= 3) {
                            if (args[2].equalsIgnoreCase("-r")) {
                                channel.sendMessage(MessageUtils.getEmbed()
                                        .setTitle("Vyhodnocení ATS - " + (new SimpleDateFormat("MM/yyyy").format(System.currentTimeMillis())))
                                        .setDescription("Vyhodnocuji a resetuji ATS...").build()).queue(msg -> {
                                    Triple<EmbedBuilder, EmbedBuilder, List<ATS>> pair = Sussi.getATSManager().evaluate(true);
                                    msg.editMessage(pair.getLeft().build()).queue();
                                    if (!pair.getRight().isEmpty()) channel.sendMessage(":warning: `" + pair.getRight().stream().map(ATS::getName).collect(Collectors.joining("` `")) + "` se nepodařilo zaslat individuální ATS do DM.").queue();
                                    MessageChannel secretChannel = Sussi.getJda().getTextChannelById(ATSManager.PRIVATE_CHANNEL_ID);
                                    if (secretChannel == null) return;
                                    secretChannel.sendMessage(pair.getMiddle().build()).queue();
                                });
                            }
                        } else {
                            channel.sendMessage(MessageUtils.getEmbed()
                                    .setTitle("Vyhodnocení ATS - " + (new SimpleDateFormat("MM/yyyy").format(System.currentTimeMillis())))
                                    .setDescription("Vyhodnocuji ATS...").build()).queue(msg -> {
                                Triple<EmbedBuilder, EmbedBuilder, List<ATS>> pair = Sussi.getATSManager().evaluate(false);
                                msg.editMessage(pair.getLeft().build()).queue();
                                if (!pair.getRight().isEmpty()) channel.sendMessage(":warning: `" + pair.getRight().stream().map(ATS::getName).collect(Collectors.joining("` `")) + "` se nepodařilo zaslat individuální ATS do DM.").queue();
                                MessageChannel secretChannel = Sussi.getJda().getTextChannelById(ATSManager.PRIVATE_CHANNEL_ID);
                                if (secretChannel == null) return;
                                secretChannel.sendMessage(pair.getMiddle().build()).queue();
                            });

                        }
                        message.delete();
                        return;
                    }
                    if (!Sussi.getATSManager().isInATS(name)) {
                        MessageUtils.sendErrorMessage("Nelze použít ,ats pokud nejsi člen AT!", channel);
                        return;
                    }
                    if (!Sussi.getInstance().getSql().isAlreadyLinkedByNick(name)) {
                        MessageUtils.sendErrorMessage("Nick není propojen s Discord účtem.", channel);
                        return;
                    }
                    ATS ats = new ATS(name);
                    channel.sendMessage(MessageUtils.getEmbed().setDescription("Evaluation sending to DM...").build()).queue();
                    ats.evaluate();
                    return;
                }
            } else {
                MessageUtils.sendErrorMessage("Na toto má oprávnění pouze Kwak!", channel);
                return;
            }
        } else {
            String name = args[0];

            if (!Sussi.getATSManager().isInATS(name)) {
                MessageUtils.sendErrorMessage("Požadovaný člen není v AT nebo nebyl nalezen!", channel);
                return;
            }

            firstPage(sender, message, channel, w, Sussi.getATSManager().getATS(name), false);
        }
    }

    private void firstPage(User s, Message message, MessageChannel ch, EventWaiter w, ATS ats, boolean generated) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Přehled ATS pro - " + ats.getName());
        embed.setThumbnail("https://mc-heads.net/head/" + ats.getName() + "/128.png");
        embed.setColor(ats.getColorByRank());
        embed.addField("Rank", ats.getRankByID(), true);
        embed.addField("Přístup na Build", getResult(ats.getPristup_build()), true);
        embed.addField("Celkem hodin", TimeUtils.formatTime("%d dni, %hh %mm", ats.getTotalTime(), false), true);
        embed.addField("Celkem aktivita", ats.getTotalActivityFormatted(), true);
        embed.addField("Trestné body", String.valueOf(0), true);
        embed.addField("Min. počet hodin", ats.getMinHoursFormatted() + " (" + resolveTime(ats.getTotalTime() / 60, ats.getMin_hours()) + ")", true);
        embed.setFooter("Platné pro: " + getDate(System.currentTimeMillis()) + " (v3)", null);

        if (generated) {
            message.delete().queue();
            ch.sendMessage(embed.build()).queue((Message m) -> {
                m.addReaction(Constants.BACK).queue();
                m.addReaction(Constants.NEXT).queue();
                m.addReaction(Constants.DELETE).queue();
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.DELETE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    message.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.NEXT));
                }, (MessageReactionAddEvent ev) -> {
                    secondPage(s, m, ch, w, ats);
                }, 60, TimeUnit.SECONDS, null);
            });
        } else {
            ch.sendMessage(embed.build()).queue((Message m) -> {
                m.addReaction(Constants.BACK).queue();
                m.addReaction(Constants.NEXT).queue();
                m.addReaction(Constants.DELETE).queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.DELETE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    message.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.NEXT));
                }, (MessageReactionAddEvent ev) -> {
                    secondPage(s, m, ch, w, ats);
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

    private void secondPage(User s, Message message, MessageChannel ch, EventWaiter w, ATS ats) {
        message.editMessage(MessageUtils.getEmbed(ats.getColorByRank())
                .setTitle("Přehled ATS pro - " + ats.getName())
                .addField("Survival", "**Chat:** " + ats.getServerATS(ATS.Server.SURVIVAL).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.SURVIVAL).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.SURVIVAL).getLastActivity()), true)
                .addField("Skyblock", "**Chat:** " + ats.getServerATS(ATS.Server.SKYBLOCK).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.SKYBLOCK).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.SKYBLOCK).getLastActivity()), true)
                .addField("Creative", "**Chat:** " + ats.getServerATS(ATS.Server.CREATIVE).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.CREATIVE).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.CREATIVE).getLastActivity()), true)
                //.addField("Prison", "**Chat:** " + ats.getServerATS(ATS.Server.PRISON).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.PRISON).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.PRISON).getLastActivity()), true)
                .addField("Vanilla", "**Chat:** " + ats.getServerATS(ATS.Server.VANILLA).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.VANILLA).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.VANILLA).getLastActivity()), true)
                .addField("Skycloud", "**Chat:** " + ats.getServerATS(ATS.Server.SKYCLOUD).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.SKYCLOUD).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.SKYCLOUD).getLastActivity()), true)
                .addField("Build servery", "**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.BUILD).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.BUILD).getLastActivity()), true)
                .addField("Event server", "**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm", ats.getServerATS(ATS.Server.EVENTS).getPlayedTime(), false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.EVENTS).getLastActivity()), true)
                .setFooter("Platné pro: " + getDate(System.currentTimeMillis()) + " (v3)", null)
                .build()).queue((Message m) -> {

            w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.DELETE));
            }, (MessageReactionAddEvent ev) -> {
                m.delete().queue();
                message.delete().queue();
            }, 60, TimeUnit.SECONDS, null);

            w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.BACK));
            }, (MessageReactionAddEvent ev) -> {
                firstPage(s, message, ch, w, ats, true);
            }, 60, TimeUnit.SECONDS, null);
        });
    }

    @Override
    public String getCommand() {
        return "ats";
    }

    @Override
    public String getDescription() {
        return "Příkaz na zjištění aktivity AT na CM (verze 3)";
    }

    @Override
    public String getHelp() {
        return ",ats [nick] - Zjištění aktivity pro zadaný nick\n" +
                ",ats reset - Vyresetování ATS (Wake)\n" +
                ",ats evaluate [-r]- Ručné vyhodnocení ATS (Wake)";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }

    private String getResult(int result) {
        if (result == 1) {
            return "Ano";
        }
        return "Ne";
    }

    private String resolveTime(int hours, int min) {
        if (hours >= min) {
            return "\u2705";
        }
        return "\u274C";
    }
}
