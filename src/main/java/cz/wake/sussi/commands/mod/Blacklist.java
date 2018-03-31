package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.menu.pagination.Paginator;
import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.BlacklistName;
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

public class Blacklist implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Constants.DARK_GRAY).setTitle("Blacklist hráčů")
                .setDescription("Blacklist, je seznam všech hráčům kteří opakovaně porušovali pravidla serveru nebo dělali různé akce proti serveru.\nPokuď se tedy nachází v tomto seznamu, jejich bany jsou permanentní do doby, než kompletní AT odhlasuje jejich unban - zatím nikdy.").build()).queue();
        } else if (args[0].equalsIgnoreCase("list")){
            showPlayersInBlacklist(channel, w);
        } else {
            String name = args[0];

            BlacklistName player = Sussi.getInstance().getSql().getBlacklistedPlayer(name);

            if(player == null){
                MessageUtils.sendErrorMessage("Zadaný hráč není v blacklistu, nebo chyba připojení.", channel);
                return;
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Blacklist: " + name);
            embed.setColor(Constants.getRandomColor());
            if(player.getReason() != null){
                embed.addField("Popis", player.getReason(), false);
            }
            if(player.getBannedBy() != null){
                embed.addField("Zabanoval", player.getBannedBy(), true);
            }
            embed.addField("Začátek banu", getDate(player.getTimeStart()), true);
            embed.addField("Konec banu", resolveEnd(player.getTimeEnd()), true);
            embed.setFooter("Pokuď je hráč v blacklistu, je blokovaný po celém serveru.", null);

            channel.sendMessage(embed.build()).queue();

        }
    }

    @Override
    public String getCommand() {
        return "blacklist";
    }

    @Override
    public String getDescription() {
        return "Seznam hráčů, kteří jsou v blacklistu.";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private String resolveEnd(long time){
        if(time == 0){
            return "Permanentní";
        }
        return getDate(time);
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy").format(cal.getTime());
        return timeString;
    }

    private void showPlayersInBlacklist(MessageChannel channel, EventWaiter w){
        List<String> list = Sussi.getInstance().getSql().getPlayersInBlacklist();

        if(list.isEmpty()){
            MessageUtils.sendErrorMessage("Nikdo na blacklistu není, můžeme odletět na Mars!", channel);
            return;
        }

        PaginatorBuilder pBuilder = new PaginatorBuilder().setColumns(1)
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

        Paginator p = pBuilder.setColor(Constants.DARK_GRAY).setText("Seznam hráčů v blacklistu:").build();
        p.paginate(channel,1);
    }
}
