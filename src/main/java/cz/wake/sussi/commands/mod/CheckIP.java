package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(ip.equalsIgnoreCase("wake")){
            channel.sendMessage("<:nelsonHAHA:267710889338077184>").queue();
            return;
        }

        if(!(isIP(ip) || isIPv6(ip))){
            MessageUtils.sendErrorMessage("Zadaná IP není validativní typ IP!", channel);
            return;
        }

        boolean vpn;
        String hostName = null, stringOrg, countryCode, countryName;
        Object city;

        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.vpnblocker.net/v2/json/" + ip + "/" + Sussi.getIpHubKey()).build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());

            vpn = (boolean) json.get("host-ip");
            try {
                hostName = (String) json.get("hostname");
            } catch (Exception e){
                EmbedBuilder eb = new EmbedBuilder();
                StringBuilder string = new StringBuilder();

                eb.setColor(Constants.ADMIN);
                string.append("Zadaná IP není v databázi nebo neexistuje!");
                string.append("\n\n**Report**\n```" + json.toString() + "```");
                eb.setDescription(string);
                channel.sendMessage(eb.build()).queue();
                return;
            }
            stringOrg = (String) json.get("org");
            city = json.get("city");

            JSONObject countyObject = json.getJSONObject("country");

            countryCode = (String) countyObject.get("code");
            countryName = (String) countyObject.get("name");

            if (city == JSONObject.NULL){
                city = "Nenalezeno";
            }

             if (stringOrg == JSONObject.NULL){
                stringOrg = "Nenalezeno";
             }

            channel.sendMessage(MessageUtils.getEmbed(resolveColor(vpn)).setTitle("Kontrola IP adresy")
                    .setDescription("**IP**: " + ip + "\n" + "**Země**: " + resolveFlag(countryCode) + " "
                            + countryName + "\n" + "**ISP**: " + stringOrg + "\n" + "**Host**: " + hostName + "\n" + "**Město**: " + city + "\n" + "**Typ**: " + resolveType(vpn))
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

    private boolean isIPv6(String ip){
        Pattern p = Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))");
        Matcher matcher = p.matcher(ip);
        return matcher.find();
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
}
