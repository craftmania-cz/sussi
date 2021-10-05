package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BoosterListener extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent e) {
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
        }
    }
}
