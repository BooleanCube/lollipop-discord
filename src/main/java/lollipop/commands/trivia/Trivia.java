package lollipop.commands.trivia;

import lollipop.*;
import lollipop.commands.trivia.models.TGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Trivia implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"trivia"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.FUN;
    }

    @Override
    public String getHelp() {
        return "Guess the anime in a trivia game to win lollipop currency.\n" +
                "Winners are rewarded with 40-60 lollipops for every correct answer, but they will lose 10-20 lollipops for every incorrect answer\n" +
                "Player will be presented with additional data to narrow down down the 4 options and guess the correct anime series\n" +
                "All titles are randomly generated and there is guaranteed to be only one correct answer\n" +
                "Usage: `" + Constant.PREFIX + getAliases()[0] + "`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this);
    }

    static API api = new API();
    public static HashMap<Long, TGame> openGames = new HashMap<>();

    @Override
    public void run(SlashCommandInteractionEvent event) {
        if(Trivia.openGames.values().stream().anyMatch(g -> g.user == event.getUser())) {
            event.replyEmbeds(
                    new EmbedBuilder()
                            .setDescription("You are currently playing a different trivia game! Please finish that game first!")
                            .setColor(Color.red)
                            .build()
            ).setEphemeral(true).queue();
            return;
        }
        InteractionHook msg = event.replyEmbeds(
                new EmbedBuilder()
                        .setDescription("Generating random question...")
                        .build()
        ).complete();
        Message m = msg.retrieveOriginal().complete();
        ScheduledFuture<?> timeout = msg.editOriginalEmbeds(new EmbedBuilder()
                .setColor(Color.red)
                .setDescription("Could not start a trivia game! Please try again later!")
                .build()
        ).queueAfter(5, TimeUnit.SECONDS, i -> openGames.remove(m.getIdLong()));
        Trivia.openGames.put(m.getIdLong(), new TGame(event.getUser(), timeout));
        api.randomTrivia(msg);
    }

}
