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
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("IP zkontroluješ v následovně: `,checkip [IPv4]` - Například: `,checkip 8.8.8.8`").build()).queue();
        } else {

            String ip = args[0];
            checkIP(ip, channel);
        }
    }

    public void checkIP(String ip, MessageChannel channel){

        if(!(isIP(ip))){
            MessageUtils.sendErrorMessage("Zadaná IP není validativní typ IP!", channel);
            return;
        }

        boolean vpn;
        String hostName, stringOrg, countryCode, countryName;
        Object city;

        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("http://api.vpnblocker.net/v2/json/" + ip + "/" + Sussi.getIpHubKey()).build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());

            vpn = (boolean) json.get("host-ip");
            hostName = (String) json.get("hostname");
            stringOrg = (String) json.get("org");
            city = json.get("city");

            System.out.println(city);

            JSONObject countyObject = json.getJSONObject("country");

            countryCode = (String) countyObject.get("code");
            countryName = (String) countyObject.get("name");


            channel.sendMessage(MessageUtils.getEmbed(resolveColor(vpn)).setTitle("Kontrola IP adresy")
                    .setDescription("**IP**: " + ip + "\n" + "**Země**: " + resolveFlag(countryCode) + " "
                            + countryName + "\n" + "**ISP**: " + stringOrg + "\n" + "**Host**: " + hostName + "\n" + "**Město**: " + resolveCity(city) + "\n" + "**Typ**: " + resolveType(vpn))
                    .build()).queue();


        } catch (Exception e){
            MessageUtils.sendErrorMessage("Chyba v API! Zkus to zachvilku...", channel);
            e.printStackTrace();
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
        return ",checkip [IP] - Pro kontrolu.";
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

    private Color resolveColor(boolean type){
        if(!type){
            return Constants.GREEN;
        } else {
            return Constants.RED;
        }
    }

    private String resolveType(boolean type){
        if(!type){
            return "Bezpečná IP";
        } else {
            return "Proxy/VPN";
        }
    }

    private String resolveFlag(String country){
        return ":flag_" + country.toLowerCase() + ":";
    }

    private String resolveCity(Object city){
        if(city == JSONObject.NULL){
            return "Nenalezeno";
        }
        return (String)city;
    }
}
