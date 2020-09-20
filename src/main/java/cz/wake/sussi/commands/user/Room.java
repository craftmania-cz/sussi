package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Room implements ICommand {
    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {



        if (Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()) == 0) {
            MessageUtils.sendErrorMessage("Nemáš žádnou místnost! Vytvoříš si ji připojením do kanálu **Vytvořit voice kanál**", channel);
            return;
        }

        VoiceChannel voiceChannel = member.getGuild().getVoiceChannelById(Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()));
        if (args.length < 1) {

            List<PermissionOverride> topKek = voiceChannel.getRolePermissionOverrides();
            PermissionOverride publicRole = topKek.get(0);
            boolean locked = publicRole.getAllowed().contains(Permission.VOICE_CONNECT);

            Member voiceOwner = member.getGuild().getMemberById(Sussi.getInstance().getSql().getPlayerVoiceOwnerIdByRoomId(voiceChannel.getIdLong()));

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Informace o voice room: " + voiceChannel.getName()).setColor(Constants.LIGHT_BLUE)
                    .addField("Základní informace",
                                    "Majitel: " + voiceOwner.getAsMention() + "\n" +
                                    "Zamknuto: " + (locked ? "Ne" : "Ano") + "\n" +
                                    "Limit: " + (voiceChannel.getUserLimit() == 0 ? "Bez limitu" : voiceChannel.getUserLimit()) + "\n" +
                                    "Bitrate: " + (voiceChannel.getBitrate()/1000) + "kbps", false)
                    .setFooter("K změně nastavení místnosti použij ,room nebo ,room help příkaz.");

            List<PermissionOverride> bannedList = voiceChannel.getMemberPermissionOverrides();
            StringBuilder banned = new StringBuilder();
            System.out.println(bannedList.size());
            if(bannedList.size() > 0) {
                boolean canShow = false;
                for (PermissionOverride perm : bannedList) {
                    if(perm.getDenied().contains(Permission.VIEW_CHANNEL)) {
                        banned.append("<@").append(perm.getMember().getIdLong()).append(">\n");
                        canShow = true;
                    }
                }
                if (canShow) {
                    embedBuilder.addField("Zabanovaní uživatelé", banned.toString(), false);
                }
            }

            List<PermissionOverride> addedList = voiceChannel.getMemberPermissionOverrides();
            StringBuilder added = new StringBuilder();
            if(addedList.size() > 0) {
                for(PermissionOverride perm : addedList) {
                    if(perm.getAllowed().contains(Permission.VOICE_CONNECT)) {
                        added.append("<@").append(perm.getMember().getIdLong()).append(">\n");
                    }
                }
                embedBuilder.addField("Přidaní uživatelé", added.toString(), false);
            }

            channel.sendMessage(embedBuilder.build()).queue();
        } else {
            switch (args[0]) {
                case "help":
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu ,room")
                            .setDescription("`,room` - Zobrazí informace o místnosti.\n" +
                                    "`,room lock` - Uzamkne místnost.\n" +
                                    "`,room unlock` - Odemkne místnost.\n" +
                                    "`,room add @uživatel` - Přidá uživatele do místnosti.\n" +
                                    "`,room remove @uživatel` - Odebere uživatele z místnosti.\n" +
                                    "`,room ban @uživatel` - Zabanuje uživatele v místnosti + skryje.\n" +
                                    "`,room unban @uživatel` - Odbanuje uživatele v místnosti.\n" +
                                    "`,room name [text]` - Nastaví název místnosti.\n" +
                                    "`,room limit [číslo]` - Nastaví limit místnosti.\n" +
                                    "`,room unlimited` - Nastaví neomezený počet připojení\n" +
                                    "`,room bitrate [číslo v kbps]` - Nastaví bitrate v místnosti.").build()).queue();
                    break;
                case "lock":
                    voiceChannel.putPermissionOverride(member.getGuild().getPublicRole()).setDeny(Permission.VOICE_CONNECT).queue();
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription(":lock: | Místnost **" + voiceChannel.getName() + "** byla uzamknuta.").build()).queue();
                    break;
                case "unlock":
                    voiceChannel.putPermissionOverride(member.getGuild().getPublicRole()).setAllow(Permission.VOICE_CONNECT).queue();
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":unlock: | Místnost ** " + voiceChannel.getName() + "** byla odemknuta.").build()).queue();
                    break;
                case "add":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toAdd = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if(toAdd.getIdLong() == sender.getIdLong()) {
                            MessageUtils.sendErrorMessage("Nemůžeš přidat sám sebe! Jsi majitel místnosti.", channel);
                            break;
                        }
                        voiceChannel.getManager().getChannel().putPermissionOverride(toAdd).setAllow(Permission.VOICE_CONNECT).queue();
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(Constants.GREEN_MARK + " | Uživatel " + toAdd.getAsMention() + " byl přidán do voice").build()).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš přidat!", channel);
                    }
                    break;
                case "remove":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toRemove = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if(toRemove.getIdLong() == sender.getIdLong()) {
                            MessageUtils.sendErrorMessage("Nemůžeš odebrat sám sebe! Jsi majitel místnosti.", channel);
                            break;
                        }
                        voiceChannel.getManager().getChannel().putPermissionOverride(toRemove).setDeny(Permission.VOICE_CONNECT).queue();
                        channel.sendMessage(MessageUtils
                                .getEmbed(Constants.GREEN).setDescription(Constants.GREEN_MARK + " | Uživatel " + toRemove.getAsMention() + " byl odebrán z kanálu").build()).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš odebrat!", channel);
                    }
                    break;
                case "limit":
                    try {
                        if (args.length == 2) {
                            int count = Integer.parseInt(args[1]);
                            voiceChannel.getManager().setUserLimit(count).queue();
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":memo: | Limit byl změněn na **" + count + "**").build()).queue();
                        } else {
                            MessageUtils.sendErrorMessage("Nesprávně zadaný příkaz: `,room limit [číslo]", channel);
                        }
                    } catch (Exception e) {
                        MessageUtils.sendErrorMessage("Limit je ve špatném formátu! `,room limit [číslo]", channel);
                    }
                    break;
                case "unlimited":
                    try {
                        voiceChannel.getManager().setUserLimit(0).queue();
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":memo: | Limit byl změněn na **neomezeně**").build()).queue();
                    } catch (Exception e) {
                        MessageUtils.sendErrorMessage("Limit je ve špatném formátu! `,room unlimited", channel);
                    }
                    break;
                case "kick":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toKick = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if(toKick.getIdLong() == sender.getIdLong()) {
                            MessageUtils.sendErrorMessage("Nemůžeš vyhodit sám sebe! Proč bys to dělal?", channel);
                        } else if (toKick.getVoiceState().inVoiceChannel() && toKick.getVoiceState().getChannel().getId().equals(voiceChannel.getId())) {
                            voiceChannel.getGuild().kickVoiceMember(toKick).queue();
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(Constants.DELETE + " Uživatel " + toKick.getAsMention() + " byl vykopnut.").build()).queue();
                        } else {
                            MessageUtils.sendErrorMessage("Uživatel není ve stejném kanálu.", channel);
                        }
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš vykopnout!", channel);
                    }
                    break;
                case "ban":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toBan = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        if(toBan.getIdLong() == sender.getIdLong()) {
                            MessageUtils.sendErrorMessage("Nemůžeš zabanovat sám sebe!", channel);
                            break;
                        }
                        if (toBan.getVoiceState().inVoiceChannel() && toBan.getVoiceState().getChannel().getId().equals(voiceChannel.getId())) {
                            voiceChannel.getGuild().kickVoiceMember(toBan).queue();
                        }
                        voiceChannel.getManager().getChannel().putPermissionOverride(toBan).setDeny(Permission.VIEW_CHANNEL).queue();
                        channel.sendMessage(MessageUtils.getEmbed(Constants.ADMIN).setDescription(":hammer: | Uživatel " + toBan.getAsMention()  + " byl zabanován v kanálu.").build()).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš zabanovat!", channel);
                    }
                    break;
                case "unban":
                    if (args.length == 2 || message.getMentions(Message.MentionType.USER).size() > 0) {
                        Member toUnban = member.getGuild().getMemberById(message.getMentions(Message.MentionType.USER).get(0).getId());
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription(":hammer_pick: | Uživatel " + toUnban.getAsMention()  + " byl odbanován z kanálu, nyní se může připojit.").build()).queue();
                        voiceChannel.getManager().getChannel().putPermissionOverride(toUnban).setAllow(Permission.VIEW_CHANNEL).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Musíš označit uživatele, kterého chceš zabanovat!", channel);
                    }
                    break;
                case "bitrate":
                    try {
                        if (args.length == 2) {
                            int bitrate = Integer.parseInt(args[1]);
                            if (bitrate < 8 || bitrate > 384) {
                                MessageUtils.sendErrorMessage("Bitrate kanálu může být nastaven pouze od 8kbps do 384kbps.", channel);
                                break;
                            }
                            voiceChannel.getManager().setBitrate(bitrate * 1000).queue();
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription(":headphones: | Bitrate byl změněn na **" + bitrate + "kbps**").build()).queue();
                        } else {
                            MessageUtils.sendErrorMessage("Špatně zadaný příkaz: `,room bitrate [kbps]`", channel);
                        }
                    } catch (Exception e) {
                        MessageUtils.sendErrorMessage("Bitrate je ve špatném formátu! `,room bitrate [kbps]`", channel);
                    }
                    break;
                case "name":
                    if (args.length >= 1) {
                        List<String> list = Arrays.asList(args);
                        list = list.subList(1, list.size());
                        String name = String.join(" ", list);
                        voiceChannel.getManager().setName(name).complete();
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription(":bookmark: | Název kanálu byl změněn na ** " + name + "**").build()).queue();
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
        return "Správa voice mistností.";
    }

    @Override
    public String getHelp() {
        return ",room - Zobrazí informace o místnosti.\n" +
                ",room lock - Uzamkne místnost.\n" +
                ",room unlock - Odemkne místnost.\n" +
                ",room add @uživatel - Přidá uživatele do místnosti.\n" +
                ",room remove @uživatel - Odebere uživatele z místnosti.\n" +
                ",room ban @uživatel - Zabanuje uživatele v místnosti.\n" +
                ",room unban @uživatel - Odbanuje uživatele v místnosti.\n" +
                ",room name [text] - Nastaví název místnosti.\n" +
                ",room limit [číslo] - Nastaví limit místnosti.\n" +
                ",room bitrate [číslo v kbps] - Nastaví bitrate v místnosti."; // unlimited? 
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
