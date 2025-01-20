package net.lyflow.songapi.song.utils;

import net.lyflow.songapi.song.Song;
import net.lyflow.songapi.song.data.Layer;
import net.lyflow.songapi.song.data.Note;

import java.io.*;
import java.util.HashMap;

public class SongParser {

    private final DataInputStream dataInputStream;
    private final String fileName;

    public SongParser(File file) throws FileNotFoundException {
        this.fileName = file.getName();
        dataInputStream = new DataInputStream(new FileInputStream(file));
    }

    public Song getSong() throws Exception {

        /**
         * @Part-1
         *
         * @Header
         * Information of Song
         *
         * The header contains information about the file
         * @Source : https://opennbs.org/nbs
         **/

        readShort();
        final byte version = dataInputStream.readByte();
        if (version < 5) throw new IllegalStateException("""
                The NBS version is old for %s
                The NBS version of the file is %a
                """.replace("%s", fileName).replace("%a", Integer.toString(version)));

        dataInputStream.readByte();
        final short songLenght = readShort();
        final short layerCount = (short) (readShort() - 1);
        final String songName = readString();
        final String songAuthor = readString();
        readString();
        final String songDescription = readString();
        final long tempo = readShort() / 100L;

        dataInputStream.readBoolean();
        dataInputStream.readByte();
        dataInputStream.readByte();
        readInt();
        readInt();
        readInt();
        readInt();
        readInt();
        readString();

        dataInputStream.readBoolean();
        dataInputStream.readByte();
        readShort();


        /**
         * @Part-2
         *
         * @Note_Block
         * Music Data
         *
         * The next part contains the information about how the note blocks are placed, what instruments they have and what note.
         * As you may know, the song is divided into ticks (horizontally) and layers (vertically).
         * Often, a majority of the ticks and layers in the song are empty, which is why we specify
         * the amount of "jumps" to the next active tick or layer, rather than just a bunch of empty slots.
         * @Source : https://opennbs.org/nbs
         */

        final HashMap<Integer, Layer> layersHashMap = new HashMap<>();

        short tick = -1;
        label:
        while (true) {
            final short jumpTick = readShort();
            if (jumpTick == 0) break;
            tick += jumpTick;
            short layer = -1;
            while (true) {
                final short jumpLayer = readShort();
                if (jumpLayer == 0) continue label;
                layer += jumpLayer;
                final byte instrument = dataInputStream.readByte();
                final byte key = dataInputStream.readByte();
                final byte velocity = dataInputStream.readByte();
                dataInputStream.readByte();
                final short pitch = readShort();
                setNote(layer, tick, new Note(instrument, key, velocity, pitch), layersHashMap);
            }
        }


        /**
         * @Part-3
         *
         * @Layers
         * Layers data
         *
         * Here the information about the layers are stored, which includes their name, volume and stereo.
         * These values are repeated the same number of layers in the song.
         * @Source : https://opennbs.org/nbs
         */

        for (int i = 0; i < layerCount; i++) {
            final Layer layer = layersHashMap.get(i);
            final String name = readString();

            dataInputStream.readByte();
            final byte volume = dataInputStream.readByte();
            dataInputStream.readByte();

            layer.setName(name);
            layer.setVolume(volume);
        }

        dataInputStream.close();

        return new Song(layersHashMap, layerCount, songLenght, songName, songDescription, songAuthor, tempo);
    }

    private String readString() throws IOException {
        int length = readInt();
        final StringBuilder builder = new StringBuilder(length);
        for (; length > 0; length--) {
            char c = (char) dataInputStream.readByte();
            if (c == '\r') c = ' ';
            builder.append(c);
        }
        return builder.toString();
    }

    private int readInt() throws IOException {
        final int byte1 = dataInputStream.readUnsignedByte();
        final int byte2 = dataInputStream.readUnsignedByte();
        final int byte3 = dataInputStream.readUnsignedByte();
        final int byte4 = dataInputStream.readUnsignedByte();
        return byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24);
    }

    private short readShort() throws IOException {
        final int byte1 = dataInputStream.readUnsignedByte();
        final int byte2 = dataInputStream.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private void setNote(int layerIndex, int ticks, Note note, HashMap<Integer, Layer> layerHashMap) {
        Layer layer = layerHashMap.get(layerIndex);
        if (layer == null) {
            layer = new Layer();
            layerHashMap.put(layerIndex, layer);
        }
        layer.setNote(ticks, note);
    }

}