package bots.bot.commands;

import bots.bot.coin.Profile;
import bots.bot.coin.ProfileRepository;
import bots.bot.coin.VoiceListener;
import bots.bot.music.commands.Play;
import bots.bot.music.player.PlayerManager;
import bots.bot.playlist.Playlist;
import bots.bot.playlist.PlaylistRepository;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;

@Component
public class SlashCommandListener extends ListenerAdapter {
    private final ProfileRepository profileRepository;
    private final PlaylistRepository playlistRepository;

    @Autowired
    public SlashCommandListener(ProfileRepository profileRepository, PlaylistRepository playlistRepository) {
        this.profileRepository = profileRepository;
        this.playlistRepository = playlistRepository;
    }
    @Transactional
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "balance" -> balanceCommand(event);
            case "list" -> listCommand(event);
            case "create" -> createCommand(event);
            case "play" -> {
                try {
                    playCommand(event);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            case "add" -> addCommand(event);
            case "remove" -> removeCommand(event);
            case "delete" -> deleteCommand(event);
            case "queue" -> queueCommand(event);
        }
    }

    private void queueCommand(SlashCommandInteractionEvent event) {
        BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue;
        StringBuilder track = new StringBuilder();
        for(AudioTrack audioTrack: queue){
            track.append(audioTrack.getInfo().title).append(" ");
        }
        event.reply(String.valueOf(track) + " " + queue.size()).setEphemeral(true).queue();

    }

    private void deleteCommand(SlashCommandInteractionEvent event) {

        Optional<Profile> optionalProfile = profileRepository.findProfileByPlaylistName(event.getOption("title").getAsString());
        if(optionalProfile.isPresent()){
            Profile profile = optionalProfile.get();
            long ownerId = profile.getIdDiscord();
            List<Playlist> playlists = profile.getPlaylists();
            for(int i =0; i<playlists.size();i++){
                if(playlists.get(i).getName().equals(event.getOption("title").getAsString())){
                    if(event.getMember().getUser().getIdLong() == ownerId || event.getMember().getUser().getIdLong() == 338935846344589321L){
                        System.out.println("Owner id is - " + ownerId);
                        playlists.remove(i);
                        profile.setPlaylists(playlists);
                        for(Playlist playlist: playlists){
                            System.out.println(playlist.getName());
                        }
                        profileRepository.deleteById(profile.getIdDiscord());
                        profileRepository.save(profile);
                        event.reply("```Playlist was successfully deleted!```").setEphemeral(true).queue();
                        return;
                    }else{
                        event.reply("```You can delete only your own playlists```").setEphemeral(true).queue();
                        return;
                    }
                }
            }
        }else{
            event.reply("```Playlist wasn't found!```").setEphemeral(true).queue();
        }
    }

    private void removeCommand(SlashCommandInteractionEvent event) {
        int trackNumber = event.getOption("number").getAsInt()-1;

        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            if(event.getMember().getUser().getIdLong()==playlist.getProfile().getIdDiscord()){
                List<String> list = playlist.getList();
                if(list.size()-1<trackNumber){
                    event.reply("```Enter valid track number```").setEphemeral(true).queue();
                    return;
                }
                list.remove(trackNumber);
                playlist.setList(list);
                playlistRepository.save(playlist);
                event.reply("```Track has been successfully removed```").setEphemeral(true).queue();
            }else{
                event.reply("```You can modify only your own playlist```").setEphemeral(true).queue();
            }
        }else{
            event.reply("```There is no such playlist```").setEphemeral(true).queue();
        }
    }

    private void addCommand(SlashCommandInteractionEvent event) {
        List<String> songs = List.of(event.getOption("tracks").getAsString().split("   "));
        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            if(event.getMember().getUser().getIdLong()==playlist.getProfile().getIdDiscord()){
                List<String> actualSongs = playlist.getList();
                actualSongs.addAll(songs);
                playlist.setList(actualSongs);
                playlistRepository.save(playlist);
                event.reply("```Tracks was successfully added```").setEphemeral(true).queue();
            }else{
                event.reply("```You can modify only your own playlist```").setEphemeral(true).queue();
            }
        }else{
            event.reply("```There is no such playlist```").setEphemeral(true).queue();
        }
    }

    public void balanceCommand(SlashCommandInteractionEvent event){
        Optional<Profile> optionalProfile = profileRepository.findByDiscordId(event.getMember().getIdLong());
        if(optionalProfile.isPresent()){
            Profile profile = optionalProfile.get();
            event.reply("```Your balance is - " + profile.getCoins()+ " grivnas```").setEphemeral(true).queue();
        }else{
            event.reply("```Something gone wrong!```").setEphemeral(true).queue();
        }
    }
    public void listCommand(SlashCommandInteractionEvent event){
        if(event.getOptions().isEmpty()){
            ArrayList<Playlist> playlists = new ArrayList<>(playlistRepository.findAll());
            event.replyEmbeds(CustomEmbed.list(playlists)).queue();
        }else{
            Optional<Profile> profileOptional = profileRepository.findProfileByPlaylistName(event.getOption("title").getAsString());
            if(profileOptional.isPresent()){
                Profile profile = profileOptional.get();
                Playlist playlist = playlistRepository.findPlaylistWithListAndProfileByName(event.getOption("title").getAsString()).get();


                event.replyEmbeds(CustomEmbed.playList(playlist, VoiceListener.memberCache.get(profile.getIdDiscord()).getUser().getName())).queue();
            }else{
                event.reply("```There is no such playlist```").setEphemeral(true).queue();
            }
        }
    }
    public void createCommand(SlashCommandInteractionEvent event){
        Profile profile = profileRepository.findByDiscordId(event.getMember().getIdLong()).get();
        String[] tracks = event.getOption("tracks").getAsString().split("   ");
        Playlist playlist = new Playlist(event.getOption("title").getAsString(), tracks);
        ArrayList<Playlist> list = new ArrayList<>();
        list.add(playlist);
        profile.setPlaylists(list);
        playlist.setProfile(profile);
        event.reply("```Playlist was successfully created!```").setEphemeral(true).queue();
        profileRepository.save(profile);
        System.out.println();
        event.getChannel().asTextChannel().getGuild().getMemberById(playlist.getProfile().getIdDiscord());
    }

    public void playCommand(SlashCommandInteractionEvent event) throws InterruptedException {
        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            Play play = new Play();
            play.executePlaylist(event, playlist.getList(), playlist.getName());
            //НЕ ВИВОДИТЬ ПОВІДОМЛЕННЯ - ОШИБКА
        }else{
            event.reply("```There is no such playlist```").setEphemeral(true).queue();
        }
    }
}
