package net.lyflow.songapi.song.data;

public class Note {

    private byte instrument;
    private byte key;
    private byte velocity;
    private short pitch;

    public Note(byte instrument, byte key, byte velocity, short pitch) {
        this.instrument = instrument;
        this.key = key;
        this.velocity = velocity;
        this.pitch = pitch;
    }

    public byte getInstrument() {
        return instrument;
    }

    public void setInstrument(byte instrument) {
        this.instrument = instrument;
    }

    public byte getKey() {
        return key;
    }

    public void setKey(byte key) {
        this.key = key;
    }

    public byte getVelocity() {
        return velocity;
    }

    public void setVelocity(byte velocity) {
        this.velocity = velocity;
    }

    public short getPitch() {
        return pitch;
    }

    public void setPitch(short pitch) {
        this.pitch = pitch;
    }

}