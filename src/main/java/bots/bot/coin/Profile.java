package bots.bot.coin;

import bots.bot.playlist.Playlist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class Profile {
    @Id
    @Column(unique = true)
    private Long idDiscord;
    private int coins;
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Playlist> playlists;
    public Profile(long id){
        this.idDiscord = id;
        this.coins = 0;
        this.playlists = new ArrayList<>();
    }
    public Profile(){
        this.idDiscord = (long)125125125;
        this.coins = 0;
    }

    public Long getId(){
        return this.idDiscord;
    }
    public int getBalance(){ return this.coins; }
    public void update(int amount){
        this.coins += amount;
    }

    @Override
    public String toString() {
        return "prof + " + this.idDiscord + " have " + this.coins + this.playlists;
    }
}