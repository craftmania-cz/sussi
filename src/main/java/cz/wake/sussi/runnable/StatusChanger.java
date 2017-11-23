package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.WebSocketCode;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.TimerTask;

public class StatusChanger extends TimerTask {

    @Override
    public void run() {
        setStatus();
    }

    private int getOnlinePlayers(){
        int online;
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://mcapi.de/api/server/mc.craftmania.cz").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONObject jsonArray = json.getJSONObject("players");
            online = jsonArray.getInt("online");
        } catch (Exception e){
            e.printStackTrace();
            online = 0;
        }
        return online;
    }

    private void setStatus(){
        JSONObject obj = new JSONObject();
        JSONObject game = new JSONObject();
        game.put("url", "https://www.twitch.tv/sussi");
        game.put("type", 3);
        obj.put("afk", false);
        obj.put("status", OnlineStatus.ONLINE.getKey());
        obj.put("since", System.currentTimeMillis());
        game.put("name", String.valueOf(getOnlinePlayers()) + " hráčů");
        obj.put("game", game);
        ((JDAImpl) Sussi.getJda()).getClient().send(new JSONObject().put("d", obj).put("op", WebSocketCode.PRESENCE).toString());
    }
}
