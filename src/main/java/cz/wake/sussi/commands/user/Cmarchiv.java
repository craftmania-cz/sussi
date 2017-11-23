package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Cmarchiv implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            try{
                channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Něco z CM archivu:").setImage(Sussi.getInstance().getSql().getRandomArchiv()).build()).queue();
            } catch (Exception e){
                MessageUtils.sendErrorMessage("A sakra chyba, zkus to zachvilku!", channel);
            }
        } else {
            if(args[0].equalsIgnoreCase("add")){
                if(sender.getId().equals("177516608778928129") && member.isOwner()){
                    try {
                        String messageEdited = message.getContent().replace(",cmarchiv add ", "");
                        Sussi.getInstance().getSql().insertChnge(messageEdited);
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Do archivu uspesne pridan novy odkaz!").build()).queue();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public String getCommand() {
        return "cmarchiv";
    }

    @Override
    public String getDescription() {
        return "Archív všech failů a vtipných hlášek z CM!";
    }

    @Override
    public String getHelp() {
        return ",cmarchiv";
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
