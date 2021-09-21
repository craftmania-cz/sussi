package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.EmoteList;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.util.List;

public class BoosterSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {
        OptionMapping optionColor = event.getOption("hex_color");
        String colorString = optionColor.getAsString();

        if (colorString == null) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Špatně zadaný příkaz! Př. `/booster #8f9eb5`.\nPodporujeme jenom hex formát.").build()).queue();
            return;
        }

        if (!member.getRoles().contains(member.getGuild().getRoleById(Constants.BOOSTER_ROLE))) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Na toto nemáš práva, musíš boostit náš server! " + EmoteList.ZABICKA_BOOSTER).build()).queue();
            return;
        }

        try {
            final Color color = Color.decode(colorString);
            final String colorHexString = "#"+Integer.toHexString(color.getRGB()).substring(2);
            final Guild guild = member.getGuild();
            final int retiredPosition = guild.getRolesByName("Retired", true).stream().findFirst().get().getPosition(); // Under retired

            member.getRoles().stream().filter(role -> role.getName().startsWith("#")).forEach(role -> {
                // Remove all other roles
                guild.removeRoleFromMember(member, role).queue(success -> {
                    if (guild.getMembersWithRoles(role).isEmpty()) {
                        // Nobody else has this role, delete
                        role.delete().queue();
                    }
                });
            });

            final List<Role> rolesByName = guild.getRolesByName(colorString, true);
            if (rolesByName.isEmpty()) {
                // Create new
                final Role newRole = guild.createRole()
                        .setName(colorHexString)
                        .setColor(color)
                        .setMentionable(false)
                        .complete();

                guild.modifyRolePositions().selectPosition(newRole).moveTo(retiredPosition - 1).queue();

                guild.addRoleToMember(member, newRole).queue();

            } else {
                // Get and assign
                final Role role = rolesByName.stream().findFirst().get();

                guild.addRoleToMember(member, role).queue();
            }

            hook.sendMessageEmbeds(MessageUtils.getEmbed(color).setTitle("Barva úspěšně nastavena").setDescription("Nastavil jsi si barvu `" + colorHexString + "`").build()).queue();
            return;
        } catch (NumberFormatException e) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Nesprávný formát barvy!").build()).queue();
            return;
        }
    }

    @Override
    public String getName() {
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
    public boolean isEphemeral() {
        return false;
    }
}
