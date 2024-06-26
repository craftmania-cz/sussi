package cz.wake.sussi.objects;

import cz.wake.sussi.utils.SussiLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Profile {

    // Status
    private int statusId;

    // Data
    private int id = 0;
    private String discriminator = "????";
    private String nick = "";
    private String uuid = "";
    private long registred = 0;
    private long last_online = 0;
    private String last_server = "";
    private boolean online = false;
    private long played_time = 0;

    // Economy
    private int craft_coins = 0;
    private int craft_tokens = 0;
    private int vote_tokens = 0;
    private int karma_points = 0;
    private int quest_points = 0;
    private int event_points = 0;
    private int bug_points = 0;
    private int parkour_points = 0;

    // Ranked
    private int global_level = 1;
    private int survival_level = 1;
    private int survival_experience = 0;
    private int skyblock_level = 1;
    private int skyblock_experience = 0;
    private int creative_level = 1;
    private int creative_experience = 0;
    private int vanilla_level = 1;
    private int vanilla_experience = 0;
    private int stare_servery_level = 1;

    // Discord
    private int discord_messages = 0;
    private int discord_voice = 0;

    // Votes
    private int total = 0;
    private int month = 0;
    private int week = 0;
    private long last_vote = 0;
    private int votePass = 0;

    // Social
    private String status = "";

    private String discordID = "";

    // Groups
    private String globalVIP = "";
    private Long globalVIPexpiry = -1L;
    private ServerVIP globalVIPobj;
    private List<ServerVIP> serverVIPs = new ArrayList<>();
    private boolean hasAnyVIP = false;
    private Map<ServerType, List<ServerVIP>> mappedServerVIPs = new HashMap<>();


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
        if (statusId == 404 || statusId == 500) {
            return;
        }

        // JSON objects
        JSONObject data = json.getJSONObject("data");
        JSONObject economy = data.getJSONObject("economy");
        JSONObject ranked = data.getJSONObject("ranked");
        JSONObject votes = data.getJSONObject("votes");
        JSONObject social = data.getJSONObject("social");
        JSONObject groups = data.getJSONObject("groups").isNull("vip") ? null : data.getJSONObject("groups").getJSONObject("vip");

        // Data
        this.id = data.isNull("id") ? 0 : data.getInt("id");
        this.discriminator = data.isNull("discriminator") ? "????" : data.getString("discriminator");
        this.nick = data.isNull("nick") ? "" : data.getString("nick");
        this.uuid = data.isNull("uuid") ? "" : data.getString("uuid");
        this.registred = data.isNull("registred") ? 0 : data.getLong("registred");
        this.last_online = data.isNull("last_online") ? 0 : data.getLong("last_online");
        this.last_server = data.isNull("last_server") ? "" : data.getString("last_server");
        this.online = data.isNull("is_online") ? false : data.getBoolean("is_online");
        this.played_time = data.isNull("played_time") ? 0 : data.getLong("played_time");

        // Economy
        this.craft_coins = economy.isNull("craft_coins") ? 0 : economy.getInt("craft_coins");
        this.craft_tokens = economy.isNull("craft_tokens") ? 0 : economy.getInt("craft_tokens");
        this.vote_tokens = economy.isNull("vote_tokens") ? 0 : economy.getInt("vote_tokens");
        this.karma_points = economy.isNull("karma_points") ? 0 : economy.getInt("karma_points");
        this.quest_points = economy.isNull("quest_points") ? 0 : economy.getInt("quest_points");
        this.event_points = economy.isNull("event_points") ? 0 : economy.getInt("event_points");
        this.bug_points = economy.isNull("bug_points") ? 0 : economy.getInt("bug_points");
        this.parkour_points = economy.isNull("parkour_points") ? 0 : economy.getInt("parkour_points");

        // Ranked
        this.global_level = ranked.isNull("global_level") ? 0 : ranked.getInt("global_level");
        this.survival_level = ranked.isNull("survival_level") ? 0 : ranked.getInt("survival_level");
        this.survival_experience = ranked.isNull("survival_experience") ? 0 : ranked.getInt("survival_experience");
        this.skyblock_level = ranked.isNull("skyblock_level") ? 0 : ranked.getInt("skyblock_level");
        this.skyblock_experience = ranked.isNull("skyblock_experience") ? 0 : ranked.getInt("skyblock_experience");
        this.creative_level = ranked.isNull("creative_level") ? 0 : ranked.getInt("creative_level");
        this.creative_experience = ranked.isNull("creative_experience") ? 0 : ranked.getInt("creative_experience");
        this.vanilla_level = ranked.isNull("vanilla_level") ? 0 : ranked.getInt("vanilla_level");
        this.vanilla_experience = ranked.isNull("vanilla_experience") ? 0 : ranked.getInt("vanilla_experience");
        this.stare_servery_level = ranked.isNull("old_servers_level") ? 0 : ranked.getInt("old_servers_level");

        // Votes
        this.total = votes.isNull("total") ? 0 : votes.getInt("total");
        this.month = votes.isNull("month") ? 0 : votes.getInt("month");
        this.week = votes.isNull("week") ? 0 : votes.getInt("week");
        this.last_vote = votes.isNull("last_vote") ? 0 : votes.getLong("last_vote");
        this.votePass = votes.isNull("vote_pass") ? 0 : votes.getInt("vote_pass");

        // Social
        this.status = social.isNull("status") ? "" : social.getString("status");

        // Discord
        this.discordID = data.getJSONObject("discord").isNull("id") ? "" : data.getJSONObject("discord").getString("id");
        this.discord_messages = data.getJSONObject("discord").isNull("text_activity") ? 0 : data.getJSONObject("discord").getInt("text_activity");
        this.discord_voice = data.getJSONObject("discord").isNull("voice_activity") ? 0 : data.getJSONObject("discord").getInt("voice_activity");

        // Groups
        if (groups != null) {
            if (groups.length() == 0) return;
            //System.out.println(nick + " " + groups);
            this.globalVIP = groups.isNull("primary") ? null : groups.getString("primary");
            if (VIPType.isValid(globalVIP)) {
                this.globalVIPexpiry = groups.isNull("time") ? null : groups.getLong("time");
                this.globalVIPobj = new ServerVIP(globalVIPexpiry, globalVIP);
            } else globalVIP = null;

            //System.out.println(globalVIP);
            //System.out.println(globalVIPobj);

            for (ServerType vip : ServerType.values()) {
                mappedServerVIPs.put(vip, new ArrayList<>());
            }

            // Nemá global VIP
            if (!groups.isNull("servers")) {
                for (String serverName : groups.getJSONObject("servers").keySet()) {
                    JSONArray serverArray = groups.getJSONObject("servers").getJSONArray(serverName);
                    serverArray.forEach(serverObj -> {
                        JSONObject jsonObject = new JSONObject(serverObj.toString());

                        ServerVIP obj = new ServerVIP(jsonObject.getLong("time"), jsonObject.getString("group"));
                        obj.setServerName(serverName);

                        //System.out.println(obj.toString());

                        serverVIPs.add(obj);
                        mappedServerVIPs.get(obj.getServerType()).add(obj);
                    });
                }

            }
        }
        hasAnyVIP = this.globalVIP != null || !serverVIPs.isEmpty();
    }

    public int getStatusId() {
        return statusId;
    }

    public int getId() {
        return id;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getName() {
        return nick;
    }

    public String getUuid() {
        return uuid;
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

    public int getCraftCoins() {
        return craft_coins;
    }

    public int getCraftTokens() {
        return craft_tokens;
    }

    public int getVoteTokens() {
        return vote_tokens;
    }

    public long getKarmaPoints() {
        return karma_points;
    }

    public int getQuestPoints() {
        return quest_points;
    }

    public int getGlobal_level() {
        return global_level;
    }

    public int getCreative_level() {
        return creative_level;
    }

    public int getSkyblock_level() {
        return skyblock_level;
    }

    public int getSurvival_level() {
        return survival_level;
    }

    public int getVanilla_level() {
        return vanilla_level;
    }

    public int getCreative_experience() {
        return creative_experience;
    }

    public int getSkyblock_experience() {
        return skyblock_experience;
    }

    public int getSurvival_experience() {
        return survival_experience;
    }

    public int getVanilla_experience() {
        return vanilla_experience;
    }

    public int getStare_servery_level() {
        return stare_servery_level;
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

    public int getDiscord_messages() {
        return discord_messages;
    }

    public int getDiscord_voice() {
        return discord_voice;
    }

    public int getVotePass() {
        return votePass;
    }

    public int getEventPoints() {
        return event_points;
    }

    public int getBugPoints() {
        return bug_points;
    }

    public int getParkourPoints() {
        return parkour_points;
    }

    /**
     * Returns player's discord ID, if does not have returns empty string
     *
     * @return Discord ID in string
     * @since 1.11.0
     */
    public String getDiscordID() {
        return discordID;
    }

    /**
     * First check if player really has this Global VIP
     * using {@link Profile#hasGlobalVIP()}
     *
     * @return ServerVIP if exists
     * @since 1.11.0
     */
    @Nullable
    public ServerVIP getGlobalVIP() {
        if (!hasGlobalVIP()) return null;
        return globalVIPobj;
    }

    /**
     * Checks if player has Global VIP
     *
     * @return boolean
     * @since 1.11.0
     */
    public boolean hasGlobalVIP() {
        if (globalVIP == null) return false;
        if (globalVIP.equalsIgnoreCase("")) return false;
        return VIPType.isValid(globalVIP);
    }

    /**
     * Get all player's VIPs, these are not ordered
     *
     * @return List of ServerVIP objects
     * @since 1.11.0
     */
    public List<ServerVIP> getServerVIPs() {
        return serverVIPs;
    }

    /**
     * Returns if player has any VIP including global
     *
     * @return boolean
     * @since 1.11.0
     */
    public boolean hasAnyVIP() {
        return this.hasAnyVIP;
    }

    /**
     * Returns one highest server VIP from each server where
     * player has activated server VIP
     *
     * @return List of Server VIPs
     * @since 1.11.0
     */
    public Set<ServerVIP> getHighestVIPs() {
        Set<ServerVIP> out = new HashSet<>();
        for (ServerType type : ServerType.values()) {
            Optional<ServerVIP> opt = serverVIPs.stream()
                    .filter(serverVIP -> serverVIP.getServerType() == type)
                    .max(Comparator.comparingInt(serverVIP -> serverVIP.getVIPType().priority));
            opt.ifPresent(out::add);
        }
        return out;
    }

    /**
     * Returns the best ServerVIP for each level - gold, emerald, diamond, obsidian.
     * @return List of Server VIPs
     */
    public HashMap<VIPType, ServerVIP> getBestVIPsFromEachLevel() {
        // VIPType : ServerVIP?
        HashMap<VIPType, ServerVIP> out = new HashMap<>();
        for (VIPType vipType : VIPType.values()) {
            if (this.serverVIPs.stream().anyMatch(serverVIP -> serverVIP.getVIPType() == vipType)) {
                for (ServerVIP serverVIP : this.serverVIPs.stream().filter(serverVIP -> serverVIP.getVIPType() == vipType).collect(Collectors.toList())) {
                    if (!out.containsKey(vipType)) {
                        out.put(vipType, serverVIP);
                    } else if (!out.get(vipType).isPermanent() && (serverVIP.isPermanent() || serverVIP.time > out.get(vipType).time)) {
                        out.put(vipType, serverVIP);
                    }
                }
            } else {
                out.put(vipType, null);
            }
        }
        // Check for global VIP
        if (this.getGlobalVIP() != null) {
            final ServerVIP globalVIP = this.getGlobalVIP();
            //System.out.println(globalVIP);
            final VIPType vipType = globalVIP.getVIPType();
            //System.out.println(out.get(vipType));
            if (out.get(vipType) == null) {
                out.put(vipType, globalVIP);
            } else if (!out.get(vipType).isPermanent()
                    && (globalVIP.isPermanent()
                    || globalVIP.time > out.get(vipType).time)) {
                out.put(vipType, globalVIP);
            }
        }
        return out;
    }

    /**
     * Server VIP object that stores server name, expire date
     * and type of VIP
     *
     * @since 1.11.0
     */
    public static class ServerVIP {

        private Long time;
        private String group;
        private String serverName;

        public ServerVIP(Long time, String group) {
            this.time = time;
            this.group = group;
        }

        public Long getTime() {
            return time;
        }

        public String getGroup() {
            return StringUtils.capitalize(group);
        }

        @Nullable
        public Profile.VIPType getVIPType() {
            return Arrays.stream(VIPType.values())
                    .filter(e -> e.name().equalsIgnoreCase(group)).findAny().orElse(null);
        }

        @Nullable
        public Profile.ServerType getServerType() {
            return ServerType.get(serverName);
        }

        public boolean isPermanent() {
            return this.time == 0L;
        }

        public String getServerName() {
            return StringUtils.capitalize(serverName);
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getFormattedDate() {
            if (isPermanent()) return "";
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyy HH:mm");
            return dateTimeFormatter.format(this.time);
        }

        public String getDiscordRoleName() {
            return getGroup() + " VIP";
        }

        @Override
        public String toString() {
            return "ServerVIP{" +
                    "time=" + time +
                    ", group='" + group + '\'' +
                    ", serverName='" + serverName + '\'' +
                    ", vipEnum='" + getVIPType() + '\'' +
                    '}';
        }
    }

    /**
     * All types of VIPs
     *
     * @since 1.11.0
     */
    public static enum VIPType {

        GOLD("gold", 1, "#d4a01d"),
        DIAMOND("diamond", 2, "#87cefa"),
        EMERALD("emerald", 3, "#6cf1c6"),
        OBSIDIAN("obsidian", 4, "#8953ff"),
        AMETHYST("amethyst", 5, "#b32ac9");

        private String groupName;
        private int priority;
        private String colour;

        VIPType(String groupName, int priority, String colour) {
            this.groupName = groupName;
            this.priority = priority;
            this.colour = colour;
        }

        public static boolean isValid(@Nullable String groupName) {
            if (groupName == null) return false;
            for (VIPType vips : values()) {
                if (groupName.equalsIgnoreCase(vips.groupName)) return true;
            }
            return false;
        }

        @Nullable
        public static VIPType getFromRoleName(@Nullable String roleName) {
            if (roleName == null) return null;
            try {
                 return valueOf(roleName.substring(0, roleName.indexOf(' ')).toUpperCase());
            } catch (NullPointerException e) {
                return null;
            }
        }

        public String getDiscordRoleName() {
            return StringUtils.capitalize(groupName) + " VIP";
        }

        public int getPriority() {
            return priority;
        }

        public Color getColor() {
            return Color.decode(colour);
        }
    }

    public static enum ServerType {

        GLOBAL,
        SURVIVAL,
        SKYBLOCK,
        CREATIVE,
        SKYCLOUD,
        PRISON,
        VANILLA;

        ServerType() {}

        @Nullable
        public static Profile.ServerType get(String serverName) {
            for (ServerType vipType : values()) {
                if (vipType.name().equalsIgnoreCase(serverName)) return vipType;
            }
            return null;
        }
    }
}
