package com.oscarliang.spotifyclone.core.domain;

import com.oscarliang.spotifyclone.core.data.repository.MusicRepository;
import com.oscarliang.spotifyclone.core.data.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.core.model.Music;
import com.oscarliang.spotifyclone.core.model.Playlist;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class UpdatePlaylistUseCase {

    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;

    @Inject
    public UpdatePlaylistUseCase(
            MusicRepository musicRepository,
            PlaylistRepository playlistRepository
    ) {
        this.musicRepository = musicRepository;
        this.playlistRepository = playlistRepository;
    }

    public Completable execute(
            Playlist playlist,
            String playlistName,
            List<String> musicIds
    ) {
        return musicRepository
                .getCachedMusicsByIds(musicIds)
                .flatMapCompletable(musics -> {
                    // Update the new playlist
                    playlist.setName(playlistName);
                    playlist.setMusicIds(musicIds);

                    // Update playlist image to the first music
                    // image, set to blank if playlist is empty
                    if (musicIds.isEmpty()) {
                        playlist.setImageUrl("");
                    } else {
                        playlist.setImageUrl(getFirstMusicImage(musics, musicIds.get(0)));
                    }

                    return playlistRepository.updatePlaylist(playlist);
                });
    }

    private String getFirstMusicImage(List<Music> musics, String id) {
        for (Music music : musics) {
            if (Objects.equals(music.getId(), id)) {
                return music.getImageUrl();
            }
        }

        return "";
    }

}