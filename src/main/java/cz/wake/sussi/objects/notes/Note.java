package cz.wake.sussi.objects.notes;

import cz.wake.sussi.Sussi;

import java.sql.*;
import java.text.SimpleDateFormat;

public class Note {

    private String player;
    private String admin;
    private String note;
    private int id;
    private Timestamp datetime;

    public Note() {}

    public Note(String player, String admin, String note, int id, Timestamp datetime) {
        this.player = player;
        this.admin = admin;
        this.note = note;
        this.id = id;
        this.datetime = datetime;
    }

    public Note(String player, String admin, String note) {
        this.player = player;
        this.admin = admin;
        this.note = note;
        this.datetime = new Timestamp(System.currentTimeMillis());
    }

    public void addToCache() {
        Sussi.getNoteManager().getNotes().add(this);
    }

    public void push() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("INSERT INTO bungeecord.notes_data (player, note, admin) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, this.player);
            ps.setString(2, this.note);
            ps.setString(3, this.admin);
            ps.executeUpdate();
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                this.id = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    public void remove() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("DELETE FROM bungeecord.notes_data WHERE id = ?;");
            ps.setInt(1, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    public String getPlayer() {
        return player;
    }

    public String getAdmin() {
        if (admin == null) return "N/A";
        return admin;
    }

    public String getNote() {
        return note;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public String getFormattedDatetime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return simpleDateFormat.format(getDatetime());
    }
}
