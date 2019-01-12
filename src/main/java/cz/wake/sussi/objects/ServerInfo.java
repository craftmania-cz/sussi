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
            Request request = (new Builder()).url("https://api.mcsrvstat.us/1/mc.craftmania.cz").build();
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            SussiLogger.fatalMessage("Internal error when retrieving data from api!");
            return null;
        }
    }

    public static int getOnlinePlayers() {
        if (getJson() != null) {
            try {
                JSONObject jsonArray = getJson().getJSONObject("players");
                return jsonArray.getInt("online");
            } catch(NullPointerException e){
                SussiLogger.dangerMessage("Chyba při zjištění online počtu hráčů.");
                return 0;
            }
        }
        return 0;
    }

    public static int getMaxOnlinePlayers() {
        if (getJson() != null) {
            JSONObject jsonArray = getJson().getJSONObject("players");
            return jsonArray.getInt("max");
        }
        return 0;
    }

    public static boolean getOnlineStatus() {
        if (getJson() != null) {
            return getJson().getBoolean("online");
        }
        return false;
    }

    public static String getMotd() {
        if (getJson() != null) {
            return getJson().getString("motd");
        }
        return "";
    }

    public static String getVersion() {
        if (getJson() != null) {
            JSONObject jsonArray = getJson().getJSONObject("server");
            return jsonArray.getString("name");
        }
        return "";
    }
}
