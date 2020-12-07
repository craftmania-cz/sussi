package cz.wake.sussi.objects.notes;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.SussiLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NoteManager {

    public List<Note> notes;
    public HashSet<NotePlayer> notePlayers;

    public NoteManager() {
        this.notes = new ArrayList<>();
        this.notePlayers = new HashSet<>();

        SussiLogger.infoMessage("Caching notes...");
        this.cacheNodes();
    }

    private void cacheNodes() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("SELECT * FROM bungeecord.notes_data;");
            ps.executeQuery();
            ResultSet resultSet = ps.getResultSet();
            while (resultSet.next()) {
                Note note = new Note(resultSet.getString("player"), resultSet.getString("admin"), resultSet.getString("note"), resultSet.getInt("id"), resultSet.getTimestamp("date"));
                this.notes.add(note);
            }
            SussiLogger.greatMessage("Cached " + this.notes.size() + " notes.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    public NotePlayer getNotePlayer(String player) {
        if (this.notePlayers.stream().filter(np -> np.getPlayer() == player).findFirst().isPresent()) {
            return this.notePlayers.stream().filter(np -> np.getPlayer() == player).findFirst().get();
        } else {
            return registerNotePlayer(player);
        }
    }

    public List<Note> getNotes() {
        return notes;
    }

    public HashSet<NotePlayer> getNotePlayers() {
        return notePlayers;
    }

    public NotePlayer registerNotePlayer(String player) {
        if (this.notePlayers.stream().anyMatch(notePlayer -> notePlayer.getPlayer().equalsIgnoreCase(player))) {
            return this.notePlayers.stream().filter(notePlayer -> notePlayer.getPlayer().equalsIgnoreCase(player)).findFirst().get();
        }
        NotePlayer notePlayer = new NotePlayer(player);
        notePlayer.fetch();
        this.notePlayers.add(notePlayer);
        return notePlayer;
    }

    public void unregisterNotePlayer(NotePlayer player) {
        this.notePlayers.remove(player);
    }
}
