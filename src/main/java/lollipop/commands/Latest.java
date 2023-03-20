package lollipop.commands;

import lollipop.*;
import lollipop.pages.AnimePage;
import lollipop.pages.MangaPage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Latest implements Command {

    public static HashMap<Long, AnimePage> messageToAnimePage = new HashMap<>();
    public static HashMap<Long, MangaPage> messageToMangaPage = new HashMap<>();

    @Override
    public String[] getAliases() {
        return new String[]{"latest"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.ANIME;
    }

    @Override
    public String getHelp() {
        return "Explore the top twenty latest anime and manga series being released of the season.\n" +
                "The latest anime shows come with a trailer component to watch and the latest mangas come with links to the released chapters\n" +
                "To learn more about each series in detail, use the `/search` command and check out it's components\n" +
                "Usage: `" + Constant.PREFIX + getAliases()[0] + " [type]`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this)
                .addOptions(
                        new OptionData(OptionType.STRING, "type", "Select the type of latest series you want to explore.", true)
                                .addChoice("anime", "anime")
                                .addChoice("manga", "manga")
                );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        final List<OptionMapping> options = event.getOptions();
        final List<String> args = options.stream().map(OptionMapping::getAsString).toList();
        API api = new API();
        if(args.get(0).equals("anime")) {
            InteractionHook msg = event.replyEmbeds(new EmbedBuilder().setDescription("Getting the `Latest` anime of the season...").build()).complete();
            Message message = msg.retrieveOriginal().complete();
            ScheduledFuture<?> timeout = msg.editOriginalEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.red)
                            .setDescription("No recently released anime shows were found for this season! Try again later!")
                            .build()
            ).queueAfter(5, TimeUnit.SECONDS, me -> messageToAnimePage.remove(message.getIdLong()));
            messageToAnimePage.put(message.getIdLong(), new AnimePage(null, message, 1, event.getUser(), timeout));
            api.getLatestAnime(msg);
        } else if(args.get(0).equals("manga")) {
            InteractionHook msg = event.replyEmbeds(new EmbedBuilder().setDescription("Getting the `Latest` manga releases...").build()).complete();
            Message message = msg.retrieveOriginal().complete();
            ScheduledFuture<?> timeout = msg.editOriginalEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.red)
                            .setDescription("No recently released manga were found! Try again later!")
                            .build()
            ).queueAfter(10, TimeUnit.SECONDS, me -> messageToMangaPage.remove(message.getIdLong()));
            messageToMangaPage.put(message.getIdLong(), new MangaPage(null, message, 1, event.getUser(), timeout));
            api.getLatestManga(msg);
        }
    }

    @Override
    public int cooldownDuration() {
        return 30;
    }

}
