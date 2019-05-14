package cz.wake.sussi.objects;

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
            Request request = (new Builder()).url("https://api.craftmania.cz/server/playercount").build();
            Response response = caller.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            SussiLogger.fatalMessage("Internal error when retrieving data from api!");
            return null;
        }
    }

    public static int getOnlinePlayers() {
        if (getJson() != null) {
            try {
                JSONObject jsonArray = getJson().getJSONObject("data").getJSONObject("players");
                if (jsonArray.get("online") != null) {
                    return Integer.parseInt(jsonArray.get("online").toString());
                }
                return 0;
            } catch (NullPointerException e){
                SussiLogger.dangerMessage("Chyba při zjišťování online hráčů.");
                return 0;
            } catch (NumberFormatException en) {
                SussiLogger.dangerMessage("Nelze prevest online pocet hracu!");
                return 0;
            }
        }
        return 0;
    }
}
