//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bots.bot.music.commands;

import com.google.api.services.youtube.YouTubeRequest;

import java.io.IOException;

public class YouTubeRequestInitializer extends com.google.api.services.youtube.YouTubeRequestInitializer {
    private final String key;

    public YouTubeRequestInitializer(String key) {
        this.key = key;
    }

    protected void initializeYouTubeRequest(YouTubeRequest<?> request) throws IOException {
        super.initializeYouTubeRequest(request);
        request.setKey(this.key);
    }
}
