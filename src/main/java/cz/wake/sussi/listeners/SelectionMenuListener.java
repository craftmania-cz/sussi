package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SelectionMenuListener extends ListenerAdapter {

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        if (event.getMember() == null) return;
        switch (event.getComponentId()) {
            case "menu:role_theme" -> {
                switch (event.getValues().get(0)) {
                    case "apple" -> {
                        this.addOrRemoveRole(event.getMember(), "Apple", 700009520935731361L, event);
                    }
                    case "fortnite" -> {
                        this.addOrRemoveRole(event.getMember(), "Fornite", 430730941728817154L, event);
                    }
                    case "hytale" -> {
                        this.addOrRemoveRole(event.getMember(), "Hytale", 523086828765446154L, event);
                    }
                }
            }
            case "menu:role_announce" -> {
                switch (event.getValues().get(0)) {
                    case "news" -> {
                        this.addOrRemoveRole(event.getMember(), "News", 847281784403001375L, event);
                    }
                    case "events" -> {
                        this.addOrRemoveRole(event.getMember(), "Events", 530749538823176193L, event);
                    }
                    case "udrzba" -> {
                        this.addOrRemoveRole(event.getMember(), "Údržba", 995476035208486912L, event);
                    }
                }
            }
            case "menu:role_server" -> {
                switch (event.getValues().get(0)) {
                    case "survival-118" -> {
                        if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(event.getMember().getId())) {
                            event.getInteraction().getHook().sendMessage("Nemáš propojený profil, propoj si ho a pak to zkus znova. Více info v #propojeni_mc_profilu").setEphemeral(true).queue();
                            return;
                        }
                        int level = Sussi.getInstance().getSql().getServerLevel(event.getMember().getIdLong(), "survival_118_level");
                        System.out.println(level);
                        if (level < 5) {
                            event.getInteraction().getHook().sendMessage("Nemáš dostatečný level 5 k odemknutí kanálu. Hraj na Survivalu a až dosáhneš levelu 5, zkus to znova.").setEphemeral(true).queue();
                            return;
                        }
                        this.addOrRemoveRole(event.getMember(), "Survival [1.18]", 995478852912558110L, event);
                    }
                }
            }
        }
    }

    private void addOrRemoveRole(Member member, String name, long roleId, SelectMenuInteractionEvent event) {
        if (!member.getRoles().contains(member.getGuild().getRoleById(roleId))) {
            member.getGuild().addRoleToMember(member, member.getGuild().getRoleById(roleId)).queue();
            event.getInteraction().getHook().sendMessage("Nastavil(a) jsi si roli **" + name + "**.").setEphemeral(true).queue();
            return;
        }
        member.getGuild().removeRoleFromMember(member, member.getGuild().getRoleById(roleId)).queue();
        event.getInteraction().getHook().sendMessage("Role **" + name + "** ti byla odebrána.").setEphemeral(true).queue();
    }
}
