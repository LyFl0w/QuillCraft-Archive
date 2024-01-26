package net.lyflow.songapi.song;

import net.lyflow.songapi.song.data.Layer;

import java.util.Map;

public class Song {

    private final Map<Integer, Layer> layerHashMap;
    private final short songHeight;
    private final short length;
    private final String title;
    private final String description;
    private final String author;

    private final long delay;

    public Song(Map<Integer, Layer> layerHashMap, short songHeight, short length, String title, String description,
                String author, float speed){
        this.layerHashMap = layerHashMap;
        this.songHeight = songHeight;
        this.length = length;
        this.title = title;
        this.description = description;
        this.author = author;
        this.delay = Math.round(20/speed);
    }

    public Map<Integer, Layer> getLayerHashMap(){
        return layerHashMap;
    }

    public short getSongHeight(){
        return songHeight;
    }

    public short getLength(){
        return length;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getAuthor(){
        return author;
    }

    public long getDelay(){
        return delay;
    }

}
