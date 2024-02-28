package bots.bot.music;//

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class JDACommands extends ListenerAdapter {
    private ArrayList<ICommand> commands = new ArrayList();
    private final String prefix;

    public JDACommands(String prefix) {
        this.prefix = prefix;
    }

    public ArrayList<ICommand> getCommands() {
        return this.commands;
    }

    public void setCommands(ArrayList<ICommand> commands) {
        this.commands = commands;
    }

    public void registerCommand(ICommand command) {
        this.commands.add(command);
    }

    private void init(MessageReceivedEvent event) throws InterruptedException {
        Iterator var2 = this.commands.iterator();

        while(var2.hasNext()) {
            ICommand command = (ICommand)var2.next();
            String var10000 = event.getMessage().getContentRaw();
            String var10001 = this.prefix;
            if (var10000.startsWith(var10001 + command.getName())) {
                command.execute(event);
                break;
            }
        }

    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event == null) {
            System.out.println("nothing was requested");
        }

        if (event.getMessage().getContentRaw().startsWith(this.prefix)) {
            try {
                this.init(event);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
