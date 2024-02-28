package bots.bot.coin;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class VoiceListener extends ListenerAdapter {

    private final ProfileRepository profileRepository;

    @Autowired
    public VoiceListener(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    private final Map<Long, Long> voiceStartTime = new HashMap<>();
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event){
        Member member = event.getEntity();
        if(profileRepository.findByDiscordId(member.getIdLong()).isPresent()&&!member.getUser().isBot()){
            if (event.getChannelLeft() == null && event.getChannelJoined() != null) {
                System.out.println(member.getUser().getName() + " joined the " + event.getGuild().getName());
                voiceStartTime.put(event.getMember().getIdLong(), System.currentTimeMillis());
            } else if (event.getChannelLeft() != null && event.getChannelJoined() == null && !member.getUser().isBot()) {
                System.out.println(member.getUser().getName() + " left the " + event.getGuild().getName());
                long timeSpent = System.currentTimeMillis() - voiceStartTime.get(event.getMember().getIdLong());
                int coinsEarned = (int) (timeSpent / 1000);
                Profile forUpdate = profileRepository.findByDiscordId(member.getIdLong()).get();
                forUpdate.update(coinsEarned);
                profileRepository.save(forUpdate);
                voiceStartTime.remove(event.getMember().getIdLong());
            }
        }else if(!member.getUser().isBot()){
            System.out.println(member.getUser().getName() + " first time in " + event.getGuild().getName());
            Profile profile = new Profile(member.getIdLong());
            profileRepository.save(profile);
            voiceStartTime.put(event.getMember().getIdLong(), System.currentTimeMillis());
        }
    }
}
