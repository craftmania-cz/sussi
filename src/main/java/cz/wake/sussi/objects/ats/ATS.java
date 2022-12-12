package cz.wake.sussi.objects.ats;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ATS {

    @NotNull
    private String name;

    private int rank;
    @Nullable
    private String discordID;

    private int min_hours, pristup_build;

    @NotNull
    private HashMap<Server, ServerATS> serverATS = new HashMap<>();

    public ATS(@NotNull String name) {
        this.name = name;

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("SELECT * FROM at_table WHERE nick = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.rank = rs.getInt("rank");
                this.pristup_build = rs.getInt("pristup_build");
                this.min_hours = rs.getInt("min_hours");

                this.serverATS.put(Server.SURVIVAL, new ServerATS(Server.SURVIVAL, rs));
                this.serverATS.put(Server.SKYBLOCK, new ServerATS(Server.SKYBLOCK, rs));
                this.serverATS.put(Server.CREATIVE, new ServerATS(Server.CREATIVE, rs));
                this.serverATS.put(Server.PRISON, new ServerATS(Server.PRISON, rs));
                this.serverATS.put(Server.VANILLA, new ServerATS(Server.VANILLA, rs));
                this.serverATS.put(Server.MINIGAMES, new ServerATS(Server.MINIGAMES, rs));
                this.serverATS.put(Server.VANILLA_ANARCHY, new ServerATS(Server.VANILLA_ANARCHY, rs));
                this.serverATS.put(Server.BUILD, new ServerATS(Server.BUILD, rs));
                this.serverATS.put(Server.EVENTS, new ServerATS(Server.EVENTS, rs));
            }
            this.discordID = Sussi.getInstance().getSql().getLinkedDiscordID(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    public ATS(@NotNull String name, @NotNull String date) {
        this.name = name;

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("SELECT * FROM ? WHERE nick = ?;");
            ps.setString(1, "ats_archive_" + date);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                this.rank = rs.getInt("rank");
                this.pristup_build = rs.getInt("pristup_build");
                this.min_hours = rs.getInt("min_hours");

                this.serverATS.put(Server.SURVIVAL, new ServerATS(Server.SURVIVAL, rs));
                this.serverATS.put(Server.SKYBLOCK, new ServerATS(Server.SKYBLOCK, rs));
                this.serverATS.put(Server.CREATIVE, new ServerATS(Server.CREATIVE, rs));
                this.serverATS.put(Server.PRISON, new ServerATS(Server.PRISON, rs));
                this.serverATS.put(Server.VANILLA, new ServerATS(Server.VANILLA, rs));
                this.serverATS.put(Server.MINIGAMES, new ServerATS(Server.MINIGAMES, rs));
                this.serverATS.put(Server.VANILLA_ANARCHY, new ServerATS(Server.VANILLA_ANARCHY, rs));
                this.serverATS.put(Server.BUILD, new ServerATS(Server.BUILD, rs));
                this.serverATS.put(Server.EVENTS, new ServerATS(Server.EVENTS, rs));
            }

            this.discordID = Sussi.getInstance().getSql().getLinkedDiscordID(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    /**
     * @return Returns true if could send evaluation in DMs, returns false if could not
     */
    public boolean evaluate() {
        if (getDiscordID() == null) return false;
        try {
            User user = Sussi.getJda().retrieveUserById(getDiscordID()).complete();
            if (user == null) return false;
            user.openPrivateChannel().submit().thenAccept(msg -> {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);
                String date = new SimpleDateFormat("MM/yyyy").format(calendar.getTime());
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setAuthor("Vyhodnocení ATS - " + date)
                        .setThumbnail("https://mc-heads.net/head/" + getName() + "/128.png")
                        .setColor((isComplete() ? Color.decode("#38b559") : Color.RED))
                        .setDescription("**Odehraný čas:** " + TimeUtils.formatTime("%d dni, %hh %mm", getTotalTime(), false) + " (min: " + getMin_hours() + "h)" + "\n" +
                                "**Aktivita:** " + String.valueOf(getTotalActivity() + " bodů") + "\n\n" +
                                (isComplete() ? "Vypadá to, že jsi odehral minimální počet hodin na serveru. Skvělá práce." :
                                        "Vypadá to, že jsi neodehral minimální počet hodin na serveru. Kontaktuj Waka když si tak ještě neudělal a vysvětluj proč."));
                Message message = msg.sendMessageEmbeds(embedBuilder.build()).complete();
            });
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isComplete() {
        return getTotalHours() >= getMin_hours();
    }

    @Nullable
    public String getDiscordID() {
        return discordID;
    }

    @Nullable
    public ServerATS getServerATS(Server s) {
        return this.serverATS.getOrDefault(s, null);
    }

    public int getTotalHours() {
        return (int) Math.floor(getTotalTime() / 60);
    }

    public int getTotalTime() {
        return this.serverATS.values().stream().collect(Collectors.summingInt(ServerATS::getPlayedTime));
    }

    public int getTotalActivity() {
        return this.serverATS.values().stream().collect(Collectors.summingInt(ServerATS::getChatBody));
    }

    public String getTotalActivityFormatted() {
        if (getTotalActivity() == 1) {
            return getTotalActivity() + " bod";
        } else if (getTotalActivity() > 1 && getTotalActivity() < 5) {
            return getTotalActivity() + " body";
        } else {
            return getTotalActivity() + " bodů";
        }
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public int getPristup_build() {
        return pristup_build;
    }

    public int getMin_hours() {
        return min_hours;
    }

    public String getMinHoursFormatted() {
        if (getMin_hours() == 1) {
            return getMin_hours() + " hodina";
        } else if (getMin_hours() < 1 && getMin_hours() > 5) {
            return getMin_hours() + " hodiny";
        } else {
            return getMin_hours() + " hodin";
        }
    }

    @Override
    public String toString() {
        return "ATS{" +
                "name='" + name + '\'' +
                ", rank=" + rank +
                ", discordID='" + discordID + '\'' +
                '}';
    }

    public static enum Server {
        SURVIVAL(true, "surv"),
        SKYBLOCK(true, "sky"),
        CREATIVE(true, "crea"),
        PRISON(true, "prison"),
        VANILLA(true, "vanilla"),
        //SKYCLOUD(true, "skycloud"),
        BUILD(false, "build"),
        EVENTS(false, "events"),
        //HARDCORE_VANILLA(true, "hardcore_vanilla"),
        VANILLA_ANARCHY(true, "anarchy"),
        MINIGAMES(true, "minigames");

        private boolean hasChatPoints;
        private String dbPrefix;

        Server(boolean hasChatPoints, String dbPrefix) {
            this.hasChatPoints = hasChatPoints;
            this.dbPrefix = dbPrefix;
        }
    }

    public static class ServerATS {

        private Server server;

        @NotNull
        private Long lastActivity;

        @NotNull
        private int playedTime;

        @Nullable
        private int chatBody;

        public ServerATS(Server server, ResultSet resultSet) {
            this.server = server;

            try {
                this.playedTime = resultSet.getInt(server.dbPrefix + "_played_time");
                this.lastActivity = resultSet.getLong(server.dbPrefix + "_pos_aktivita");
                if (server.hasChatPoints) this.chatBody = resultSet.getInt(server.dbPrefix + "_chat_body");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public Server getServer() {
            return server;
        }

        public Long getLastActivity() {
            return lastActivity;
        }

        public int getPlayedTime() {
            return playedTime;
        }

        public int getChatBody() {
            return chatBody;
        }
    }
}
