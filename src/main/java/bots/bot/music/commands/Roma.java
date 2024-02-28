package bots.bot.music.commands;

import bots.bot.music.ICommand;
import bots.bot.music.player.PlayerManager;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roma implements ICommand {
    private static final String API_KEY = "AIzaSyDfLiKxMCqCb-X6CHul8vfs6qpl4AS-wWI";

    public Roma() {
    }

    public String getName() {
        return "roma";
    }

    public void execute(MessageReceivedEvent event) {
        try {
            String[] word = event.getMessage().getContentRaw().split("\\s+", 2);
            String link = word[1];
            String playlistId = extractPlaylistId(link);
            YouTube youtubeService = getService("AIzaSyDfLiKxMCqCb-X6CHul8vfs6qpl4AS-wWI");
            List<String> videoUrls = parseYoutubePlaylist(youtubeService, playlistId);
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.getChannel().asTextChannel().sendMessage("You need to be in a voice channel for this command to work.").queue();
                return;
            }

            if (!event.getMember().getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                AudioManager audioManager = event.getGuild().getAudioManager();
                VoiceChannel memberChannel = (VoiceChannel)event.getMember().getVoiceState().getChannel();
                audioManager.openAudioConnection(memberChannel);
            }

            PlayerManager.getInstance().loadAndPlaylist(event.getChannel().asTextChannel(), videoUrls);
        } catch (IOException | GeneralSecurityException var9) {
            var9.printStackTrace();
        }

    }

    public static List<String> parseYoutubePlaylist(YouTube youtube, String playlistId) throws IOException {
        List<String> videoUrls = new ArrayList();
        int counter = 0;
        YouTube.PlaylistItems.List request = youtube.playlistItems().list("snippet").setPlaylistId(playlistId).setMaxResults(50L);
        String nextPageToken = null;

        do {
            PlaylistItemListResponse response = (PlaylistItemListResponse)request.execute();
            List<PlaylistItem> items = response.getItems();

            for(Iterator var8 = items.iterator(); var8.hasNext(); ++counter) {
                PlaylistItem item = (PlaylistItem)var8.next();
                String videoId = item.getSnippet().getResourceId().getVideoId();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                videoUrls.add(videoUrl);
                System.out.println(videoUrl);
            }

            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while(nextPageToken != null && counter < 30);

        return videoUrls;
    }

    public static YouTube getService(String apiKey) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return (new YouTube.Builder(httpTransport, jsonFactory, (request) -> {
        })).setApplicationName("YourAppName").setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey)).build();
    }

    public static String extractPlaylistId(String url) {
        String playlistId = null;
        String regex = "(?<=list=)[^&$]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            playlistId = matcher.group();
        }

        return playlistId;
    }
}
