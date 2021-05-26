package cz.wake.sussi.commands.mod;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.ConfigProperties;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class CheckIpSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {
        if (event.getOption("ip") != null) {
            String requestedIP = event.getOption("ip").getAsString();
            checkIP(requestedIP,hook);
            return;
        }

        if (event.getOption("name") != null) {
            String requestedName = event.getOption("name").getAsString();
            String playerIP = Sussi.getInstance().getSql().getIPFromServerByPlayer(requestedName);
            if (playerIP != null) {
                checkIP(playerIP, hook);
                return;
            }
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Pro zadaný nick nebyla nelezena žádná IP!").build()).queue();
            return;
        }
        hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Špatně zadaný příkaz! Musíš takhle `/checkip [nick/IP]`. Př. `/checkip 8.8.8.8`").build()).queue();
    }

    @Override
    public String getName() {
        return "checkip";
    }

    @Override
    public String getDescription() {
        return "Získání informace o IP adrese hráče.";
    }

    @Override
    public String getHelp() {
        return "/checkip [nick/IP]";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    public void checkIP(String ip, InteractionHook hook){

        boolean vpn = false;
        String provider = "Unknown";
        String countryCode = "Unknown";
        String countryName = "Unknown";
        String type = null;
        int risk = 0;
        Object city;

        ConfigProperties properties = new ConfigProperties();

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
                hook.sendMessageEmbeds(MessageUtils.getEmbed().setTitle("Kontrola IP adresy")
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

            hook.sendMessageEmbeds(eb.setAuthor("Kontrola IP adresy").setDescription(text).build()).queue();

        } catch (Exception e){
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Chyba v API! Zkus to zachvilku...").build()).queue();
            e.printStackTrace();
        }
    }

    private String resolveFlag(String country){
        return ":flag_" + country.toLowerCase() + ":";
    }
}
