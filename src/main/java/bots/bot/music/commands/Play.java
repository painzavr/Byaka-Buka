package bots.bot.music.commands;

import bots.bot.music.ICommand;
import bots.bot.music.SpotifyParser.SpotifyPlaylistParser;
import bots.bot.music.autoleave.AdvancedHashMap;
import bots.bot.music.autoleave.AutoLeave;
import bots.bot.music.autoleave.LeftTimerTask;
import bots.bot.music.player.PlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Play implements ICommand {
    public static AdvancedHashMap<Long, Timer, Long> leaveList = new AdvancedHashMap<>();
    private static final long IDLE_TIME_THRESHOLD = 5 * 60 * 1000; // 5 minutes in milliseconds


    @Override
    public void execute(MessageReceivedEvent event) throws InterruptedException, IOException {

        if(!event.getMember().getVoiceState().inAudioChannel()){
            event.getChannel().asTextChannel().sendMessage("You need to be in a voice channel for this command to work.").queue();
            return;
        }
        if(!event.getMember().getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }

        String[] lines = event.getMessage().getContentRaw().split("\n");
        String[] songs = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            if(lines[i].length()<6){
                return;
            }
            songs[i] = lines[i].substring(6); // Remove "!play " prefix
        }
        for(String link: songs) {
            if (!isUrl(link)) {
                link = "ytsearch:" + String.join(" ", link) + " audio";
            }
            if(link.contains("open.spotify")){
                link = "ytsearch: " + SpotifyPlaylistParser.parseSoloTrack(link) + " audio";
            }
            System.out.println(event.getMember().getUser().getName() + " " + event.getGuild().getName());

            PlayerManager.getInstance().loadAndPlay(event.getChannel().asTextChannel(), link);


            Thread.sleep(1000);
            setTimer(event.getGuild(),1);

        }
    }

    private void setTimer(Guild guild, int quantity) {
        if(!leaveList.containsKey(guild.getIdLong())){
            Timer timer = new Timer();
            timer.schedule(new LeftTimerTask(guild), IDLE_TIME_THRESHOLD*quantity);
            long executeTime = System.currentTimeMillis() + IDLE_TIME_THRESHOLD*quantity;
            leaveList.put(guild.getIdLong(), timer, executeTime);
            System.out.println("Going to leave from " + guild.getName() + " at " + new Date(executeTime));
        }else{
            leaveList.get(guild.getIdLong()).getValue1().cancel();
            Timer timer = new Timer();
            long newDelay = leaveList.get(guild.getIdLong()).getValue2()-System.currentTimeMillis()+IDLE_TIME_THRESHOLD*quantity;
            System.out.println("New delay time is - " + newDelay/1000.0 + " sec");
            timer.schedule(new LeftTimerTask(guild), newDelay);
            leaveList.updateValues(guild.getIdLong(), timer, newDelay+System.currentTimeMillis());
            System.out.println("Previous timer was canceled! Starting new one:");
            System.out.println("Going to leave from " + guild.getName() + " at " + new Date(newDelay+System.currentTimeMillis()));
        }
    }

    public void executePlaylist(SlashCommandInteractionEvent event, List<String> songs, String songName) throws InterruptedException, IOException {

        if(!event.getMember().getVoiceState().inAudioChannel()){
            event.getChannel().asTextChannel().sendMessage("You need to be in a voice channel for this command to work.").queue();
            return;
        }
        if(!event.getMember().getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        List<String> urlSong = new ArrayList<>();
        for(String link: songs) {
            if (!isUrl(link)) {
                urlSong.add("ytsearch:" + String.join(" ", link) + " audio");
            } else {
                if(link.contains("open.spotify")){
                    link = "ytsearch: " + SpotifyPlaylistParser.parseSoloTrack(link) + " audio";
                }
                urlSong.add(link);
            }
        }
        for(String song: urlSong){
            System.out.println(song);
        }
        System.out.println(event.getMember().getUser().getName() + " " + event.getGuild().getName());

        PlayerManager.getInstance().loadAndPlaylist(event.getChannel().asTextChannel(), urlSong);

        setTimer(event.getGuild(), urlSong.size());

        event.reply("```You are listening to "+ songName + "```").setEphemeral(true).queue();
    }

    private boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        } catch (URISyntaxException e){
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }
}