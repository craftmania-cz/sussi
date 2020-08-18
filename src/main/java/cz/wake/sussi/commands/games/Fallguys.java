package cz.wake.sussi.commands.games;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Fallguys implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        try {
            if (!member.getRoles().contains(member.getGuild().getRoleById("742341243396292688"))) {
                member.getGuild().addRoleToMember(member, member.getGuild().getRoleById("742341243396292688")).queue();
                message.delete().queue();
                MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " nastavil/a sis roli `FallGuys`!", 5000L, channel);
            } else {
                member.getGuild().removeRoleFromMember(member, member.getGuild().getRoleById("742341243396292688")).queue();
                message.delete().queue();
                MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " odebral/a sis roli `FallGuys`!", 5000L, channel);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "fallguys";
    }

    @Override
    public String getDescription() {
        return "Získání role a přístupu do channelu #fallguys";
    }

    @Override
    public String getHelp() {
        return ".";
    }

    @Override
    public CommandType getType() {
        return CommandType.GAME_CHANNEL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}