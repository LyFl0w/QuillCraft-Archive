package net.lyflow.songapi.song.data;

import org.bukkit.Sound;

public enum InstrumentNBS {

    PIANO(0, Sound.BLOCK_NOTE_BLOCK_HARP),
    DOUBLE_BASS(1, Sound.BLOCK_NOTE_BLOCK_BASS),
    BASS_DRUM(2, Sound.BLOCK_NOTE_BLOCK_BASEDRUM),
    SNARE_DRUM(3, Sound.BLOCK_NOTE_BLOCK_SNARE),
    CLICK(4, Sound.BLOCK_NOTE_BLOCK_HAT),
    GUITAR(5, Sound.BLOCK_NOTE_BLOCK_GUITAR),
    FLUTE(6, Sound.BLOCK_NOTE_BLOCK_FLUTE),
    BELL(7, Sound.BLOCK_NOTE_BLOCK_BELL),
    CHIME(8, Sound.BLOCK_NOTE_BLOCK_CHIME),
    XYLOPHONE(9, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE),
    IRON_XYLOPHONE(10, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE),
    COW_BELL(11, Sound.BLOCK_NOTE_BLOCK_COW_BELL),
    DIDGERIDOO(12, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO),
    BIT(13, Sound.BLOCK_NOTE_BLOCK_BIT),
    BANJO(14, Sound.BLOCK_NOTE_BLOCK_BANJO),
    PLING(15, Sound.BLOCK_NOTE_BLOCK_PLING);

    private final int id;
    private final Sound sound;

    InstrumentNBS(int id, Sound sound) {
        this.id = id;
        this.sound = sound;
    }

    public static Sound getInstrumentSoundByID(int id) {
        for (InstrumentNBS instrumentNBS : values()) {
            if (instrumentNBS.id == id) return instrumentNBS.sound;
        }
        return PIANO.sound;
    }

}
