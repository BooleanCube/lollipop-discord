package lollipop.commands;

import lollipop.CommandType;
import lollipop.Constant;
import lollipop.Command;
import lollipop.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class BotInfo implements Command {

    @Override
    public String[] getAliases() { return new String[] {"botinfo"}; }

    @Override
    public CommandType getCategory() {
        return CommandType.MISCELLANEOUS;
    }

    @Override
    public String getHelp() {
        return "Learn more about lollipop and the application's developers.\n" +
                "Visit [lollipop's website](" + Constant.WEBSITE + ") to get a better understanding of how the application works\n" +
                "Invite lollipop to your server and even view the open-source code for the application\n" +
                "Vote for lollipop on all of it's bot list profiles\n" +
                "Usage: `" + Constant.PREFIX + getAliases()[0] + "`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this);
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Bot Information")
                        .setDescription("""
                                Lollipop is an anime/manga discord bot which allows any user to search for an anime or a manga from the web and get the results on discord and has many features like fun roleplay commands, useful utility commands and other fun commands.
                                > [Bot Website](https://lollipop-bot.github.io/)
                                > [Bot Invite Link](https://discord.com/api/oauth2/authorize?client_id=919061572649910292&permissions=1515854359872&scope=bot%20applications.commands)
                                > [Github Repository](https://github.com/lollipop-bot)
                                > [Discord Bot List](https://discordbotlist.com/bots/lollipop-4786)
                                > [Top.gg](https://top.gg/bot/919061572649910292)
                                > [Infinity Bot List](https://infinitybots.gg/bots/919061572649910292)
                                """)
                        .addField("Developer", "**BooleanCube** (" + event.getJDA().getUserById(Constant.OWNER_ID).getAsTag() + ")\n[MyAnimeList](https://myanimelist.net/profile/BooleanCube) - [Playlist](https://open.spotify.com/playlist/4KnWT1hszQuBi4IaKdm8Pk?si=91e0fe7e73b54853) - [Portfolio](https://booleancube.github.io/) - [Github](https://github.com/BooleanCube) - [Youtube](https://www.youtube.com/channel/UCsivrachJyFVLi7V60lrd6g)", false)
                        .addField("Lollipop Version", Constant.VERSION, false)
                        .setFooter("konnichiwa, watashi wa lollipop desu")
                        .setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl() + "?size=512")
                        .build()
        ).queue();
    }

    @Override
    public int cooldownDuration() {
        return 1;
    }

}
