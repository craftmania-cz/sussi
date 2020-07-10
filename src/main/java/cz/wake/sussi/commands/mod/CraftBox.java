package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class CraftBox implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {
            channel.sendMessage(
                    MessageUtils.getEmbed(Constants.GRAY)
                            .setTitle("Odkaz na CraftBox")
                            .setDescription("Kliknutím [zde](https://craftbox.craftmania.cz/) se přesměruješ na CraftBox.")
                            .build()
            ).queue();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "register": {
                if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
                    MessageUtils.sendErrorMessage("Integrace s CraftBox nefunguje bez přepojení s MC účtem.", channel);
                    break;
                }
                String nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());

                if (!Sussi.getATSManager().isInATS(nick)) {
                    MessageUtils.sendErrorMessage("Nelze použít ,craftbox register pokud nejsi člen AT!", channel);
                    break;
                }

                channel.sendMessage(
                        MessageUtils.getEmbed(Constants.RED)
                                .setTitle("Registrace do CraftBoxu")
                                .setDescription(":closed_lock_with_key: | Vypadá to, že se chceš registrovat do CraftBoxu. Poslala jsem ti DM.")
                                .build()
                ).queue(beginningMessage -> {
                    sender.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage(
                                MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                                        .setTitle("Registrace do CraftBoxu 0/2")
                                        .setDescription(":closed_lock_with_key: | Registrace do CraftBoxu je jednoduchá. Tvůj nick byl automaticky vybrán na základě tvého nicku na " +
                                                "Minecraftu - `" + nick + "`.\n" +
                                                "Teď od tebe budu chtít aby sis vybral své heslo. Mělo by být minimálně **6 " +
                                                "znaků dlouhé**, obsahovat **aspoň jedno číslo** a **jedno velké písmeno**. Pro větší zabezpeční můžeš použít i **znaky**.")
                                        .build()
                        ).queue();

                        w.waitForEvent(PrivateMessageReceivedEvent.class, privateMessageReceivedEvent -> privateMessageReceivedEvent.getAuthor().equals(sender), privateMessageReceivedEvent -> {
                            String password = privateMessageReceivedEvent.getMessage().getContentRaw();

                            if (password.contains(" ") || password.length() < 6 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
                                MessageUtils.sendErrorMessage("Tvé heslo nesplňuje minimální požadavky, registrace ukončena.", privateChannel);
                                return;
                            }

                            privateChannel.sendMessage(MessageUtils.getEmbed(Constants.BLUE)
                                    .setTitle("Registrace do CraftBoxu 1/2")
                                    .setDescription(":closed_lock_with_key: | Výborně, tvé heslo splňuje minimální požadavky. Teď ho prosím napiš znovu pro potvrzení.")
                                    .build()).queue();

                            w.waitForEvent(PrivateMessageReceivedEvent.class, privateMessageReceivedEvent1 -> privateMessageReceivedEvent1.getAuthor().equals(sender), privateMessageReceivedEvent1 -> {
                                String confirmedPassword = privateMessageReceivedEvent1.getMessage().getContentRaw();

                                if (!confirmedPassword.equals(password)) {
                                    MessageUtils.sendErrorMessage("Hesla se nezhodují, registrace ukončena.", privateChannel);
                                    return;
                                }

                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", nick)
                                        .add("password", password)
                                        .add("adminKey", Sussi.getConfig().getCraftBoxAdminKey())
                                        .build();
                                Request request = new Request.Builder()
                                        .url("https://api.craftmania.cz/account/register")
                                        .post(requestBody)
                                        .build();

                                try {
                                    Response response = client.newCall(request).execute();
                                    JSONObject jsonObject = new JSONObject(response.body().string());

                                    if (response.code() != 200) {
                                        MessageUtils.sendErrorMessage("Chyba `" + response.code() + "` - " + jsonObject.getString("message"), privateChannel);
                                        return;
                                    }

                                    beginningMessage.editMessage(
                                            MessageUtils.getEmbed(Constants.GREEN)
                                                    .setTitle("Registrace do CraftBoxu")
                                                    .setDescription(":closed_lock_with_key: | Uživatel " + sender.getAsMention() + " se registroval do CraftBoxu jako `" + nick + "`.")
                                                    .build()
                                    ).queue();
                                    message.delete().queue();

                                    privateChannel.sendMessage(
                                            MessageUtils.getEmbed(Constants.GREEN)
                                                    .setTitle("Registrace do CraftBoxu 2/2")
                                                    .setDescription("Registrace proběhla úspěšně. Do CraftBoxu se přihlásíš [zde](https://craftbox.craftmania.cz).\n" +
                                                            "Tvůj nick: `" + nick + "`\n" +
                                                            "Tvé heslo: `" + password + "` (heslo se zamaže za 30 vteřin)")
                                                    .build()
                                    ).queue(sentMessage -> sentMessage.editMessage(
                                            MessageUtils.getEmbed(Constants.GREEN)
                                                    .setTitle("Registrace do CraftBoxu 2/2")
                                                    .setDescription("Registrace proběhla úspěšně. Do CraftBoxu se přihlásíš [zde](https://craftbox.craftmania.cz).\n" +
                                                            "Tvůj nick: `" + nick + "`\n" +
                                                            "Tvé heslo: `" + StringUtils.repeat("*", password.length()) + "`")
                                                    .build()
                                    ).queueAfter(30, TimeUnit.SECONDS));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MessageUtils.sendErrorMessage("Nepodařilo se registrovat do CraftBoxu, kontaktuj developery.", privateChannel);
                                    return;
                                }
                            }, 60, TimeUnit.SECONDS, () -> {
                                MessageUtils.sendErrorMessage("Uběhlo 60 vtěřin bez aktivity, registrace ukončena", privateChannel);
                                message.delete().queue();
                                beginningMessage.delete().queue();
                            });
                        }, 60, TimeUnit.SECONDS, () -> {
                            MessageUtils.sendErrorMessage("Uběhlo 60 vtěřin bez aktivity, registrace ukončena", privateChannel);
                            message.delete().queue();
                            beginningMessage.delete().queue();
                        });
                    });
                });
                break;
            }
        }
    }

    @Override
    public String getCommand() {
        return "craftbox";
    }

    @Override
    public String getDescription() {
        return "Integrace s administračním panelem CraftBox pro členy AT.";
    }

    @Override
    public String getHelp() {
        return ",craftbox - Zobrazí odkaz na CraftBox\n" +
                ",craftbox register - Záhají registraci do CraftBox v DM";
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
        return new String[]{"cb"};
    }
}
