package lollipop;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Listener class which listens for commands
 */
public class Listener extends ListenerAdapter {

    public Manager m = null;
    public TestCM testM = null;

    /**
     * Notify that the bot application is online on startup!
     * @param event ready event
     */
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        System.out.println(event.getJDA().getSelfUser().getName() + " is online!");
        m = new Manager();
        testM = new TestCM();
    }

    /**
     * Triggered when a slash command is called and gets the command object connecte to the slash command through the command manager
     * @param event slash command interaction event
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("shutdown") && (event.getUser().getIdLong()== Constant.OWNER_ID)) {
            event.getJDA().shutdown();
            System.exit(0);
        }
        //if(event.getJDA().getSelfUser().getIdLong() == CONSTANT.TESTID) { testM.run(event); return; }
        m.run(event);
    }

    /**
     * Triggered when a message is received
     * @param event message received event
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().startsWith("l!reload") && event.getAuthor().getIdLong() == Constant.OWNER_ID) {
            String[] possible = event.getMessage().getContentRaw().split(" ", 2);
            List<String> args = new ArrayList<>();
            if(possible.length == 2) args = Arrays.asList(possible[1].split(" "));
            event.getGuild().updateCommands().queue();
            event.getJDA().updateCommands().queue();
            if(args.size() == 0) {
                m.reloadCommands(event.getJDA());
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setDescription("Reloaded all slash commands!")
                        .build()
                ).queue();
            } else if(args.size() == 1) {
                Command c = m.getCommand(args.get(0));
                if(c == null) {
                    event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setDescription("The command `" + args.get(0) + "` does not exist!")
                            .setColor(Color.RED)
                            .build()
                    ).queue();
                    return;
                }
                m.reloadCommand(event.getJDA(), c);
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setDescription("Successfully reloaded the `" + c.getAliases()[0] + "` command!")
                        .setColor(Color.GREEN)
                        .build()
                ).queue();
            }
        } else if(event.getMessage().getContentRaw().startsWith("l!greload") && event.getAuthor().getIdLong() == Constant.OWNER_ID) {
            String[] possible = event.getMessage().getContentRaw().split(" ", 2);
            List<String> args = new ArrayList<>();
            if(possible.length == 2) args = Arrays.asList(possible[1].split(" "));
            event.getGuild().updateCommands().queue();
            if(args.size() == 0) {
                m.reloadCommands(event.getGuild());
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setDescription("Reloaded all slash commands!")
                        .build()
                ).queue();
            } else if(args.size() == 1) {
                Command c = m.getCommand(args.get(0));
                if(c == null) {
                    event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setDescription("The command `" + args.get(0) + "` does not exist!")
                            .setColor(Color.RED)
                            .build()
                    ).queue();
                    return;
                }
                m.reloadCommand(event.getGuild(), c);
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setDescription("Successfully reloaded the `" + c.getAliases()[0] + "` command!")
                        .setColor(Color.GREEN)
                        .build()
                ).queue();
            }
        } else if(event.getMessage().getContentRaw().startsWith("l!shutdown") && event.getAuthor().getIdLong() == Constant.OWNER_ID) {
            event.getJDA().getShardManager().shutdown();
            System.exit(0);
        }
    }

}
