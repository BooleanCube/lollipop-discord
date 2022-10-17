package lollipop.commands;

import lollipop.CommandType;
import lollipop.Constant;
import lollipop.Command;
import lollipop.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.List;

public class Hentai implements Command {

    @Override
    public String[] getAliases() {
        return new String[] {"hentai"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.ROLEPLAY;
    }

    @Override
    public String getHelp() {
        return "Calls somebody a Hentai!\nUsage: `" + Constant.PREFIX + getAliases()[0] + " [user]`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this)
                .addOption(OptionType.USER, "user", "mention a user", true);
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        final List<OptionMapping> options = event.getOptions();
        String[] gifs = {"https://c.tenor.com/MifS9QJUGA4AAAAC/anime-angry.gif", "https://c.tenor.com/-85WiDA6074AAAAC/anime-girl.gif", "https://c.tenor.com/MLsVzlSceaEAAAAC/anime-angry.gif", "https://c.tenor.com/oxqylurVQmkAAAAC/touken-angry.gif", "https://c.tenor.com/0P7u23ALUC0AAAAC/yandere-test-why-comfy-black-hair-anime.gif", "https://c.tenor.com/_G8gPkGWLPEAAAAC/anime-yelling.gif"};
        User target = options.get(0).getAsUser();
        if(event.getUser().getIdLong() == target.getIdLong()) {
            event.replyEmbeds(new EmbedBuilder().setDescription("You can't use Roleplay Commands on yourself!").setColor(Color.red).build()).queue();
            return;
        }
        event.replyEmbeds(new EmbedBuilder()
                .setDescription("**HENTAAAAIIIIIIIIIIIIIIIIIIIIIII**\n" + target.getAsMention() + " was called a **hentai** by " + event.getUser().getAsMention())
                .setImage(gifs[(int)(Math.random()*gifs.length)])
                .build()).queue();
    }

}
