package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;

public class StaffList implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        MessageEmbed embed = MessageUtils.getEmbed(Constants.BLUE).setTitle("Seznam všech členů AT + jejich ID")
                .addField("Owner", generateStaffList(member, "207423116861767681"), false)
                .addField("Admins", generateStaffList(member, "272454769975754753"), false)
                .addField("Helpers", generateStaffList(member, "207638528757202945"), false)
                .addField("Developers", generateStaffList(member, "364085114172604426"), false)
                .addField("Eventer", generateStaffList(member, "272457315767287808"), false)
                .addField("Builder", generateStaffList(member, "294897053207756800"), false)
                .addField("Moderator", generateStaffList(member, "649927113964650496"), false)
                .build();
        channel.sendMessage(embed).queue();
    }

    @Override
    public String getCommand() {
        return "stafflist";
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
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"staffid", "sid"};
    }

    @Override
    public boolean deleteMessage() {
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
