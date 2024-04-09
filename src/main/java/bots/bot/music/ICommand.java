package bots.bot.music;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public interface ICommand {
    String getName();
    void execute(MessageReceivedEvent event) throws InterruptedException, IOException;
}
