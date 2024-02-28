package bots.bot;

import bots.bot.coin.ProfileRepository;
import bots.bot.coin.VoiceListener;
import bots.bot.commands.SlashCommandListener;
import bots.bot.music.JDACommands;
import bots.bot.music.commands.*;
import bots.bot.playlist.PlaylistRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
@ComponentScan(basePackages = {"bots.bot.coin", "bots.bot"})
@SpringBootApplication

public class mBot {
    public String token;
    public static JDA jda;


    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT};
    public static void main(String[] args) throws LoginException, InterruptedException {
        mBot mb = new mBot();
        System.out.println(mb.token);
        ApplicationContext applicationContext = SpringApplication.run(mBot.class, args);


        // Теперь у вас есть доступ к ApplicationContext
        ProfileRepository profileRepository = applicationContext.getBean(ProfileRepository.class);
        PlaylistRepository playlistRepository = applicationContext.getBean(PlaylistRepository.class);

        VoiceListener voiceListener = new VoiceListener(profileRepository);

        JDACommands jdaCommands = new JDACommands("!");

        jdaCommands.registerCommand(new Play());
        jdaCommands.registerCommand(new Stop());
        jdaCommands.registerCommand(new Skip());
        jdaCommands.registerCommand(new Pause());
        jdaCommands.registerCommand(new CurrentTrack());
        jdaCommands.registerCommand(new Roma());


        jda = JDABuilder.createDefault("MTExNDYzMzIyOTk3MDk3Njg0OA.G94ITX.DFTEEMFdyrWxLfGNT_5iE09OdeIvWKMh8uwvys")
                .setActivity(Activity.watching("checks"))
                        .addEventListeners(jdaCommands)
                        .addEventListeners(voiceListener)
                        .addEventListeners(new SlashCommandListener(profileRepository, playlistRepository))
                        .enableIntents(Arrays.asList(INTENTS))
                        .build();
        jda.updateCommands().addCommands(
                Commands.slash("play", "play the chosen playlist by its name")
                        .addOption(OptionType.STRING, "title", "Title of the playlist", true),
                Commands.slash("balance", "Check your balance"),
                Commands.slash("create", "Create your own playlist")
                        .addOption(OptionType.STRING, "title", "Title of the playlist", true)
                        .addOption(OptionType.STRING, "tracks", "List of the tracks separated with triple SPACE", true),
                Commands.slash("list", "Show the list of playlists")
                        .addOption(OptionType.STRING, "title", "Show the track list of playlist"),
                Commands.slash("add","Add tracks to playlist")
                        .addOption(OptionType.STRING, "title", "Name of the playlist to which the tracks will be added", true)
                        .addOption(OptionType.STRING,"tracks", "Name of the tracks to be add, separated with triple space", true),
                Commands.slash("remove", "Remove tracks from playlist")
                        .addOption(OptionType.STRING, "title", "Name of the playlist from which the tracks will be removed", true)
                        .addOption(OptionType.INTEGER, "number","Number of the track to be removed", true),
                Commands.slash("delete", "Delete chosen playlist")
                        .addOption(OptionType.STRING, "title", "Title of the playlist to be deleted")

        ).queue();
        jda.awaitReady();

    }



}
