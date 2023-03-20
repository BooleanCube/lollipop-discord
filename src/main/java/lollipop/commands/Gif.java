package lollipop.commands;

import lollipop.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.io.IOException;

public class Gif implements Command {

    @Override
    public String[] getAliases() {
        return new String[] {"gif"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.ANIME;
    }

    @Override
    public String getHelp() {
        return "Retrieve an arbitrarily chosen anime-related GIF\n" +
                "`/gif` returns fancy anime GIFs that can be saved and used later in conversations\n" +
                "No NSFW GIFs should appear at all\n" +
                "Usage: `" + Constant.PREFIX + getAliases()[0] + "`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this);
    }

    static API api = new API();

    @Override
    public void run(SlashCommandInteractionEvent event) {
        InteractionHook message = event.replyEmbeds(
                new EmbedBuilder()
                        .setDescription("Retrieving a Random `GIF`...")
                        .build()
        ).complete();
        api.randomGIF(message);
    }

    @Override
    public int cooldownDuration() {
        return 5;
    }

}
