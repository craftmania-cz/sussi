package cz.wake.sussi.commands.mod;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class StaffListSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {
        MessageEmbed embed = MessageUtils.getEmbed(Constants.BLUE).setTitle("Seznam všech členů AT + jejich ID")
                .addField("Owner", generateStaffList(member, "207423116861767681"), false)
                .addField("Staff", generateStaffList(member, "208227643714306050"), false)
                .addField("Admins", generateStaffList(member, "272454769975754753"), false)
                .addField("Helpers", generateStaffList(member, "207638528757202945"), false)
                .addField("Developers", generateStaffList(member, "364085114172604426"), false)
                .addField("Eventer", generateStaffList(member, "272457315767287808"), false)
                .addField("Builder", generateStaffList(member, "294897053207756800"), false)
                .addField("Moderator", generateStaffList(member, "649927113964650496"), false)
                .addField("Artist", generateStaffList(member, "796382683734605836"), false)
                .build();
        hook.sendMessageEmbeds(embed).queue();
    }

    @Override
    public String getName() {
        return "stafflist";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getHelp() {
        return "/stafflist";
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

    private String generateStaffList(Member member, String roleId) {
        StringBuilder builder = new StringBuilder();
        Role role = member.getGuild().getRoleById(roleId);
        member.getGuild().getMembersWithRoles(role).forEach(roleMember -> {
            builder.append(roleMember.getAsMention() + " - `" + roleMember.getId() + "`\n");
        });
        return builder.toString();
    }
}
