package com.craftmend.openaudiomc.spigot.modules.playlists;

import com.craftmend.openaudiomc.generic.database.DatabaseService;
import com.craftmend.openaudiomc.generic.database.internal.Repository;
import com.craftmend.openaudiomc.generic.service.Inject;
import com.craftmend.openaudiomc.generic.service.Service;
import com.craftmend.openaudiomc.spigot.modules.playlists.models.Playlist;
import com.craftmend.openaudiomc.spigot.modules.playlists.models.PlaylistEntry;

import java.util.HashMap;
import java.util.Map;

public class PlaylistService extends Service {

    @Inject
    private DatabaseService databaseService;

    private Repository<Playlist> playlistRepository;
    private Repository<PlaylistEntry> playlistEntryRepository;

    private Map<String, Playlist> cachedPlaylists = new HashMap<>();

    @Override
    public void onEnable() {
        playlistRepository = databaseService.getRepository(Playlist.class);
        playlistEntryRepository = databaseService.getRepository(PlaylistEntry.class);

        for (Playlist value : playlistRepository.values()) {
            cachedPlaylists.put(value.getName(), value);
        }
    }

    @Override
    public void onDisable() {
        saveAll();
    }

    private void saveAll() {
        for (Playlist value : cachedPlaylists.values()) {
            // save the playlist itself
            playlistRepository.save(value);

            // delete entries
            for (PlaylistEntry deletedEntry : value.getDeletedEntries()) {
                playlistEntryRepository.delete(deletedEntry);
            }
            value.getDeletedEntries().clear();

            // save the other entries
            for (PlaylistEntry entry : value.getEntries()) {
                playlistEntryRepository.save(entry);
            }
        }
    }

    public Playlist getPlaylist(String name) {
        return cachedPlaylists.get(name);
    }

    public Playlist createPlaylist(String name, String creator) {
        Playlist playlist = new Playlist(name, creator);
        cachedPlaylists.put(name, playlist);
        playlistRepository.save(playlist);
        return playlist;
    }

}
