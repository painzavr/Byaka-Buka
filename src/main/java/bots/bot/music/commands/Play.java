package bots.bot.music.commands;

import bots.bot.music.ICommand;
import bots.bot.music.player.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Play implements ICommand {
    private Map<Long, Long> guildLeave = new HashMap<>();
    private Map<Long, Timer> guildTimer = new HashMap<>();
    private static final long IDLE_TIME_THRESHOLD = 5 * 60 * 1000; // 5 minutes in milliseconds
    private long duration;
    private long currentTime;


    @Override
    public void execute(MessageReceivedEvent event) throws InterruptedException {

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
            songs[i] = lines[i].substring(6); // Remove "!play " prefix
        }
        for(String link: songs) {
            if (!isUrl(link)) {
                link = "ytsearch:" + String.join(" ", link) + " audio";
            }
            System.out.println(link);
            System.out.println(event.getMember().getUser().getName() + " " + event.getGuild().getName());

            PlayerManager.getInstance().loadAndPlay(event.getChannel().asTextChannel(), link);
            Thread.sleep(1000);

            if(!guildLeave.containsKey(event.getGuild().getIdLong())){
                currentTime = System.currentTimeMillis();
                guildLeave.put(event.getGuild().getIdLong(), currentTime + 300_000L);
                guildTimer.put(event.getGuild().getIdLong(), new Timer());
            }else{
                duration = guildLeave.get(event.getGuild().getIdLong()) + 300_000L;
                guildLeave.replace(event.getGuild().getIdLong(), duration );
            }
            startIdleTimer(event.getGuild(),guildLeave.get(event.getGuild().getIdLong()));
        }
    }

    public void executePlaylist(SlashCommandInteractionEvent event, List<String> songs, String songName) throws InterruptedException {

        if(!event.getMember().getVoiceState().inAudioChannel()){
            event.getChannel().asTextChannel().sendMessage("You need to be in a voice channel for this command to work.").queue();
            return;
        }
        if(!event.getMember().getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }
        List<String> urlSong = new ArrayList<>();
        for(String link: songs) {
            if (!isUrl(link)) {
                urlSong.add("ytsearch:" + String.join(" ", link) + " audio");
            } else {
                urlSong.add(link);
            }
        }
        for(String song: urlSong){
            System.out.println(song);
        }
        System.out.println(event.getMember().getUser().getName() + " " + event.getGuild().getName());

        PlayerManager.getInstance().loadAndPlaylist(event.getChannel().asTextChannel(), urlSong);

        if(!guildLeave.containsKey(event.getGuild().getIdLong())){
            currentTime = System.currentTimeMillis();
            guildLeave.put(event.getGuild().getIdLong(), currentTime + 300_000L*urlSong.size());
            guildTimer.put(event.getGuild().getIdLong(), new Timer());
        }else{
            duration = guildLeave.get(event.getGuild().getIdLong()) + 300_000L*urlSong.size();
            guildLeave.replace(event.getGuild().getIdLong(), duration );
        }
        startIdleTimer(event.getGuild(),guildLeave.get(event.getGuild().getIdLong()));

        event.reply("```You are listening to "+ songName + "```").queue();
    }
    public void startIdleTimer(Guild guild, long currentTime) {
        if (guildTimer.get(guild.getIdLong()) != null) {
            guildTimer.get(guild.getIdLong()).cancel();
        }
        guildTimer.replace(guild.getIdLong(), new Timer());
        Date scheduledTime = new Date(currentTime+IDLE_TIME_THRESHOLD);
        System.out.println("GONNA LEAVE AT - " + scheduledTime);
        guildTimer.get(guild.getIdLong()).schedule(new MyTimer(guild), scheduledTime);
    }

    class MyTimer extends TimerTask {
        private final Guild guild;

        public MyTimer(Guild guild) {
            this.guild = guild;
        }

        @Override
        public void run() {
            guild.getAudioManager().closeAudioConnection();
            guildLeave.remove(guild.getIdLong());
            guildTimer.remove(guild.getIdLong());
            System.out.println("Gonna leave from - " + guild.getName());
        }
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