package cz.wake.sussi.objects;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class HalloweenStatsHandler {

    private static final Gson gson = new Gson();

    @Nullable
    public static HalloweenStats getStatistics(String nick) {
        try {
            // TODO: GET from stats
            OkHttpClient caller = new OkHttpClient();
            Request request = (new Request.Builder()).url("https://api.craftmania.cz/player/" + nick + "/halloween").build();
            Response response = caller.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());

            if (!jsonObject.has("success")) return null;
            if (!jsonObject.getBoolean("success")) return null;

            return gson.fromJson(jsonObject.getJSONObject("data").toString(), HalloweenStats.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class HalloweenStats {
        private RolePreference role_preference = RolePreference.FILL;

        private int games_played = 0;
        private int killer_kills = 0;
        private int killer_downs = 0;
        private int killer_wins = 0;
        private int killer_hits = 0;

        private int survivor_wins = 0;
        private int survivor_generators_powered = 0;
        private int survivor_fuels_filled = 0;
        private int survivor_players_revived = 0;

        private Long playtime = 0L;

        public RolePreference getRole_preference() {
            return role_preference;
        }

        public void setRole_preference(RolePreference role_preference) {
            this.role_preference = role_preference;
        }

        public int getGames_played() {
            return games_played;
        }

        public void setGames_played(int games_played) {
            this.games_played = games_played;
        }

        public int getKiller_kills() {
            return killer_kills;
        }

        public void setKiller_kills(int killer_kills) {
            this.killer_kills = killer_kills;
        }

        public int getKiller_downs() {
            return killer_downs;
        }

        public void setKiller_downs(int killer_downs) {
            this.killer_downs = killer_downs;
        }

        public int getKiller_wins() {
            return killer_wins;
        }

        public void setKiller_wins(int killer_wins) {
            this.killer_wins = killer_wins;
        }

        public int getKiller_hits() {
            return killer_hits;
        }

        public void setKiller_hits(int killer_hits) {
            this.killer_hits = killer_hits;
        }

        public int getSurvivor_wins() {
            return survivor_wins;
        }

        public void setSurvivor_wins(int survivor_wins) {
            this.survivor_wins = survivor_wins;
        }

        public int getSurvivor_generators_powered() {
            return survivor_generators_powered;
        }

        public void setSurvivor_generators_powered(int survivor_generators_powered) {
            this.survivor_generators_powered = survivor_generators_powered;
        }

        public int getSurvivor_fuels_filled() {
            return survivor_fuels_filled;
        }

        public void setSurvivor_fuels_filled(int survivor_fuels_filled) {
            this.survivor_fuels_filled = survivor_fuels_filled;
        }

        public int getSurvivor_players_revived() {
            return survivor_players_revived;
        }

        public void setSurvivor_players_revived(int survivor_players_revived) {
            this.survivor_players_revived = survivor_players_revived;
        }

        public Long getPlaytime() {
            return playtime;
        }

        public void setPlaytime(Long playtime) {
            this.playtime = playtime;
        }
    }

    private enum RolePreference {

        SURVIVOR,
        KILLER,
        FILL

    }
}
