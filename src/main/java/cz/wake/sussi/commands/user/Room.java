package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;
import java.util.List;

public class Room implements ICommand {
    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {



        if (Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()) == 0) {
            MessageUtils.sendErrorMessage("Nemáš žádnou místnost! Vytvoříš si ji připojením do kanálu <room>", channel);
        }

        VoiceChannel voiceChannel = member.getGuild().getVoiceChannelById(Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()));

        if (args.length < 1) {
            boolean locked = !voiceChannel.getPermissionOverride(member.getGuild().getPublicRole()).getAllowed().contains(Permission.VOICE_CONNECT);
            EmbedBuilder embedBuilder = new EmbedBuilder()             //TODO: Banned, added people
                    .setTitle("Informace o voice místnosti uživatele " + sender.getAsTag())
                    .addField("Základní informace",
                            "Název: " + voiceChannel.getName() + "\n" +
                                    "Zamknuto: " + (locked ? "Ano" : "Ne") + "\n" +
                                    "Limit: " + (voiceChannel.getUserLimit() == 0 ? "Bez limitu" : voiceChannel.getUserLimit()) + "\n" +
                                    "Bitrate: " + voiceChannel.getBitrate(), false);
            channel.sendMessage(embedBuilder.build()).queue();
        } else {
            switch (args[0]) {
                case "lock":
                    voiceChannel.putPermissionOverride(member.getGuild().getPublicRole()).setDeny(Permission.VOICE_CONNECT).queue();
                    MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " tvá místnost byla zamknuta.", 3000, channel);
                    break;
                case "unlock":
                    voiceChannel.putPermissionOverride(member.getGuild().getPublicRole()).setAllow(Permission.VOICE_CONNECT).queue();
                    MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " tvá místnost byla odemknuta", 3000, channel);
                    break;
                case "add":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toAdd = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        voiceChannel.getManager().getChannel().createPermissionOverride(toAdd).setAllow(Permission.VOICE_CONNECT).queue();
                        MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " uživatel byl přidán.", 3000, channel);
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš přidat!", channel);
                    }
                    break;
                case "remove":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toAdd = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        voiceChannel.getManager().getChannel().createPermissionOverride(toAdd).setDeny(Permission.VOICE_CONNECT).queue();
                        MessageUtils.sendAutoDeletedMessage("Uživatel byl odebrán.", 3000, channel);
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš odebrat!", channel);
                    }
                    break;
                case "limit":
                    try {
                        if (args.length == 2) {
                            voiceChannel.getManager().setUserLimit(Integer.parseInt(args[1])).queue();
                            MessageUtils.sendAutoDeletedMessage("Limit byl změněn na " + args[1], 3000, channel);
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        MessageUtils.sendErrorMessage("Limit je ve špatném formátu!", channel);
                    }
                    break;
                case "kick":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toKick = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if (toKick.getVoiceState().inVoiceChannel() && toKick.getVoiceState().getChannel().getId().equals(voiceChannel.getId())) {
                            voiceChannel.getGuild().kickVoiceMember(toKick).queue();
                            MessageUtils.sendAutoDeletedMessage("Uživatel byl vykopnut.", 3000, channel);
                        } else {
                            MessageUtils.sendErrorMessage("Uživatel není ve stejném kanálu", channel);
                        }
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš vykopnout!", channel);
                    }
                    break;
                case "ban":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toBan = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if (toBan.getVoiceState().inVoiceChannel() && toBan.getVoiceState().getChannel().getId().equals(voiceChannel.getId())) {
                            voiceChannel.getGuild().kickVoiceMember(toBan).queue();
                            MessageUtils.sendAutoDeletedMessage("Uživatel byl zabanován.", 3000, channel);
                        }
                        voiceChannel.getManager().getChannel().createPermissionOverride(toBan).setDeny(Permission.VIEW_CHANNEL).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš zabanovat!", channel);
                    }
                case "bitrate":
                    try {
                        if (args.length == 2) {
                            voiceChannel.getManager().setBitrate(Integer.parseInt(args[1])).queue();
                            MessageUtils.sendAutoDeletedMessage("Bitrate byl změněn.", 3000, channel);
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        MessageUtils.sendErrorMessage("Bitrate je ve špatném formátu!", channel);
                    }
                    break;
                case "name":
                    if (args.length >= 1) {
                        List<String> list = Arrays.asList(args);
                        list = list.subList(1, list.size());
                        String name = String.join(" ", list);
                        voiceChannel.getManager().setName(name).queue();
                        MessageUtils.sendAutoDeletedMessage("Název byl změněn.", 3000, channel);
                    } else {
                        MessageUtils.sendErrorMessage("Musíš napsat uvést název místnosti!", channel);
                    }
                    break;
            }
        }
    }

    @Override
    public String getCommand() {
        return "room";
    }

    @Override
    public String getDescription() {
        return "room";
        //TODO: Description
    }

    @Override
    public String getHelp() {
        return null;
        //TODO: Help
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
