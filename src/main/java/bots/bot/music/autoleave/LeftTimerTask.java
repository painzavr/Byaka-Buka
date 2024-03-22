package bots.bot.music.autoleave;

import bots.bot.music.commands.Play;
import bots.bot.music.player.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.TimerTask;

public class LeftTimerTask extends TimerTask {
    private final Guild guild;

    public LeftTimerTask(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void run() {
        if(PlayerManager.getInstance().getMusicManager(guild).audioPlayer.getPlayingTrack()==null) {
            Play.leaveList.remove(guild.getIdLong());
            guild.getAudioManager().closeAudioConnection();
        }else{
            AutoLeave.leaveAfter5min(guild);
        }
    }
}

