package cz.wake.sussi.objects.notes;

import cz.wake.sussi.Sussi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashSet;

public class NotePlayer {

    private String player;
    private HashSet<Note> notes;

    public NotePlayer(String player) {
        this.player = player;
        this.notes = new HashSet<>();
    }

    public void fetch() {
        for (Note note : Sussi.getNoteManager().getNotes()) {
            if (note.getPlayer().equalsIgnoreCase(player)) {
                this.notes.add(note);
            }
        }
    }

    public void addNote(Note note) {
        this.notes.add(note);
        note.push();
    }

    public void removeNote(Note note) {
        this.notes.remove(note);
    }

    public String getPlayer() {
        return player;
    }

    public HashSet<Note> getNotes() {
        return notes;
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }

    public boolean hasNoteWithID(int ID) {
        return this.notes.stream().anyMatch(note -> note.getId() == ID);
    }

    public Note getNoteByID(int ID) {
        if (hasNoteWithID(ID)) {
            return this.notes.stream().filter(note -> note.getId() == ID).findFirst().get();
        } else return null;
    }

    public void removeNote(int ID) {
        Note note = new Note();
        if (hasNoteWithID(ID)) {
            note = getNoteByID(ID);
            this.notes.remove(note);
        }
        Sussi.getNoteManager().notes.remove(note);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("DELETE FROM bungeecord.notes_data WHERE player = ? AND note = ? AND id = ?;");
            ps.setString(1, this.player);
            ps.setString(2, note.getNote());
            ps.setInt(3, note.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    public void clearNotes() {
        this.notes.clear();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("DELETE FROM bungeecord.notes_data WHERE player = ?;");
            ps.setString(1, this.player);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    public void updateDatabase() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("DELETE FROM bungeecord.notes_data WHERE player = ?;");
            ps.setString(1, this.player);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
        if (!this.hasNotes()) return;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("INSERT INTO bungeecord.notes_data (id, player, note, admin, date) VALUES (?, ?, ?, ?, ?);");
            for (Note note : getNotes()) {
                ps.setInt(1, note.getId());
                ps.setString(2, note.getPlayer());
                ps.setString(3, note.getNote());
                ps.setString(4, note.getAdmin());
                ps.setTimestamp(5, note.getDatetime());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            if (e instanceof SQLIntegrityConstraintViolationException) { }
            else e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
        //todo
    }
}

