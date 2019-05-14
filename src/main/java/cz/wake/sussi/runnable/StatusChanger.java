package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.ServerInfo;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.WebSocketCode;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.managers.Presence;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.TimerTask;

public class StatusChanger extends TimerTask {

    @Override
    public void run() {
        try {
            setStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatus(){
        Sussi.getJda().getPresence().setGame(Game.of(Game.GameType.WATCHING, ServerInfo.getOnlinePlayers() + " hráčů"));
    }
}
