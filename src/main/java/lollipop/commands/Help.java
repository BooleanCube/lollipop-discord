package lollipop.commands;

import lollipop.*;
import net.bytebuddy.matcher.CollectionOneToOneMatcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Help implements Command {

    Manager manager;

    /**
     * Initialize command manager
     * @param m command manager
     */
    public Help(Manager m) {
        this.manager = m;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"help"};
    }

    @Override
    public CommandType getCategory() {
        return CommandType.MISCELLANEOUS;
    }

    @Override
    public String getHelp() {
        return "Learn more about lollipop and explore all the available commands!\n" +
                "`/help` will retrieve a list of all commands sorted into their corresponding category\n" +
                "Specifying a command name within the `/help` command parameters will give a detailed description of the given command\n" +
                "Usage: `" + Constant.PREFIX + getAliases()[0] + " <command*>`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this)
                .addOption(OptionType.STRING, "command", "command name", false);
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        final List<OptionMapping> options = event.getOptions();
        final List<String> args = options.stream().map(OptionMapping::getAsString).collect(Collectors.toList());
        if(args.size() > 1) {
            Tools.wrongUsage(event, this);
            return;
        }
        if(args.isEmpty()) {
            StringBuilder anime = new StringBuilder();
            manager.getCommands(CommandType.ANIME).forEach(command -> anime.append("**`").append(command.getAliases()[0]).append("`**, "));
            StringBuilder fun = new StringBuilder();
            manager.getCommands(CommandType.FUN).forEach(command -> fun.append("**`").append(command.getAliases()[0]).append("`**, "));
            StringBuilder roleplay = new StringBuilder();
            manager.getCommands(CommandType.ROLEPLAY).forEach(command -> roleplay.append("**`").append(command.getAliases()[0]).append("`**, "));
            StringBuilder misc = new StringBuilder();
            manager.getCommands(CommandType.MISCELLANEOUS).forEach(command -> misc.append("**`").append(command.getAliases()[0]).append("`**, "));
            EmbedBuilder e = new EmbedBuilder()
                    .setAuthor(event.getUser().getName(), event.getUser().getAvatarUrl(), event.getUser().getEffectiveAvatarUrl())
                    .setFooter("Use " + Constant.PREFIX + "help [command] to get more information about a command!")
                    .setTitle("Lollipop Commands");
            e.setImage("https://user-images.githubusercontent.com/47650058/147891305-58aa09b6-2053-4180-9a9a-8c09826567f1.png");
            e.addField("Anime", anime.substring(0, anime.length()-2), true);
            e.addField("Fun", fun.substring(0, fun.length()-2), true);
            e.addField("Misc", misc.substring(0, misc.length()-2), true);
            e.addField("Roleplay", roleplay.substring(0, roleplay.length()-2), true);
            e.setDescription("Visit the [lollipop bot website](https://booleancube.github.io/projects/lollipop/lollipop.html) or use `/botinfo` to learn more about the lollipop!\n" +
                    "Use `/support` to join a support server if you have bugs to report and features to suggest!");
            if(event.getUser().getIdLong() == Constant.OWNER_ID) {
                StringBuilder owner = new StringBuilder();
                manager.getCommands(CommandType.OWNER).forEach(command -> owner.append("**`").append(command.getAliases()[0]).append("`**, "));
                e.addField("Owner", owner.substring(0, owner.length()-2), false);
            }
            event.replyEmbeds(e.build()).queue();
            return;
        }
        Command command = manager.getCommand(String.join("", args));
        if(command == null) {
            event.reply("The command `" + String.join("", args) + "` does not exist!\n" +
                    "Use `" + Constant.PREFIX + getAliases()[0] + "` for a list of all my commands!").queue();
            return;
        }
        event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Command Help: `" + command.getAliases()[0] + "`")
                        .setDescription(command.getHelp())
                        .setFooter("If the argument is followed by a *, then the argument is optional.")
                        .build()
        ).queue();
    }

}
