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

public class Room implements ICommand {
    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {



        if (Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()) == 0) {
            //TODO: error hláška - nemáš žádnou room
        }

        VoiceChannel voiceChannel = member.getGuild().getVoiceChannelById(Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()));

        if (args.length < 1) {
            //TODO: room info
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Informace o voice místnosti uživatele " + sender.getAsTag())
                    .addField("Základní informace",
                            "Název: " + voiceChannel.getName() + "\n" +
                                    "Limit: " + voiceChannel.getUserLimit() + "\n" + //TODO: if unlocked => bez limitu
                                    "Bitrate: " + voiceChannel.getBitrate(), false);
            channel.sendMessage(embedBuilder.build()).queue();
        } else {
            switch (args[0]) {
                case "lock":
                    voiceChannel.getManager().getChannel().putPermissionOverride(member.getGuild().getPublicRole()).setDeny(Permission.VOICE_CONNECT).queue();
                    MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " tvá místnost byla zamknuta.", 3000, channel);
                    break;
                case "unlock":
                    voiceChannel.getManager().getChannel().putPermissionOverride(member.getGuild().getPublicRole()).setAllow(Permission.VOICE_CONNECT).queue();
                    MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " tvá místnost byla odemknuta", 3000, channel);
                    break;
                case "add":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toAdd = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        voiceChannel.getManager().getChannel().createPermissionOverride(toAdd).setAllow(Permission.VOICE_CONNECT).queue();
                        //TODO: úspěch hláška
                    } else {
                        //TODO: error hláška
                    }
                    break;
                case "remove":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toAdd = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        voiceChannel.getManager().getChannel().createPermissionOverride(toAdd).setDeny(Permission.VOICE_CONNECT).queue();
                        //TODO: úspěch hláška
                    } else {
                        //TODO: error hláška
                    }
                    break;
                case "limit":
                    try {
                        if (args.length == 2) {
                            voiceChannel.getManager().setUserLimit(Integer.parseInt(args[1])).queue();
                            //TODO: úspěch hláška
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        //TODO: error hláška
                    }
                    break;
                case "kick":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toKick = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if (toKick.getVoiceState().inVoiceChannel() && toKick.getVoiceState().getChannel().getId().equals(voiceChannel.getId())) {
                            voiceChannel.getGuild().kickVoiceMember(toKick).queue();
                            //TODO: úspěch hláška
                        } else {
                            //TODO: error hláška - uživatel není v (stejném) kanálu
                        }
                    } else {
                        //TODO: error hláška
                    }
                    break;
                case "ban":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toBan = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if (toBan.getVoiceState().inVoiceChannel() && toBan.getVoiceState().getChannel().getId().equals(voiceChannel.getId())) {
                            voiceChannel.getGuild().kickVoiceMember(toBan).queue();
                            //TODO: úspěch hláška
                        }
                        voiceChannel.getManager().getChannel().createPermissionOverride(toBan).setDeny(Permission.VIEW_CHANNEL).queue();
                    } else {
                        //TODO: error hláška
                    }
                case "bitrate":
                    try {
                        if (args.length == 2) {
                            voiceChannel.getManager().setBitrate(Integer.parseInt(args[1])).queue();
                            //TODO: úspěch hláška
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        //TODO: error hláška
                    }
                    break;
                case "name":
                    if (args.length == 2) {
                        voiceChannel.getManager().setName(args[1]).queue();
                        //TODO: úspěch hláška
                    } else {
                        //TODO: error hláška
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
