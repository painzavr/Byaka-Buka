package bots.bot.commands;

import bots.bot.coin.Profile;
import bots.bot.coin.ProfileRepository;
import bots.bot.music.commands.Play;
import bots.bot.playlist.Playlist;
import bots.bot.playlist.PlaylistRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        }
    }

    private void deleteCommand(SlashCommandInteractionEvent event) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            playlistRepository.delete(playlist);
            event.reply("```Playlist was successfully deleted").queue();
        }else{
            event.reply("```There is no such playlist```").queue();
        }
    }

    private void removeCommand(SlashCommandInteractionEvent event) {
        int trackNumber = event.getOption("number").getAsInt()-1;

        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            List<String> list = playlist.getList();
            if(list.size()-1<trackNumber){
                event.reply("```Enter valid track number```").queue();
                return;
            }
            list.remove(trackNumber);
            playlist.setList(list);
            playlistRepository.save(playlist);
            event.reply("```Track has been successfully removed```").queue();
        }else{
            event.reply("```There is no such playlist```").queue();
        }
    }

    private void addCommand(SlashCommandInteractionEvent event) {
        List<String> songs = List.of(event.getOption("tracks").getAsString().split("   "));
        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            List<String> actualSongs = playlist.getList();
            actualSongs.addAll(songs);
            playlist.setList(actualSongs);
            playlistRepository.save(playlist);
            event.reply("```Tracks was successfully added```").queue();
        }else{
            event.reply("```There is no such playlist```").queue();
        }
    }

    public void balanceCommand(SlashCommandInteractionEvent event){
        System.out.println(profileRepository.findByDiscordId(event.getMember().getIdLong()).get());
        event.reply("```Your balance is - " + profileRepository.findByDiscordId(event.getMember().getIdLong()).get().getBalance()+ " grivnas```").queue();
    }
    public void listCommand(SlashCommandInteractionEvent event){
        if(event.getOptions().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<Playlist> playlists = new ArrayList<>(playlistRepository.findAll());
            event.replyEmbeds(CustomEmbed.list(playlists)).queue();
        }else{
            Optional<Profile> profileOptional = profileRepository.findProfileByPlaylistName(event.getOption("title").getAsString());
            if(profileOptional.isPresent()){
                Profile profile = profileOptional.get();
                Playlist playlist = playlistRepository.findPlaylistWithListAndProfileByName(event.getOption("title").getAsString()).get();
                event.replyEmbeds(CustomEmbed.playList(playlist,event.getGuild().getMemberById(profile.getId()).getNickname())).queue();
            }else{
                event.reply("```There is no such playlist```").queue();
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
        event.reply("```Playlist was successfully created!```").queue();
        profileRepository.save(profile);
        System.out.println();
        event.getChannel().asTextChannel().getGuild().getMemberById(playlist.getProfile().getId());
    }

    public void playCommand(SlashCommandInteractionEvent event) throws InterruptedException {
        Optional<Playlist> optionalPlaylist = playlistRepository.findPlaylistByName(event.getOption("title").getAsString());
        if(optionalPlaylist.isPresent()){
            Playlist playlist = optionalPlaylist.get();
            Play play = new Play();
            play.executePlaylist(event, playlist.getList(), playlist.getName());
            //НЕ ВИВОДИТЬ ПОВІДОМЛЕННЯ - ОШИБКА
        }else{
            event.reply("```There is no such playlist```").queue();
        }
    }
}
