package net.lyflow.songapi.song.data;

import java.util.HashMap;

public class Layer {

    private HashMap<Integer, Note> noteAtTick;

    private byte volume;
    private String name;
    public Layer(){
        this.volume = 100;
        this.noteAtTick = new HashMap<>();
        this.name = "";
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public HashMap<Integer, Note> getNoteAtTick(){
        return noteAtTick;
    }

    public void setNoteAtTick(HashMap<Integer, Note> noteAtTick){
        this.noteAtTick = noteAtTick;
    }

    public byte getVolume(){
        return volume;
    }

    public void setVolume(byte volume){
        this.volume = volume;
    }

    public Note getNote(int tick){
        return noteAtTick.get(tick);
    }

    public void setNote(int tick, Note note) {
        noteAtTick.put(tick, note);
    }

}
