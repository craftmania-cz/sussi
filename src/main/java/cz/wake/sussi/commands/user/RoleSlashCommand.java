package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class RoleSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        String optionId = event.getOption("name").getAsString();

        switch (optionId) {
            case "news":
                this.addOrRemoveRole(member, 847281784403001375L, hook);
                break;
            case "events":
                this.addOrRemoveRole(member, 530749538823176193L, hook);
                break;
            case "apple":
                this.addOrRemoveRole(member, 700009520935731361L, hook);
                break;
            case "android":
                this.addOrRemoveRole(member, 745260424949399563L, hook);
                break;
            case "crypto":
                this.addOrRemoveRole(member, 745260488224669817L, hook);
                break;
            case "fortnite":
                this.addOrRemoveRole(member, 430730941728817154L, hook);
                break;
            case "genshin":
                this.addOrRemoveRole(member, 785533342693982258L, hook);
                break;
            case "gta":
                this.addOrRemoveRole(member, 432194920187559946L, hook);
                break;
            case "hytale":
                this.addOrRemoveRole(member, 523086828765446154L, hook);
                break;
            case "osu":
                this.addOrRemoveRole(member, 766608357083316235L, hook);
                break;
            case "korean":
                this.addOrRemoveRole(member, 618740692553826304L, hook);
                break;
        }

    }

    @Override
    public String getName() {
        return "role";
    }

    @Override
    public String getDescription() {
        return "KEK";
    }

    @Override
    public String getHelp() {
        return "/role [nazev]";
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
        return true;
    }

    private void addOrRemoveRole(Member member, long roleId, InteractionHook hook) {
        if (!member.getRoles().contains(member.getGuild().getRoleById(roleId))) {
            member.getGuild().addRoleToMember(member, member.getGuild().getRoleById(roleId)).queue();
            hook.sendMessage(member.getAsMention() + " nastavil/a jsi si roli `" + member.getGuild().getRoleById(roleId).getName() + "`!").queue();
        } else {
            member.getGuild().removeRoleFromMember(member, member.getGuild().getRoleById(roleId)).queue();
            hook.sendMessage(member.getAsMention() + " odebral/a jsi si roli `" + member.getGuild().getRoleById(roleId).getName() + "`!").queue();
        }
    }
}
