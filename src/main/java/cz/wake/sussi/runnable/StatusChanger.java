package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.ServerInfo;
import net.dv8tion.jda.api.entities.Activity;

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
        Sussi.getJda().getPresence().setActivity(Activity.listening(ServerInfo.getOnlinePlayers() + " hráčů"));
    }
}
