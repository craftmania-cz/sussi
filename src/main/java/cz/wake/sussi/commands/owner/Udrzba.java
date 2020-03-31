package cz.wake.sussi.commands.owner;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Udrzba implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(sender.getId().equals(Sussi.getConfig().getOwnerID())){
            if (args.length == 0) {
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - udrzba :question:")
                        .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                return;
            }

            String name = args[0];

            if (!Sussi.getInstance().getSql().isExistServer(name.toLowerCase())) {
                MessageUtils.sendErrorMessage("Tento server není zapsaný v databázi!", channel);
                return;
            }

            if (Sussi.getInstance().getSql().isMaintenance(name.toLowerCase())) {
                Sussi.getInstance().getSql().updateMaintenance(name.toLowerCase(), 0);
                channel.sendMessage("Údržba pro server `" + name + "` byla vypnuta.").queue();
            }
            else {
                Sussi.getInstance().getSql().updateMaintenance(name.toLowerCase(), 1);
                channel.sendMessage("Údržba pro server `" + name + "` byla zapnuta.").queue();
            }
        }
    }

    @Override
    public String getCommand() {
        return "udrzba";
    }

    @Override
    public String getDescription() {
        return "Zapnutí/Vypnutí údržby na serverech";
    }

    @Override
    public String getHelp() {
        return ",udrzba <server> - Zapnutí/Vypnutí údržby na serveru";
    }

    @Override
    public CommandType getType() {
        return CommandType.BOT_OWNER;
    }

    @Override
    public Rank getRank() {
        return Rank.BOT_OWNER;
    }
}
