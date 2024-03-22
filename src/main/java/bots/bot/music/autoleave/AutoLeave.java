package bots.bot.music.autoleave;

import bots.bot.music.player.PlayerManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Date;
import java.util.Timer;
@Getter
public class AutoLeave {
    private Guild guild;
    private Timer leaveTimer;

    public AutoLeave(Guild guild){
        this.guild = guild;
        this.leaveTimer = null;
    }

    public static void leaveAfter5min(Guild guild){
        Timer timer = new Timer();
        timer.schedule(new LeftTimerTask(guild), 1000 * 60 * 5);
    }
}
