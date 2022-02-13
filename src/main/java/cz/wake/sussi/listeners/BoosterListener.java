package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BoosterListener extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent e) {
        final Guild guild = Sussi.getJda().getGuildById(Sussi.getConfig().getCmGuildID());
        if (guild == null) return;
        Member member = e.getMember();

        System.out.println("[BOOST]: " + member.getNickname() + " - newTime(" + e.getNewTimeBoosted() + "), oldTime(" + e.getOldTimeBoosted() + ")");
        if(e.getNewTimeBoosted() != null && e.getOldTimeBoosted() == null) {
            /*
            member.getUser().openPrivateChannel().queue(msg -> msg.sendMessage(
                    MessageUtils.getEmbed(new Color(255, 80, 255))
                            .setTitle("Děkujeme za Boost CraftManie! " + EmoteList.ZABICKA_BOOSTER)
                            .setDescription("Ahoj, tímto bychom ti chtěli poděkovat za Boost, který jsi dal(a) našemu Discord serveru. Tímto se ti také odemkly výhody u nás na Discordu a brzo také i na Minecraft serveru.")
                            .addField("Seznam výhod", "- Booster role + růžový prefix\n- Změna barvy nicku pomocí `,booster color`", false)
                            .setFooter("Kdo nebůstí s námi, bůstí proti nám!")
                            .setThumbnail("https://discordapp.com/assets/b941bc1dfe379db6cc1f2acc5a612f41.png").build()).queue());
             */
            Sussi.getInstance().getSql().updateBooster(member.getId(), 1);
        }
        if (e.getNewTimeBoosted() == null && e.getOldTimeBoosted() != null) {
            Sussi.getInstance().getSql().updateBooster(member.getId(), 0);
            guild.getRoles().stream().filter(role -> role.getName().startsWith("#")).forEach(role -> {
                guild.removeRoleFromMember(member, role).queue(success -> {
                    if (guild.getMembersWithRoles(role).isEmpty()) {
                        role.delete().queue();
                    }
                });
                member.getUser().openPrivateChannel().queue(channel -> {
                    channel.sendMessage(MessageUtils.getEmbedError().setTitle("Přestal jsi boostovat CraftMania server").setDescription("Z tohoto důvodu ti byla odebrána role `" + role.getName() + "` tvé barvy.").build()).queue();
                });
            });
        }
    }
}
