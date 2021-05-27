package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.ArrayList;
import java.util.List;

public class ChooseSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {

        String option1 = event.getOption("opt1").getAsString();
        String option2 = event.getOption("opt2").getAsString();

        List<String> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);

        if (event.getOption("opt3") != null) {
            options.add(event.getOption("opt3").getAsString());
        }

        if (event.getOption("opt4") != null) {
            options.add(event.getOption("opt4").getAsString());
        }

        if (event.getOption("opt5") != null) {
            options.add(event.getOption("opt5").getAsString());
        }

        if (event.getOption("opt6") != null) {
            options.add(event.getOption("opt6").getAsString());
        }

        hook.sendMessage("<:FeelsSmileMan:566074595568779284> **" + sender.getName() + "**, zvolila jsem **" + options.get((int) (Math.random() * options.size())) + "**").queue();
    }

    @Override
    public String getName() {
        return "choose";
    }

    @Override
    public String getDescription() {
        return "Nech Sussi vybrat správnou odpoveď.";
    }

    @Override
    public String getHelp() {
        return "/choose [něco] [něco2] [něco3] [něco4] [něco5]";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
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
