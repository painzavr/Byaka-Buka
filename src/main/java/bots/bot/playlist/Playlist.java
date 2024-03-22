package bots.bot.playlist;

import bots.bot.coin.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistId;

    @Fetch(FetchMode.JOIN)
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "playlistId"))
    private List<String> list;

    private String name;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "idDiscord")
    @JsonIgnore
    private Profile profile;

    public Playlist(String name, String[] tracks){
        this.name = name;
        this.list = new ArrayList<>(Arrays.asList(tracks));
    }
    public Playlist(String name){
        this.name = name;
        list = new ArrayList<>();
    }

    public Playlist() {

    }
    @Override
    public String toString(){
        StringBuilder ride = new StringBuilder();
        for(String track: this.list){
            ride.append(track);
        }
        return "Title - " + this.name + " || tracks: " + ride;
    }
}
