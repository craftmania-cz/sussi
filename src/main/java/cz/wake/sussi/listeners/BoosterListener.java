package cz.wake.sussi.listeners;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.utils.EmoteList;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class BoosterListener extends ListenerAdapter {

    private EventWaiter w;

    public BoosterListener(EventWaiter w) {
        this.w = w;
    }

    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent e) {
        if(e.getNewTimeBoosted() != null) {
            Member member = e.getMember();
            member.getUser().openPrivateChannel().queue(msg -> msg.sendMessage(
                    MessageUtils.getEmbed(new Color(255, 80, 255))
                            .setTitle("Děkujeme za Boost CraftManie! " + EmoteList.ZABICKA_BOOSTER)
                            .setDescription("Ahoj, tímto bychom ti chtěli poděkovat za Boost, který jsi dal(a) našemu Discord serveru. Tímto se ti také odemkly výhody u nás na Discordu a brzo také i na Minecraft serveru.")
                            .addField("Seznam výhod", "- Booster role + růžový prefix\n- Změna barvy nicku pomocí `,booster color`", false)
                            .setFooter("Kdo nebůstí s námi, bůstí proti nám!")
                            .setThumbnail("https://discordapp.com/assets/b941bc1dfe379db6cc1f2acc5a612f41.png").build()).queue());
        }
    }
}
