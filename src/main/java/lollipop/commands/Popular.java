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

public class Popular implements Command {

    public static HashMap<Long, AnimePage> messageToAnimePage = new HashMap<>();
    public static HashMap<Long, MangaPage> messageToMangaPage = new HashMap<>();

    @Override
    public String[] getAliases() {
        return new String[]{"popular"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.ANIME;
    }

    @Override
    public String getHelp() {
        return "Explore the top twenty most popular anime and manga series being released of the season.\n" +
                "The most popular animes come with a trailer component to watch and the most popular mangas come with view counts\n" +
                "To learn more about each series in detail, use the `/search` command and check out it's components\n" +
                "Usage: `" + Constant.PREFIX + getAliases()[0] + " [type]`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this)
                .addOptions(
                        new OptionData(OptionType.STRING, "type", "Select the type of most popular series you want to explore.", true)
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
            InteractionHook msg = event.replyEmbeds(
                    new EmbedBuilder()
                            .setDescription("Getting the `Most Popular` anime...")
                            .build()
            ).complete();
            Message message = msg.retrieveOriginal().complete();
            ScheduledFuture<?> timeout = msg.editOriginalEmbeds(new EmbedBuilder()
                    .setColor(Color.red)
                    .setDescription("Could not get the most popular anime shows! Please try again later!")
                    .build()
            ).queueAfter(5, TimeUnit.SECONDS, me -> messageToAnimePage.remove(message.getIdLong()));
            messageToAnimePage.put(message.getIdLong(), new AnimePage(null, message, 1, event.getUser(), timeout));
            api.getPopularAnime(msg);
        } else if(args.get(0).equals("manga")) {
            InteractionHook msg = event.replyEmbeds(
                    new EmbedBuilder()
                            .setDescription("Getting the `Most Popular` mangas...")
                            .build()
            ).complete();
            Message message = msg.retrieveOriginal().complete();
            ScheduledFuture<?> timeout = msg.editOriginalEmbeds(new EmbedBuilder()
                    .setColor(Color.red)
                    .setDescription("Could not get the most popular mangas! Please try again later!")
                    .build()
            ).queueAfter(5, TimeUnit.SECONDS, me -> messageToMangaPage.remove(message.getIdLong()));
            messageToMangaPage.put(message.getIdLong(), new MangaPage(null, message, 1, event.getUser(), timeout));
            api.getPopularManga(msg);
        }
    }

}
