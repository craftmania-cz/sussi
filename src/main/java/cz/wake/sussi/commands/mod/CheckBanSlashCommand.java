package cz.wake.sussi.commands.mod;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.LBan;
import cz.wake.sussi.objects.LPlayer;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckBanSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {

        LPlayer player = null;

        if (event.getOption("name") != null) {
            String requestedName = event.getOption("name").getAsString();
            player = Sussi.getInstance().getSql().getPlayerBanlistObject(requestedName);
            if (player == null) {
                hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Požadovaný hráč neexistuje!").build()).queue();
                return;
            }
            LBan ban = Sussi.getInstance().getSql().getActiveBanObject(player.getUuid());
            if (ban == null){
                hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Hráč nemá aktivní žádný ban!").build()).queue();
                return;
            }
            hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.ORANGE).setTitle("Kontrola banu")
                    .setDescription("**Nick**: " + player.getName() + "\n" +
                            "**IP**: " + controlIP(player.getIP(), channel) + "\n" +
                            "**Důvod**: " + ban.getReason() + "\n" +
                            "**Zabanoval**: " + ban.getBannedBy() + "\n" +
                            "**Datum**: " + getDate(ban.getTime()) + "\n" +
                            "**Konec**: " + getDate(ban.getUntil()) + "\n" +
                            "**IP ban**: "+ resolveBan(ban.isIpban())).setFooter("(1) Kontrola IP", null)
                    .setThumbnail("https://visage.surgeplay.com/head/256/" + player.getUuid()).build())
                    .addActionRow(Button.primary("checkIp:" + member.getUser().getId() + ":" + player.getIP(), "Kontrola IP"), Button.danger("delete:" + member.getUser().getId() + ":" + hook.getInteraction().getId(), "Smazat")).queue();
            return;

        }
        hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Špatně zadaný příkaz! Musíš takhle `/checkban [nick]`. Př. `/checkban MrWakeSK`").build()).queue();
    }

    @Override
    public String getName() {
        return "checkban";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return "/checkban [nick]";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    private String getDate(long time) {
        if(time == -1){
            return "Permanentní";
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
    }

    private String resolveBan(boolean ban){
        if(ban){
            return "Ano";
        }
        return "Ne";
    }

    private String controlIP(String ip, MessageChannel tc){
        if(tc.getId().equals("236749682229903360") || tc.getId().equals("402262554375880705") || tc.getId().equals("484807072060538881") || tc.getId().equals("451785371399749642")) {
            return ip;
        }
        return "Skrytá";
    }
}
