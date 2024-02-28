package bots.bot.music.commands;



import bots.bot.music.ICommand;
import bots.bot.music.player.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Stop implements ICommand {

    @Override
    public void execute(MessageReceivedEvent event) {

        if(!event.getMember().getVoiceState().inAudioChannel()){
            event.getChannel().sendMessage("You need to be in a voice channel for this command to work.").queue();
            return;
        }
            PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.player.stopTrack();
            PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.queue.clear();
            event.getGuild().getAudioManager().closeAudioConnection();
            event.getChannel().sendMessage("The player has been stopped and the queue has been cleared.").queue();

    }

    @Override
    public String getName() {
        return "stop";
    }

}