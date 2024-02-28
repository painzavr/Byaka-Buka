package bots.bot.music.commands;



import bots.bot.music.ICommand;
import bots.bot.music.player.GuildMusicManager;
import bots.bot.music.player.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Skip implements ICommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        final TextChannel channel = event.getChannel().asTextChannel();
        final Member self = event.getMember().getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()){
            channel.sendMessage("I need to be in a voice channel for this to work.").queue();
            return;
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if(!memberVoiceState.inAudioChannel()){
            channel.sendMessage("You need to be in a voice channel for this command to work.").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            channel.sendMessage("You need to be in the same voice channel as me for this to work.").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(audioPlayer.getPlayingTrack() == null){
            channel.sendMessage("There is no track playing currently.").queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        channel.sendMessage("Skipped the current track. Now playing **`" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "`** by **`" + musicManager.audioPlayer.getPlayingTrack().getInfo().author + "`**.").queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

}