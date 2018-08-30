package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.RewardPlayer;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Dotaznik implements ICommand {

    private Paginator.Builder pBuilder;

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(sender.getId().equals("238410025813540865") || sender.getId().equals("177516608778928129")){
            if(args.length < 1){
                channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu ,dotaznik")
                        .setDescription(getHelp()).build()).queue();
                return;
            }
            if(args[0].equalsIgnoreCase("add")){
                if(args[1] == null){
                    MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Zkus to takto: `,dotaznik add nick`", channel);
                } else {
                    String nick = args[1];
                    if(!Sussi.getInstance().getSql().hasActiveReward(nick)){
                        try {
                            Sussi.getInstance().getSql().addToRewardList(nick);
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Hráč **" + nick + "** byl přidán na seznam.").build()).queue();
                        } catch (Exception e){
                            MessageUtils.sendErrorMessage("Nepodařilo se provést akci, zkus to zachvilku...", channel);
                        }
                    } else {
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Hráč **" + nick + "** je již v databázi hráčů s odměnou.").build()).queue();
                    }
                }
            } else if (args[0].equalsIgnoreCase("remove")){
                if(args[1] == null){
                    MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Zkus to takto: `,dotaznik remove nick`", channel);
                } else {
                    String nick = args[1];
                    if(Sussi.getInstance().getSql().hasActiveReward(nick)){
                        try {
                            Sussi.getInstance().getSql().removeFromRewardList(nick);
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Hráč **" + nick + "** byl odebrán z seznamu.").build()).queue();
                        } catch (Exception e){
                            MessageUtils.sendErrorMessage("Nepodařilo se provést akci, zkus to zachvilku...", channel);
                        }
                    } else {
                        MessageUtils.sendErrorMessage("Hráč **" + nick + "** není na seznamu, abys ho mohl smazat musíš ho přidat!", channel);
                    }
                }
            } else if (args[0].equalsIgnoreCase("status")){
                String nick = args[1];
                try {
                    RewardPlayer rp = Sussi.getInstance().getSql().getRewardPlayer(nick);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Stav hráče " + nick);
                    eb.setDescription("Informace o tom, zda si hráč vybral odměnu nebo ne pomocí příkazu `/vyzvednout`");
                    eb.addField("Vybrano", rp.isVybrano() ? "Ano" : "Ne", true);
                    eb.addField("Server", rp.getServer() != null ? rp.getServer() : "Žádný", true);
                    eb.addField("Čas vybrání", rp.getTime() == 0 ? "Nevybráno" : getDate(rp.getTime()), true);
                    channel.sendMessage(eb.build()).queue();
                } catch (Exception e){
                    MessageUtils.sendErrorMessage("Nepodařilo se zjistit stav hráče, asi není na seznamu.", channel);
                }

            } else if (args[0].equalsIgnoreCase("list")){
                showPlayersOnWhitelist(channel, w);
            } else {
                channel.sendMessage(":oliznuTe:").queue();
            }
        } else {
            channel.sendMessage("Na toto mají právo pouze Krosta a Kwak!").queue();
        }
    }

    @Override
    public String getCommand() {
        return "dotaznik";
    }

    @Override
    public String getDescription() {
        return ".";
    }

    @Override
    public String getHelp() {
        return "**,dotaznik** - Zobrazí nápovědu\n" +
                "**,dotaznik add [nick]** - Přidá hráče na seznam\n" +
                "**,dotaznik remove [nick]** - Odebere hráče z seznamu\n" +
                "**,dotaznik status [nick]** - Zobrazí status, zda vybral nebo čeká\n" +
                "**,dotaznik list** - Zobrazí všechny hráče, kterým jde příkaz";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private void showPlayersOnWhitelist(MessageChannel channel, EventWaiter w){
        List<String> list = Sussi.getInstance().getSql().getPlayersOnRewardList();

        if(list.isEmpty()){
            MessageUtils.sendErrorMessage("No, nikdo na seznamu není!", channel);
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

        for (String name : list){
            pBuilder.addItems(name);
        }

        Paginator p = pBuilder.setColor(Constants.DEV).setText("Seznam hráčů, kterým jde příkaz `/vyzvednout`:").build();
        p.paginate(channel,1);
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }
}
