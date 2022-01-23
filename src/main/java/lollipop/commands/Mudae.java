package lollipop.commands;

import lollipop.CONSTANT;
import lollipop.Command;
import lollipop.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.List;

public class Mudae implements Command {
    @Override
    public String[] getAliases() {
        return new String[] {"mudae"};
    }

    @Override
    public String getCategory() {
        return "Roleplay";
    }

    @Override
    public String getHelp() {
        return "Mudae Mudae Mudae\nUsage: `" + CONSTANT.PREFIX + getAliases()[0] + " [user]`";
    }

    @Override
    public CommandData getSlashCmd() {
        return Tools.defaultSlashCmd(this)
                .addOption(OptionType.USER, "user", "specified member", true);
    }

    @Override
    public void run(List<String> args, SlashCommandEvent event) {
        if(args.isEmpty()) { Tools.wrongUsage(event.getTextChannel(), this); return; }
        String[] mudaegifs = {"https://tenor.com/view/jojo-jojos-bizarre-adventures-ora-ora-ora-the-world-stand-gif-15566871", "https://tenor.com/view/jojo-jotaro-dio-brando-kujo-gif-21583125", "https://tenor.com/view/dio-jojo-jjba-anime-fire-gif-16677683"};
        Member target = Tools.getEffectiveMember(event.getGuild(), String.join(" ", args));
        if(target == null) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Could not find the specified member!").setColor(Color.red).build()).queue();
            return;
        }
        event.reply("**MUDAE MUDAE MUDAE MUDAAAEEEEEEE**\n" + target.getAsMention() + " was powdered by " + event.getMember().getAsMention()).queue();
        event.getChannel().sendMessage(mudaegifs[(int)(Math.random()*mudaegifs.length)]).queue();
    }
}
