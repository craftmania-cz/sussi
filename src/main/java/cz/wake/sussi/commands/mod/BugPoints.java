package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.Profile;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class BugPoints implements ICommand {


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String nick = "";
        switch (args.length) {
            case 0:
                nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
                break;
            case 1:
                nick = args[0];
                break;
            case 3:
                if (sender.getIdLong() == Sussi.getConfig().getOwnerID() || member.getRoles().contains(member.getGuild().getRoleById("649927113964650496")))
                    nick = args[1];
                else {
                    MessageUtils.sendErrorMessage("Na toto má práva jen Wake!", channel);
                    return;
                }
                break;
            default:
                channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setTitle("Bug points - použití").setDescription(getHelp()).build()).queue();
                return;
        }

        Profile profile = new Profile(nick);
        switch (profile.getStatusId()) {
            case 404:
                MessageUtils.sendErrorMessage("Uživatel `" + nick + "` neexistuje", channel);
                break;
            case 200:
                switch (args.length) {
                    case 0:
                        channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Bug points")
                                .setDescription("**Počet tvých bug pointů:** " + Sussi.getInstance().getSql().getBugPoints(nick)).build()).queue();
                        break;
                    case 1:
                        channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Bug points")
                                .setDescription("**Počet bug pointů hráče `" + nick + "`:** " + Sussi.getInstance().getSql().getBugPoints(nick)).build()).queue();
                        break;
                    case 3:
                        if (args[0].toLowerCase().equals("add") && args[2].matches("\\d+")) {
                            Sussi.getInstance().getSql().giveBugPoints(nick, Integer.parseInt(args[2]));
                            channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Bug points")
                                    .setDescription("**Počet bug pointů hráče `" + nick + "` byl navýšen**").build()).queue();
                        } else if (args[0].toLowerCase().equals("remove") && args[2].matches("\\d+")) {
                            Sussi.getInstance().getSql().removeBugPoints(nick, Integer.parseInt(args[2]));
                            channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Bug points")
                                    .setDescription("**Počet bug pointů hráče `" + nick + "` byl snížen**").build()).queue();
                        } else {
                            channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setTitle("Bug points - použití").setDescription(getHelp()).build()).queue();
                        }
                        break;
                }
                break;
            case 500:
                MessageUtils.sendErrorMessage("Nepodařilo se provést akci, zkus to zachvilku...", channel);
                break;
        }
    }

    @Override
    public String getCommand() {
        return "bp";
    }

    @Override
    public String getDescription() {
        return "Body za nahlášení bugů.";
    }

    @Override
    public String getHelp() {
        return ",bp - Zobrazí počet **tvých** bug pointů\n" +
                ",bp <nick> - Zobrazí počet bug pointů určitého hráče\n" +
                ",bp add <nick> <počet> - Přidá určitému hráči zvolený počet bug pointů\n" +
                ",bp remove <nick> <počet> - Odebere určitému hráči zvolený počet bug pointů";
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
