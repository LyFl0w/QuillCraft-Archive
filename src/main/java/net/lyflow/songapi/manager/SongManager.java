package net.lyflow.songapi.manager;

import net.lyflow.songapi.song.PlayerSongQuitListener;
import net.lyflow.songapi.song.Song;
import net.lyflow.songapi.song.utils.SongParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class SongManager {

    private final HashMap<String, Song> songs;

    public SongManager(JavaPlugin javaPlugin){
        songs = new HashMap<>();
        loadSongs(javaPlugin);
    }

    private void loadSongs(JavaPlugin javaPlugin){
        final Logger logger = javaPlugin.getLogger();
        final File folderSongs = new File(javaPlugin.getDataFolder(), "/songs");
        if(!folderSongs.exists()){
            logger.severe("""
                            The songs directory does not exist
                            Songs files can't be loaded
                            """);
            return;
        }

        Arrays.stream(Objects.requireNonNull(folderSongs.listFiles())).forEach(songFile -> {
            String name = songFile.getName();
            if(name.endsWith(".nbs")){
                try{
                    final Song song = new SongParser(songFile).getSong();
                    name = name.replace(".nbs", "");
                    if(songs.containsKey(name)){
                        songs.replace(name, song);
                    }else{
                        songs.put(name, song);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                logger.warning("The file \""+name+"\" is not an NBS file");
            }
        });

        javaPlugin.getServer().getPluginManager().registerEvents(new PlayerSongQuitListener(), javaPlugin);
    }

    public Song getSong(String songName){
        return songs.get(songName);
    }

}
