package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Cmarchiv implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {

            String url = "";
            int id = 0;

            try {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                    conn = Sussi.getInstance().getSql().getPool().getConnection();
                    ps = conn.prepareStatement("SELECT * FROM sussi_archiv ORDER BY RAND() LIMIT 1;");
                    ps.executeQuery();
                    if (ps.getResultSet().next()) {
                        url = ps.getResultSet().getString("url");
                        id = ps.getResultSet().getInt("id");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    Sussi.getInstance().getSql().getPool().close(conn, ps, null);
                }

                channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Něco z CM archivu:").setImage(url).setFooter("ID: " + id, null).build()).queue();
            } catch (Exception e) {
                MessageUtils.sendErrorMessage("A sakra chyba, zkus to zachvilku!", channel);
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (sender.getId().equals("177516608778928129") && member.isOwner() || sender.getId().equals("238410025813540865")) {
                try {
                    String messageEdited = message.getContentRaw().replace(",cmarchiv add ", "");
                    Sussi.getInstance().getSql().insertChnge(messageEdited);
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Do archivu úspěsně přidán nový odkaz!").build()).queue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (args[0].equalsIgnoreCase("status")) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("V archivu je celkem **" + Sussi.getInstance().getSql().countArchiv() + "** obrázků.").build()).queue();
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (sender.getId().equals("177516608778928129") && member.isOwner() || sender.getId().equals("238410025813540865")) {
                String id = args[1];
                try {
                    Sussi.getInstance().getSql().delete(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Z archivu byl úspěšně odebrán obrázek s ID: " + id).build()).queue();
            }
        } else {
            String id = args[0];
            String url = "";

            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = Sussi.getInstance().getSql().getPool().getConnection();
                ps = conn.prepareStatement("SELECT url FROM sussi_archiv WHERE id = '" + id + "'");
                ps.executeQuery();
                if (ps.getResultSet().next()) {
                    url =  ps.getResultSet().getString("url");
                }
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.sendErrorMessage("Požadovaný obrázek podle ID nebyl nalezen!", channel);
            } finally {
                Sussi.getInstance().getSql().getPool().close(conn, ps, null);
            }

            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Něco z CM archivu:").setImage(url).setFooter("ID: " + id, null).build()).queue();

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
        return ",cmarchiv - Vygenerování náhodného obrázku\n" +
                ",cmarchiv [ID] - Získání obrázku podle ID\n" +
                ",cmarchiv add [URL] - Přidání obrázku do archivu (Wake, Krosta)\n" +
                ",cmarchiv delete [URL] - Odebrání obrázku z archivu (Wake, Krosta)\n" +
                ",cmarchiv status - Přehled o celkovém počtu dat v archivu";
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
