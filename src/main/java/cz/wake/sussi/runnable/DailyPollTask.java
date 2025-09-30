package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.TimeUnit;

public class DailyPollTask implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TextChannel channel = Sussi.getJda().getTextChannelById("1416850083026174043");
        assert channel != null;

        MessagePollBuilder pollBuilder = new MessagePollBuilder("Dnes na to???");
        pollBuilder.setDuration(12, TimeUnit.HOURS);
        pollBuilder.addAnswer("Mám!!!", Emoji.fromFormatted(getRandomGoodEmote()));
        pollBuilder.addAnswer("Nemám :(", Emoji.fromFormatted(getRandomBadEmote()));
        pollBuilder.setMultiAnswer(false);

        channel.sendMessagePoll(pollBuilder.build()).queue();
    }

    private String getRandomGoodEmote() {
        String[] emotes = new String[]{
                "<:catSmug:827027907263856670>",
                "<:FeelsCoffeeMan:581793112335319040>",
                "<a:licka:1058464908708167811>",
                "<a:peepoOH:638776117590753290>",
                "<:pogU:824587766817357854>"
        };
        int randomIndex = (int) (Math.random() * emotes.length);
        return emotes[randomIndex];
    }

    private String getRandomBadEmote() {
        String[] emotes = new String[]{
                "<:catStaree:944007120544731177>",
                "<:FeelsOhGod:287208617856466944>",
                "<:pepeMegaRage:884181125868711986>",
                "<:Clueless:931692136154431488>",
                "<:rage:877596305894015016>",
                "<a:peepoSadCry:755111364959404213>"
        };
        int randomIndex = (int) (Math.random() * emotes.length);
        return emotes[randomIndex];
    }
}
