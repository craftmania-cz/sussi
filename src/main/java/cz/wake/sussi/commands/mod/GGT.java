package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.menu.pagination.Paginator;
import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GGT implements ICommand {

    private PaginatorBuilder pBuilder;

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(channel.getId().equals("376149292550979585")){ // #giga_turnaj
            if(sender.getId().equals("208225899726897152") // Kubrastig
                    || sender.getId().equals("177516608778928129") // MrWakeCZ
                    || sender.getId().equals("268721590127034368")){ //COOLPLAY1
                if(args.length < 1){
                    showPlayersOnWhitelist(channel, w);
                } else if (args[0].equalsIgnoreCase("add")){
                    String nick = args[1];
                    if(!Sussi.getInstance().getSql().isOnGGT(nick)){
                        try {
                            Sussi.getInstance().getSql().addtoWhitelist(nick);
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("GGT Whitelist").setDescription("Nick **" + nick + "** byl úspěšně přidán na whitelist serveru.").build()).queue();
                        } catch (Exception e){
                            e.printStackTrace();
                            MessageUtils.sendErrorMessage("Zadaný nick se nepodařilo přidat na GGT Whitelist!", channel);
                        }
                    } else {
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Hráč **" + nick + "** je již na whitelistu!").build()).queue();
                    }
                } else if (args[0].equalsIgnoreCase("remove")){
                    String nick = args[1];
                    if(Sussi.getInstance().getSql().isOnGGT(nick)){
                        try {
                            Sussi.getInstance().getSql().removeFromWhitelist(nick);
                            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("GGT Whitelist").setDescription("Nick **" + nick + "** byl odebrán z whitelistu serveru.").build()).queue();
                        } catch (Exception e){
                            e.printStackTrace();
                            MessageUtils.sendErrorMessage("Zadaný nick se nepodařilo odebrat z GGT Whitelistu!", channel);
                        }
                    } else {
                        MessageUtils.sendErrorMessage("Nikdo takový na whitelistu není!", channel);
                    }
                } else if (args[0].equalsIgnoreCase("streamlink")){
                    String url = args[1];
                    try {
                        Sussi.getInstance().getSql().updateURLData(url);
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Update stream URL").setDescription("Odkaz na stream byl nastaven na: **" + url + "**").build()).queue();
                    } catch (Exception e){
                        e.printStackTrace();
                        MessageUtils.sendErrorMessage("Chyba při updatování odkazu na stream!", channel);
                    }
                } else if (args[0].equalsIgnoreCase("server")){
                    if(args.length > 1){
                        String value = args[1];
                        if(value.equalsIgnoreCase("lock")){
                            Sussi.getInstance().getSql().updateLockData("1");
                            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setDescription("Server byl uzamknut! Nikdo se na něj nedostane!").build()).queue();
                        } else if (value.equalsIgnoreCase("unlock")){
                            Sussi.getInstance().getSql().updateLockData("0");
                            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Server byl odemknut! Kdokoliv je na whitelistu může na server!").build()).queue();
                        } else {
                            MessageUtils.sendErrorMessage("No, to nejde přece... :smile:", channel);
                        }
                    } else {
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Status serveru je: " + getStatus(Sussi.getInstance().getSql().getServerData())).build()).queue();
                    }
                }
            } else {
                MessageUtils.sendErrorMessage("K nastavení GIGA Turnaje potřebuješ práva Eventera nebo Majitele!", channel);
            }
        } else {
            MessageUtils.sendErrorMessage("Toto není channel pro nastavení turnaji, použij channel #giga_turnaj", channel);
        }
    }

    @Override
    public String getCommand() {
        return "turnaj";
    }

    @Override
    public String getDescription() {
        return "Příkaz správu turnaj serverů.";
    }

    @Override
    public String getHelp() {
        return ",turnaj - Zobrazí seznam hráčů na whitelistu\n" +
                ",turnaj add [nick] - Přidá hráče na server\n" +
                ",turnaj remove [nick] - Odebere hráče z serveru\n" +
                ",turnaj streamlink [odkaz] - Nastaví odkaz na Stream\n" +
                ",turnaj server [LOCK/UNLOCK] - Změní v lobby status serveru";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private String getStatus(String state){
        if(state.equalsIgnoreCase("1")){
            return "LOCK";
        }
        return "UNLOCK";
    }

    private void showPlayersOnWhitelist(MessageChannel channel, EventWaiter w){
        List<String> list = Sussi.getInstance().getSql().getPlayersOnWhitelist();

        if(list.isEmpty()){
            MessageUtils.sendErrorMessage("No, nikdo na whitelistu není!", channel);
            return;
        }

        pBuilder = new PaginatorBuilder().setColumns(1)
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

        Paginator p = pBuilder.setColor(Constants.DEV).setText("Seznam hráčů na GGT whitelistu:").build();
        p.paginate(channel,1);
    }
}
