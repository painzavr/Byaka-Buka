package bots.bot.music.commands;



import bots.bot.music.ICommand;
import bots.bot.music.player.GuildMusicManager;
import bots.bot.music.player.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CurrentTrack implements ICommand {
    @Override
    public void execute(MessageReceivedEvent event) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;


        if(audioPlayer.getPlayingTrack() == null){
            event.getChannel().sendMessage("There is no track playing currently.").queue();
            return;
        }

        String title = audioPlayer.getPlayingTrack().getInfo().title;
        String author = audioPlayer.getPlayingTrack().getInfo().author;

        event.getChannel().sendMessage("Now playing: **`" + title + "`** by **`" + author + "`**.").queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

}