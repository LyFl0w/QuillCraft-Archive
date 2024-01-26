package net.lyflow.songapi.song;

import net.lyflow.songapi.song.data.InstrumentNBS;
import net.lyflow.songapi.song.data.Note;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RadioPlayer{

    protected static final ArrayList<RadioPlayer> radiosPlayers = new ArrayList<>();

    private final JavaPlugin javaPlugin;

    private List<Song> playListSongs;
    private final List<Song> songs;
    private final ArrayList<String> players;
    private boolean loop;
    private boolean random;
    private boolean autoRemove;
    private boolean playing;

    private int songIterator;

    private SongPlayerScheduler songPlayerScheduler;

    public RadioPlayer(JavaPlugin javaPlugin, boolean loop, boolean random, boolean autoRemove, Song... songs){
        this(javaPlugin, new ArrayList<>(), loop, random, autoRemove, songs);
    }

    public RadioPlayer(JavaPlugin javaPlugin, List<Player> players, boolean loop, boolean random, boolean autoRemove, Song... songs){
        this.javaPlugin = javaPlugin;
        this.songs = Arrays.asList(songs);
        this.players = new ArrayList<>();
        this.loop = loop;
        this.random = random;
        this.autoRemove = autoRemove;
        players.forEach(player -> this.players.add(player.getName()));

        radiosPlayers.add(this);
    }

    public RadioPlayer(JavaPlugin javaPlugin, List<Player> players, Song... songs){
        this(javaPlugin, players, false, false, true, songs);
    }

    public RadioPlayer(JavaPlugin javaPlugin, Song... songs){
        this(javaPlugin, new ArrayList<>(), songs);
    }

    public void playRadio(){
        if(isPlaying())
            throw new IllegalStateException("The radio is already on");

        if(songPlayerScheduler == null){
            songIterator = 0;
            playListSongs = songs;
            if(isRandom()) Collections.shuffle(playListSongs, new SecureRandom());
            songPlayerScheduler = new SongPlayerScheduler(playListSongs.get(songIterator));
        }
        songPlayerScheduler.runTaskTimerAsynchronously(javaPlugin, 0L, playListSongs.get(songIterator).getDelay());

        playing = true;
    }

    public void stopRadio(){
        if(songPlayerScheduler == null)
            throw new IllegalStateException("The radio is not on");

        if(!isPlaying())
            throw new IllegalStateException("The radio is already turned off");

        songPlayerScheduler.stop();
    }

    public void pauseRadio(){
        if(songPlayerScheduler == null)
            throw new IllegalStateException("The radio is not on");

        songPlayerScheduler.pause();
    }

    public boolean isAutoRemove(){
        return autoRemove;
    }

    public void setAutoRemove(boolean autoRemove){
        this.autoRemove = autoRemove;
    }

    public boolean isPlaying(){
        return playing;
    }

    public boolean isLoop(){
        return loop;
    }

    public void setLoop(boolean loop){
        this.loop = loop;
    }

    public boolean isRandom(){
        return random;
    }

    public void setRandom(boolean random){
        this.random = random;
    }

    public void addPlayer(Player player){
        if(playerListenRadio(player))
            throw new IllegalStateException("The player is already listening to a radio");

        players.add(player.getName());
    }

    public void removePlayer(Player player){
        players.remove(player.getName());
        if(players.isEmpty() && isAutoRemove()){
            songPlayerScheduler.stop();
            radiosPlayers.remove(this);
        }
    }

    public boolean containPlayer(Player player){
        return players.contains(player.getName());
    }

    public void addSong(Song song){
        songs.add(song);
        if(isPlaying()) playListSongs.add(song);
    }

    public void removeSong(Song song){
        songs.remove(song);
    }

    public static boolean playerListenRadio(Player player){
        return radiosPlayers.stream().parallel().anyMatch(radioPlayer -> radioPlayer.containPlayer(player));
    }

    public static RadioPlayer getRadioPlayer(Player player){
        return radiosPlayers.stream().parallel().filter(radioPlayer -> radioPlayer.containPlayer(player)).findFirst().orElseThrow(() -> new NullPointerException("The player "+player.getName()+" doesn't listen to the radio"));
    }

    void playNoteAtTick(Song song, int tick){
        song.getLayerHashMap().values().forEach(layer -> {
            final Note note = layer.getNote(tick);
            if(note != null){
                final float volume = layer.getVolume();
                final Sound sound = InstrumentNBS.getInstrumentSoundByID(note.getInstrument());
                final float pitch = getPitchInOctave(note.getKey(), note.getPitch());

                players.stream().parallel().forEach(playerName -> {
                    final Player player = Bukkit.getPlayerExact(playerName);
                    if(player != null && player.isOnline()){
                        player.playSound(player.getLocation(), sound, volume, pitch);
                    }
                });

            }
        });
    }

    private float getPitchInOctave(byte key, short pitch){
        float[] pitches = new float[2401];

        for(int i = 0; i < 2401; ++i){
            pitches[i] = (float) Math.pow(2.0D, (i-1200.0D)/1200.0D);
        }

        key = (byte) (key+pitch/100);
        pitch = (short) (pitch%100);
        if(key < 9){
            key = (byte) (key+15);
        }else if(key < 33){
            key = (byte) (key-9);
        }else if(key < 57){
            key = (byte) (key-33);
        }else if(key < 81){
            key = (byte) (key-57);
        }else if(key < 105){
            key = (byte) (key-81);
        }

        return pitches[key*100+pitch];
    }

    private class SongPlayerScheduler extends BukkitRunnable{

        private final Song songPlaying;
        private int tick;

        public SongPlayerScheduler(Song song, int tick){
            songPlaying = song;
            this.tick = tick;
        }

        public SongPlayerScheduler(Song song){
            this(song, -1);
        }

        @Override
        public void run(){

            //Fin du sons
            if(tick > songPlaying.getLength()){
                //Si c'est la fin de la playlist
                if(songIterator == playListSongs.size()-1){
                    //Si on continue a jouer
                    if(loop){
                        //Si on randomise les sons jouer
                        if(random){
                            Collections.shuffle(playListSongs, new SecureRandom());
                        }
                        songIterator = 0;
                        final Song nextSong = playListSongs.get(songIterator);
                        songPlayerScheduler = new SongPlayerScheduler(nextSong);
                        songPlayerScheduler.runTaskTimerAsynchronously(javaPlugin, 0L, nextSong.getDelay());
                    }else{
                        //On arrête si on ne continue pas à jouer
                        songPlayerScheduler = null;
                        playing = false;
                    }
                }else{
                    //On passe au sons suivant si ce n'est pas la fin de la playlist
                    final Song nextSong = playListSongs.get(++songIterator);
                    songPlayerScheduler = new SongPlayerScheduler(nextSong);
                    songPlayerScheduler.runTaskTimerAsynchronously(javaPlugin, 0L, nextSong.getDelay());
                }
                cancel();
            }else{
                tick++;
                Bukkit.getScheduler().runTask(javaPlugin, () -> playNoteAtTick(songPlaying, tick));
            }
        }

        public synchronized void stop() throws IllegalStateException{
            if(!isCancelled()){
                cancel();
            }
            playing = false;
            songPlayerScheduler = null;
        }

        public synchronized void pause() throws IllegalStateException{
            cancel();
            playing = false;
            songPlayerScheduler = new SongPlayerScheduler(songPlaying, tick);
        }
    }

}