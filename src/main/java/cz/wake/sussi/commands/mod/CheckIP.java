package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.LoadingProperties;
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

        if(!(isIP(ip) || isIPv6(ip))){
            MessageUtils.sendErrorMessage("Zadaná IP není validativní typ IP!", channel);
            return;
        }

        boolean vpn = false;
        String provider = "Unknown";
        String countryCode = "Unknown";
        String countryName = "Unknown";
        String type = null;
        int risk = 0;
        Object city;

        LoadingProperties properties = new LoadingProperties();

        String finalUrl = "https://proxycheck.io/v2/" + ip + "?key=" + properties.getProxycheckKey()  + "&vpn=1&asn=1&node=1&time=1&inf=0&risk=1&port=1&seen=1&days=7&tag=Sussi_kontrola";

        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url(finalUrl).build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONObject adressInfo = json.getJSONObject(ip);

            System.out.println(adressInfo);

            // Embed Builder
            EmbedBuilder eb = new EmbedBuilder();
            StringBuilder text = new StringBuilder();

            if (adressInfo.get("proxy").equals("yes")) { // Kdyz je proxy true, tak se jedná o VPN/Proxy.
                vpn = true;
            }

            // Nezjistitelna IP?
            if (adressInfo.get("isocode") == JSONObject.NULL) {
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Kontrola IP adresy")
                    .setDescription("Tato IP adresa je nezjistitelná.").build()).queue();
                return;
            }

            // Standartní setup pro všechny kontroly
            text.append("**IP:** " + ip + "\n");

            if (adressInfo.has("provider")) {
                provider = (String) adressInfo.get("provider");
                text.append("**Provider:** " + provider + "\n");
            }
            if (adressInfo.has("country") && adressInfo.has("isocode")) {
                countryName = (String) adressInfo.get("country");
                countryCode = (String) adressInfo.get("isocode");
                text.append("**Země:** " + resolveFlag(countryCode) + " " + countryName + "\n");
            }

            if (adressInfo.has("city")) {
                city = (String) adressInfo.get("city");
                text.append("**Město:** " + city + "\n");
            }

            if (adressInfo.has("risk")) {
                risk = (int) adressInfo.get("risk");
                text.append("**Risk:** " + risk + "% \n");
            }

            if (vpn) {
                type = (String) adressInfo.get("type");
                if (type.equalsIgnoreCase("vpn")) { // Zda je IP VPN
                    eb.setColor(Constants.ADMIN);
                    text.append("**VPN:** Ano");


                } else { // IP je Proxy
                    eb.setColor(Constants.MANAGER);
                    text.append("**Proxy:** Ano");
                }
            } else {
                // Neni Proxy/VPN
                eb.setColor(Constants.GREEN);
                text.append("**Proxy/VPN:** Ne");

            }

            channel.sendMessage(eb.setAuthor("Kontrola IP adresy").setDescription(text).build()).queue();

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

    private String resolveFlag(String country){
        return ":flag_" + country.toLowerCase() + ":";
    }
}
