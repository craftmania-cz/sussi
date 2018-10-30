package cz.wake.sussi.commands.games;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Fortnite implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        try {
            if (!member.getRoles().contains(member.getGuild().getRoleById("430730941728817154"))) {
                member.getGuild().getController().addRolesToMember(member, member.getGuild().getRoleById("430730941728817154")).queue();
                message.delete().queue();
                MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " nastavil/a jsi si roli `Fortnite`!", 5000L, channel);
            } else {
                member.getGuild().getController().removeRolesFromMember(member, member.getGuild().getRoleById("430730941728817154")).queue();
                message.delete().queue();
                MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " odebral/a jsi si roli `Fortnite`!", 5000L, channel);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String getCommand() {
        return "fortnite";
    }

    @Override
    public String getDescription() {
        return "Získání role a přístupu do channelu #fortnite";
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
