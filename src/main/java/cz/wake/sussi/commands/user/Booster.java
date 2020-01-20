package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.EmoteList;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Booster implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {

        if (!member.getRoles().contains(member.getGuild().getRoleById(Constants.BOOSTER_ROLE))) {
            MessageUtils.sendAutoDeletedMessage("Na toto nemáš práva, musíš boostit náš server! " + EmoteList.ZABICKA_BOOSTER, 15000, channel);
            return;
        }

        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(new Color(255, 80, 255)).setTitle("Booster").setDescription("Díky boostování našeho serveru, si můžeš vybrat pár odměn! " + EmoteList.ZABICKA_BOOSTER)
                .addField("Výhody a příkazy", "- Růžová role\n- Volba barvy nicku `,booster color`", false).build()).queue();
            return;
        }

        if (args[0].equalsIgnoreCase("color")) {
            MessageEmbed embed = MessageUtils.getEmbed(new Color(255, 80, 255)).setTitle("Booster - Volba barvy").setDescription("Zvol si jednu z vybraných barev pomocí reakce.")
                    .addField(
                            "Barvy",
                            "- " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_1).getAsMention() + ": " + Constants.ONE + "\n" +
                            "- " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_2).getAsMention() + ": " + Constants.TWO + "\n" +
                            "- " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_3).getAsMention() + ": " + Constants.THREE + "\n" +
                            "- " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_4).getAsMention() + ": " + Constants.FOUR + "\n" +
                            "- " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_5).getAsMention() + ": " + Constants.FIVE + "\n", false)
                            .setFooter("Pro smazání barevné role, klikni na křížek!").build();
            channel.sendMessage(embed).queue((Message m) -> {
                m.addReaction(Constants.ONE).queue();
                m.addReaction(Constants.TWO).queue();
                m.addReaction(Constants.THREE).queue();
                m.addReaction(Constants.FOUR).queue();
                m.addReaction(Constants.FIVE).queue();
                m.addReaction(Constants.DELETE).queue();

                // Delete
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.DELETE));
                }, (MessageReactionAddEvent ev) -> {
                    removeAllRoles(member);
                    channel.sendMessage(sender.getAsMention() + " všechny barevné role, které jsi měl(a) byly odebrány!").queue();
                    m.delete().queue();
                }, 60, TimeUnit.SECONDS, null);


                // Color 1
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.ONE));
                }, (MessageReactionAddEvent ev) -> {
                    removeAllRoles(member);
                    m.getGuild().addRoleToMember(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_1)).queue();
                    channel.sendMessage(sender.getAsMention() + " barevná role " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_1).getAsMention() + " ti byla nastavena!").queue();
                    m.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                // Color 2
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.TWO));
                }, (MessageReactionAddEvent ev) -> {
                    removeAllRoles(member);
                    m.getGuild().addRoleToMember(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_2)).queue();
                    channel.sendMessage(sender.getAsMention() + " barevná role " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_2).getAsMention() + " ti byla nastavena!").queue();
                    m.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                // Color 3
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.THREE));
                }, (MessageReactionAddEvent ev) -> {
                    removeAllRoles(member);
                    m.getGuild().addRoleToMember(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_3)).queue();
                    channel.sendMessage(sender.getAsMention() + " barevná role " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_3).getAsMention() + " ti byla nastavena!").queue();
                    m.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                // Color 4
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.FOUR));
                }, (MessageReactionAddEvent ev) -> {
                    removeAllRoles(member);
                    m.getGuild().addRoleToMember(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_4)).queue();
                    channel.sendMessage(sender.getAsMention() + " barevná role " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_4).getAsMention() + " ti byla nastavena!").queue();
                    m.delete().queue();
                }, 60, TimeUnit.SECONDS, null);

                // Color 5
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.FIVE));
                }, (MessageReactionAddEvent ev) -> {
                    removeAllRoles(member);
                    m.getGuild().addRoleToMember(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_5)).queue();
                    channel.sendMessage(sender.getAsMention() + " barevná role " + member.getGuild().getRoleById(Constants.BOOSTER_COLOR_5).getAsMention() + " ti byla nastavena!").queue();
                    m.delete().queue();
                }, 60, TimeUnit.SECONDS, null);
            });
        }

    }

    @Override
    public String getCommand() {
        return "booster";
    }

    @Override
    public String getDescription() {
        return ".";
    }

    @Override
    public String getHelp() {
        return ".";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean deleteMessage() {
        return false;
    }

    private void removeAllRoles(Member member) {
        checkAndRemove(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_1));
        checkAndRemove(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_2));
        checkAndRemove(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_3));
        checkAndRemove(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_4));
        checkAndRemove(member, member.getGuild().getRoleById(Constants.BOOSTER_COLOR_5));
    }

    private void checkAndRemove(Member member, Role role) {
        if (member.getRoles().contains(role)) {
            member.getGuild().removeRoleFromMember(member, role).queue();
        }
    }
}
