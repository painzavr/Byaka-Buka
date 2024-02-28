package bots.bot.playlist;

import bots.bot.coin.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "playlistId"))
    private List<String> list;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "idDiscord")
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
