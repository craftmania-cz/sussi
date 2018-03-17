package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.awt.*;

public class CheckIP implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            //HELP
        } else {

            String ip = args[0];
            if(!(isIP(ip))){
                MessageUtils.sendErrorMessage("Zadaná IP není validativní typ IP!", channel);
                return;
            }

            String countryCode, countryName, isp;
            int block;

            OkHttpClient caller = new OkHttpClient();
            Request request = new Request.Builder().url("http://v2.api.iphub.info/ip/" + ip).addHeader("X-Key", Sussi.getIpHubKey()).build();
            try {
                Response response = caller.newCall(request).execute();
                JSONObject json = new JSONObject(response.body().string());
                countryCode = (String) json.get("countryCode");
                countryName = (String) json.get("countryName");
                isp = (String) json.get("isp");
                block = (int) json.get("block");

                channel.sendMessage(MessageUtils.getEmbed(resolveColor(block)).setTitle("Kontrola IP adresy")
                    .setDescription("**IP**: " + ip + "\n" + "**Země**: " + resolveFlag(countryCode) + " "
                            + countryName + "\n" + "**ISP**: " + isp + "\n" + "**Typ**: " + resolveType(block))
                        .setFooter("Hosting/Proxy mohou být malí poskytovatelé internetu.", null).build()).queue();


            } catch (Exception e){
                MessageUtils.sendErrorMessage("Chyba v API! Zkus to zachvilku...", channel);
                e.printStackTrace();
            }

        }
    }

    @Override
    public String getCommand() {
        return "checkip";
    }

    @Override
    public String getDescription() {
        return "Kontrola IP, zda je VPN/Proxy nebo normální.";
    }

    @Override
    public String getHelp() {
        return ",checkip [ip] - Pro kontrolu.";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    public static boolean isIP(String str) {
        try {
            String[] parts = str.split("\\.");
            if (parts.length != 4) return false;
            for (int i = 0; i < 4; ++i) {
                int p = Integer.parseInt(parts[i]);
                if (p > 255 || p < 0) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Color resolveColor(int type){
        if(type == 0){
            return Constants.GREEN;
        } else if (type == 1){
            return Constants.RED;
        } else {
            return Constants.ORANGE;
        }
    }

    private String resolveType(int type){
        if(type == 0){
            return "Bezpečná IP";
        } else if (type == 1){
            return "Proxy/VPN";
        } else {
            return "Hosting/Proxy";
        }
    }

    private String resolveFlag(String country){
        return ":flag_" + country.toLowerCase() + ":";
    }
}
