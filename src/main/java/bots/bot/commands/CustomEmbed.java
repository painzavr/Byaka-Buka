package bots.bot.commands;

import bots.bot.playlist.Playlist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomEmbed {

    public static MessageEmbed list(ArrayList<Playlist> list){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.DARK_GRAY);
        StringBuilder tracksText = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            tracksText.append(String.format("**%d.** %s%n", i + 1, list.get(i).getName()));
        }

        embedBuilder.addField("***List:***", tracksText.toString(), false);

        return embedBuilder.build();
    }
    public static MessageEmbed playList(Playlist playlist, String owner){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("***" + playlist.getName()+"***");
        embedBuilder.setColor(Color.GRAY);

        embedBuilder.addField("Owner", owner, true);
        embedBuilder.addField("Total Tracks", String.valueOf(playlist.getList().size()), true);
        List<String> tracks = playlist.getList();

        StringBuilder tracksText = new StringBuilder();
        for (int i = 0; i < tracks.size(); i++) {
            tracksText.append(String.format("**%d.** %s%n", i + 1, tracks.get(i)));
        }

        embedBuilder.addField("Tracks:", tracksText.toString(), false);

        return embedBuilder.build();
    }
}

