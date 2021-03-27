package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.EmoteList;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class RoomSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, CommandHook hook, SlashCommandEvent event) {

        String subcommandName = event.getSubcommandName();

        if (subcommandName == null) {
            return;
        }

        VoiceChannel voiceChannel = member.getGuild().getVoiceChannelById(Sussi.getInstance().getSql().getPlayerVoiceRoomIdByOwnerId(sender.getIdLong()));

        if (voiceChannel == null) {
            hook.sendMessage(MessageUtils.getEmbedError().setDescription("Nenacházíš se v žádném z voice kanálů. Připoj se prvně do **Vytvořit voice kanál**.").build()).queue();
            return;
        }

        switch (subcommandName) {
            case "help":
                hook.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu ,room")
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
                hook.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription(":lock: | Místnost **" + voiceChannel.getName() + "** byla uzamknuta.").build()).queue();
                break;
            case "unlock":
                voiceChannel.putPermissionOverride(member.getGuild().getPublicRole()).setAllow(Permission.VOICE_CONNECT).queue();
                hook.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":unlock: | Místnost ** " + voiceChannel.getName() + "** byla odemknuta.").build()).queue();
                break;
            case "add":
                Member toAdd = event.getOption("user").getAsMember();
                if (toAdd == null) {
                    hook.sendMessage(MessageUtils.getEmbedError().setDescription("Zadaný uživatel neexistuje, chyba Discordu?!").build()).queue();
                    return;
                }
                if (toAdd.getId().equals(hook.getEvent().getMember().getId())) {
                    hook.sendMessage(MessageUtils.getEmbedError().setDescription("Sám sebe pozvat nemůžeš, už tam jsi.").build()).queue();
                    return;
                }
                voiceChannel.getManager().getChannel().putPermissionOverride(toAdd).setAllow(Permission.VOICE_CONNECT).queue();
                hook.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(Constants.GREEN_MARK + " | Uživatel " + toAdd.getAsMention() + " byl přidán do voice").build()).queue();
                break;
        }

        System.out.println(event.getSubcommandName());
    }

    @Override
    public String getName() {
        return "room";
    }

    @Override
    public String getDescription() {
        return "Správa voice mistností.";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
