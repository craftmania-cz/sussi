package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.notes.Note;
import cz.wake.sussi.objects.notes.NotePlayer;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class NoteCommand implements ICommand {

    private Paginator.Builder pBuilder;

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length == 0) {
            sendHelp(channel);
            return;
        }

        if(!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
            String name = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
            if (!Sussi.getInstance().getSql().isAT(name)) {
                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Pro použití tohoto příkazu musíš mít propojený MC účet.").build()).queue();
                return;
            }
        }

        if (args.length >= 2) {
            String player = args[0];
            String operation = args[1];
            NotePlayer notePlayer = Sussi.getNoteManager().getNotePlayer(player);
            switch (operation.toLowerCase()) {
                case "add":
                    if (args.length >= 3) {
                        StringJoiner noteBuilder = new StringJoiner(" ");
                        for (int i = 2; i < args.length; i++) {
                            noteBuilder.add(args[i]);
                        }
                        String stringNote = noteBuilder.toString();

                        Note note = new Note(player, sender.getAsTag(), stringNote);
                        notePlayer.addNote(note);
                        note.addToCache();

                        channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("Poznámka pro hráče '" + player + "' byla přidána (ID: " + note.getId() + ").").build()).queue();
                        break;
                    } else {
                        sendHelp(channel);
                        break;
                    }
                case "remove":
                    if (args.length >= 3) {
                        if (!notePlayer.hasNotes()) {
                            channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Tento hráč nemá žádne poznámky.").build()).queue();
                            return;
                        }
                        int id;
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sendHelp(channel);
                            break;
                        }
                        try {
                            if (!notePlayer.hasNoteWithID(id)) {
                                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Hráč '" + player + "' nemá poznámku s ID " + id + ".").build()).queue();
                                break;
                            }
                            notePlayer.removeNote(id);
                            channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("Poznámka s ID " + id + " hráče '" + player + "' byla vymazána.").build()).queue();
                            break;
                        } catch (Exception e) {
                            channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Nastala chyba při vymazávaní poznámky s ID: " + id + ".").build()).queue();
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        sendHelp(channel);
                        break;
                    }
                case "clear":
                    if (!notePlayer.hasNotes()) {
                        channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Tento hráč nemá žádné poznámky.").build()).queue();
                        return;
                    }
                    notePlayer.clearNotes();
                    channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("Poznámky hráče '" + player + "' byly vymazány.").build()).queue();
                    break;
                case "list":
                    if (!notePlayer.hasNotes()) {
                        channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Tento hráč nemá žádné poznámky.").build()).queue();
                        return;
                    }

                    pBuilder = new Paginator.Builder()
                            .setColumns(1)
                            .setItemsPerPage(3)
                            .showPageNumbers(true)
                            .waitOnSinglePage(false)
                            .useNumberedItems(false)
                            .setFinalAction(m -> {
                                try {
                                    m.clearReactions().queue();
                                } catch (PermissionException e) {
                                    m.delete().queue();
                                }
                            })
                            .setEventWaiter(w)
                            .setTimeout(1, TimeUnit.MINUTES);

                    for (Note note : notePlayer.getNotes()) {
                        pBuilder.addItems("'" + note.getNote() + "' \n**ID:** " + note.getId() + " | **Admin:** " + note.getAdmin() + " | **Datum:** " + note.getFormattedDatetime() + "\n");
                    }

                    Paginator p = pBuilder.setColor(Color.YELLOW).setText("Poznámky hráče " + player + ":").build();
                    p.paginate(channel, 1);
                    break;
                default:
                    sendHelp(channel);
                    break;
            }
        } else {
            sendHelp(channel);
            return;
        }
    }

    @Override
    public String getCommand() {
        return "note";
    }

    @Override
    public String getDescription() {
        return "Správa poznámek hráčů.";
    }

    @Override
    public String getHelp() {
        return ",note <nick> list - zobrazí seznam poznámek hráče\n" +
                ",note <nick> add <note> - přidá novou poznámku pro hráče\n" +
                ",note <nick> remove <id> - vymaže hráčskou poznámku s určitým ID\n" +
                ",note <nick> clear - vymaže všechny poznámky hráče";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private void sendHelp(MessageChannel channel) {
        channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Nesprávně napsané argumenty! Syntax: ,note (nick) [add/remove/clear/list] [note]").build()).queue();
    }
}
