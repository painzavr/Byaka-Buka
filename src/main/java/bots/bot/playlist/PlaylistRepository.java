package bots.bot.playlist;

import bots.bot.playlist.Playlist;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {


    @EntityGraph(attributePaths = {"list", "profile"})
    Optional<Playlist> findPlaylistByName(String name);
    @EntityGraph(attributePaths = {"list", "profile"})
    Optional<Playlist> findPlaylistWithListAndProfileByName(String name);


}
