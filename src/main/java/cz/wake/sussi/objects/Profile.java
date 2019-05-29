package cz.wake.sussi.objects;

import cz.wake.sussi.utils.SussiLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class Profile {

    // Status
    private int statusId;

    // Data
    private int id = 0;
    private int discriminator = 0000;
    private String nick = "";
    private String uuid = "";
    private int web_group = 0;
    private long registred = 0;
    private long last_online = 0;
    private String last_server = "";
    private boolean online = false;
    private long played_time = 0;
    private String mc_version = "";

    // Economy
    private int craftcoins = 0;
    private int crafttokens = 0;
    private int votetokens = 0;
    private long karma = 0;
    private int achievment_points= 0;

    // Ranked
    private int level = 0;
    private int experience = 0;
    private double total_experience = 0;

    // Votes
    private int total = 0;
    private int month = 0;
    private int week = 0;
    private long last_vote = 0;

    // Social
    private String status = "";
    private String facebook = "";
    private String twitter = "";
    private String twitch = "";
    private String steam = "";
    private String web = "";

    public Profile(String nick) {
        JSONObject json;
        try {
            OkHttpClient caller = new OkHttpClient();
            Request request = (new Request.Builder()).url("https://api.craftmania.cz/player/" + nick).build();
            Response response = caller.newCall(request).execute();
            json = new JSONObject(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            SussiLogger.fatalMessage("Internal error when retrieving data from api!");
            statusId = 500;
            return;
        }

        // Status
        this.statusId = json.getInt("status");
        if(statusId == 404 || statusId == 500) {
            return;
        }

        // JSON objects
        JSONObject data = json.getJSONObject("data");
        JSONObject economy = data.getJSONObject("economy");
        JSONObject ranked = data.getJSONObject("ranked");
        JSONObject votes = data.getJSONObject("votes");
        JSONObject social = data.getJSONObject("social");

        // Data
        this.id = data.isNull("id") ? 0 : data.getInt("id");
        this.discriminator = data.isNull("discriminator") ? 0 : data.getInt("discriminator");
        this.nick = data.isNull("nick") ? "" : data.getString("nick");
        this.uuid = data.isNull("uuid") ? "" : data.getString("uuid");
        this.web_group = data.isNull("web_group") ? 0 : data.getInt("web_group");
        this.registred = data.isNull("registred") ? 0 : data.getLong("registred");
        this.last_online = data.isNull("last_online") ? 0 : data.getLong("last_online");
        this.last_server = data.isNull("last_server") ? "" : data.getString("last_server");
        this.online = data.isNull("is_online") ? false : data.getBoolean("is_online");
        this.played_time = data.isNull("played_time") ? 0 : data.getLong("played_time");
        this.mc_version = data.isNull("mc_version") ? "" : data.getString("mc_version");

        // Economy
        this.craftcoins = economy.isNull("craftcoins") ? 0 : economy.getInt("craftcoins");
        this.crafttokens = economy.isNull("crafttokens") ? 0 : economy.getInt("crafttokens");
        this.votetokens = economy.isNull("votetokens") ? 0 : economy.getInt("votetokens");
        this.karma = economy.isNull("karma") ? 0 : economy.getLong("karma");
        this.achievment_points = economy.isNull("achievement_points") ? 0 : economy.getInt("achievement_points");

        // Ranked
        this.level = ranked.isNull("level")? 0 : ranked.getInt("level");
        this.experience = ranked.isNull("experience") ? 0 : ranked.getInt("experience");
        this.total_experience = ranked.isNull("total_experience") ? 0 : ranked.getInt("total_experience");

        // Votes
        this.total = votes.isNull("total") ? 0 : votes.getInt("total");
        this.month = votes.isNull("month") ? 0 : votes.getInt("month");
        this.week = votes.isNull("week") ? 0 : votes.getInt("week");
        this.last_vote = votes.isNull("last_vote") ? 0 : votes.getLong("last_vote");

        // Social

        //this.status = social.isNull("status") ? "" : social.getString("status");
        this.facebook = social.isNull("facebook") ? "" : social.getString("facebook");
        this.twitter = social.isNull("twitter") ? "" : social.getString("twitter");
        this.twitch = social.isNull("twitch")  ? "" : social.getString("twitch");
        this.steam = social.isNull("steam")  ? "" : social.getString("steam");
        this.web = social.isNull("web")  ? "" : social.getString("web");
    }

    public int getStatusId() {
        return statusId;
    }

    public int getId() {
        return id;
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public String getName() {
        return nick;
    }

    public String getUuid() {
        return uuid;
    }

    public int getWebGroup() {
        return web_group;
    }

    public long getRegistred() {
        return registred;
    }

    public long getLastOnline() {
        return last_online;
    }

    public String getLastServer() {
        return last_server;
    }

    public boolean isOnline() {
        return online;
    }

    public long getPlayedTime() {
        return played_time;
    }

    public String getMcVersion() {
        return mc_version;
    }

    public int getCraftCoins() {
        return craftcoins;
    }

    public int getCraftTokens() {
        return crafttokens;
    }

    public int getVoteTokens() {
        return votetokens;
    }

    public long getKarma() {
        return karma;
    }

    public int getAchievmentPoints() {
        return achievment_points;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public double getTotalExperience() {
        return total_experience;
    }

    public int getTotalVotes() {
        return total;
    }

    public int getMonthVotes() {
        return month;
    }

    public int getWeekVotes() {
        return week;
    }

    public long getLastVote() {
        return last_vote;
    }

    public String getStatus() {
        return status;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getTwitch() {
        return twitch;
    }

    public String getSteam() {
        return steam;
    }

    public String getWeb() {
        return web;
    }
}
