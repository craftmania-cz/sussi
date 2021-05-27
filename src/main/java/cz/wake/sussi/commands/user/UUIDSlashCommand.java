package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class UUIDSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {

        OptionMapping optionName = event.getOption("name");
        String nameValue = optionName.getAsString();

        JSONObject apiResponse = getApiObject(nameValue);

        if (apiResponse == null) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Chyba v CraftMania API! Zkus to zachvilku....").build()).queue();
            return;
        }

        String onlineUUUID = null;
        try {
            onlineUUUID = apiResponse.getJSONObject("data").getString("original");
        } catch (JSONException ignored) {}
        String offlineUUID = apiResponse.getJSONObject("data").getString("offline");

        if (onlineUUUID != null) {
            onlineUUUID = onlineUUUID.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
        }
        offlineUUID = offlineUUID.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");

        hook.sendMessageEmbeds(MessageUtils.getEmbed().setColor(Constants.RED).setTitle("Přehled UUID: " + nameValue)
            .addField("Online (Mojang)", onlineUUUID != null ? "`" + onlineUUUID + "`" : "Warez hráč - nelze vygenerovat." , false)
            .addField("Offline", "`" + offlineUUID + "`", false).build()).queue();

    }

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String getDescription() {
        return "Získání UUID originálky a warez hráče podle nicku.";
    }

    @Override
    public String getHelp() {
        return "/uuid [nick]";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    private static JSONObject getApiObject(String name) {
        try {
            OkHttpClient caller = new OkHttpClient();
            Request request = (new Request.Builder()).url("https://api.craftmania.cz/mojang/uuid/" + name).build();
            Response response = caller.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            SussiLogger.fatalMessage("Internal error when retrieving data from api!");
            return null;
        }
    }
}
