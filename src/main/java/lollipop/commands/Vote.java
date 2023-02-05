package lollipop.commands;

import lollipop.Command;
import lollipop.CommandType;
import lollipop.Constant;
import lollipop.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Vote implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"vote"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.MISCELLANEOUS;
    }

    @Override
    public String getHelp() {
        return "Vote for lollipop on all the various discord bot lists!\n" +
                "Voting for lollipop on [top.gg](https://top.gg/) gives lollipop users a 1.5x lollipop currency multiplier boost\n" +
                "Voting for lollipop on other discord bot lists helps spread word about lollipop and grows the application\n" +
                "All votes are greatly appreciated by the developers\n" +
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
                        .setTitle("Vote for lollipop!")
                        .setDescription("**Voting on top.gg gives you a 1.5x lollipop multiplier to grow your currency profile faster!**")
                        .setFooter("Every vote towards lollipop helps and is really appreciated! Dropping a review doesn't hurt either..")
                        .setThumbnail("https://cdn-icons-png.flaticon.com/512/927/927295.png")
                        .build()
        ).addActionRow(
                Button.link("https://top.gg/bot/919061572649910292", "Top.gg"),
                Button.link("https://discordbotlist.com/bots/lollipop-4786", "Discord Bot List"),
                Button.link("https://infinitybots.gg/bots/919061572649910292", "Infinity Bot List")
        ).queue();
    }

    @Override
    public int cooldownInSeconds() {
        return 0;
    }

}
