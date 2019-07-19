package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.WhitelistedIP;
import cz.wake.sussi.objects.WhitelistedUUID;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NickWhitelist implements ICommand {

    private Paginator.Builder pBuilder;


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(sender.getId().equals("238410025813540865") || sender.getId().equals("177516608778928129") || sender.getId().equals("211749751475929088") || sender.getId().equals("234700305831428106")) {
            List<WhitelistedUUID> uuids = Sussi.getInstance().getSql().getWhitelistedUUIDs();

            if(args.length < 1) {
                if(uuids.isEmpty()) {
                    MessageUtils.sendErrorMessage("No, nikdo na NickWhitelistu není!", channel);
                    return;
                }

                pBuilder = new Paginator.Builder()
                        .setColumns(1)
                        .setItemsPerPage(10)
                        .showPageNumbers(true)
                        .waitOnSinglePage(false)
                        .useNumberedItems(true)
                        .setFinalAction(m -> {
                            try {
                                m.clearReactions().queue();
                            } catch (PermissionException e) {
                                m.delete().queue();
                            }
                        })
                        .setEventWaiter(w)
                        .setTimeout(1, TimeUnit.MINUTES);

                for(WhitelistedUUID uuid : uuids) {
                    pBuilder.addItems("**" + uuid.getUUID() + "** - " + uuid.getDescription());
                }

                Paginator p = pBuilder.setColor(Color.YELLOW).setText("Seznam hráčů na IPWhitelistu:").build();
                p.paginate(channel, 1);
                return;
            }

            if(args[0].equals("add")) {
                if(args.length < 3) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Nesprávně napsané argumenty! Př. ,nickwl add c4ba6d11-a45e-47cd-8a75-998460056b2b Testovaci Zprava").build()).queue();
                    return;
                }

                try {
                    UUID checkUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávaní příkazu").setDescription("Musíš zadat UUID! Př. ,nickwl add c4ba6d11-a45e-47cd-8a75-998460056b2b Testovaci Zprava").build()).queue();
                    return;
                }

                for(WhitelistedUUID uuid : Sussi.getInstance().getSql().getWhitelistedUUIDs()) {
                    if(args[1].equals(uuid.getUUID())) {
                        channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("UUID Nenalezeno").setDescription("UUID " + args[1] + " již existuje.").build()).queue();
                        return;
                    }
                }

                String description = args[2];

                for(int i = 3; i != args.length; i++) {
                    description += " " + args[i];
                }

                Sussi.getInstance().getSql().addWhitelistedUUID(args[1], description);
                channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("UUID " + args[1] + " bylo úspěšně přidán!").build()).queue();
            }

            if(args[0].equals("remove")) {
                if(args.length < 2) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Nesprávně napsané argumenty! Př. ,nickwl remove c4ba6d11-a45e-47cd-8a75-998460056b2b").build()).queue();
                    return;
                }

                try {
                    UUID checkUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávaní příkazu").setDescription("Musíš zadat UUID! Př. ,nickwl add c4ba6d11-a45e-47cd-8a75-998460056b2b Testovaci Zprava").build()).queue();
                    return;
                }

                for(WhitelistedIP ip : Sussi.getInstance().getSql().getWhitelistedIPs()) {
                    if(args[1].equals(ip.getAddress())) {
                        Sussi.getInstance().getSql().removeWhitelistedUUID(args[1]);
                        channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("UUID " + args[1] + " bylo úspěšně odebráno!").build()).queue();
                        return;
                    }
                }
                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("UUID Nenalezeno").setDescription("UUID " + args[1] + " nebylo nalezeno.").build()).queue();
            }

            if(args[0].equals("check")) {
                if(args.length < 2) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazů").setDescription("Nesprávně napsané argumenty! Př. ,nickwl check c4ba6d11-a45e-47cd-8a75-998460056b2b").build()).queue();
                    return;
                }

                try {
                    UUID checkUUID = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávaní příkazu").setDescription("Musíš zadat UUID! Př. ,nickwl add c4ba6d11-a45e-47cd-8a75-998460056b2b Testovaci Zprava").build()).queue();
                    return;
                }

                for(WhitelistedUUID ip : Sussi.getInstance().getSql().getWhitelistedUUIDs()) {
                    if(args[1].equals(ip.getUUID())) {
                        channel.sendMessage(MessageUtils.getEmbed(Color.yellow).setTitle("Informace o UUID").setDescription("**" + ip.getUUID() + "** - " + ip.getDescription()).build()).queue();
                        return;
                    }
                }
                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("UUID Nenalezeno").setDescription("UUID " + args[1] + " nebylo nalezeno.").build()).queue();
                return;
            }
        } else {
            MessageUtils.sendErrorMessage("Na toto má práva pouze Krosta nebo Kwak!", channel);
            return;
        }
    }

    @Override
        public String getCommand() {
        return "nickwl";
    }

    @Override
    public String getDescription() {
        return "Sprava UUID whitelistu na serveru";
    }

    @Override
    public String getHelp() {
        return ",nickwl - Zobrazí seznam UUID na whitelistu\n" +
                ",nickwl add <ip> <description> - Přidá UUID na whitelist\n" +
                ",nickwl remove <ip> - Odebere UUID z whitelistu\n" +
                ",nickwl check <ip> - Zkontroluje zda je UUID na whitelistu";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
