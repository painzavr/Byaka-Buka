package bots.bot.coin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }

    public List<Profile> getProfiles(){
        return profileRepository.findAll();
    }

    public Profile getProfile(Long id){
        Optional<Profile> optionalProfile = profileRepository.findByDiscordId(id);
        return optionalProfile.orElse(null);
    }
}
