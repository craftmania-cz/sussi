package cz.wake.sussi.objects;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.SussiLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Request.Builder;
import org.json.JSONObject;

public class ServerInfo {

    private static JSONObject getJson() {
        try {
            OkHttpClient caller = new OkHttpClient();
            Request request = (new Builder()).url(Sussi.API_URL).build();
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            return json;
        } catch (Exception e) {
            SussiLogger.fatalMessage("Internal error when retrieving data from api!");
            return null;
        }
    }

    public static int getOnlinePlayers() {
        if (getJson() != null) {
            int online;
            JSONObject jsonArray = getJson().getJSONObject("players");
            online = jsonArray.getInt("now");
            return online;
        }
        return 0;
    }

    public static int getMaxOnlinePlayers() {
        if (getJson() != null) {
            int maxOnline;
            JSONObject jsonArray = getJson().getJSONObject("players");
            maxOnline = jsonArray.getInt("max");
            return maxOnline;
        }
        return 0;
    }

    public static boolean getOnlineStatus() {
        if (getJson() != null) {
            boolean isOnline;
            isOnline = getJson().getBoolean("online");
            return isOnline;
        }
        return false;
    }

    public static String getMotd() {
        if (getJson() != null) {
            String motd;
            motd = getJson().getString("motd");
            return motd;
        }
        return "";
    }

    public static String getVersion() {
        if (getJson() != null) {
            String version;
            JSONObject jsonArray = getJson().getJSONObject("server");
            version = jsonArray.getString("name");
            return version;
        }
        return "";
    }
}
