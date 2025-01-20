package net.lyflow.entitytest.reader;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.lyflow.entitytest.utils.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SchematicReader {

    private final int version, dataVersion;
    private final short width, height, length;

    private final Map<SimpleLocation, BlockData> blocks;

    public SchematicReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public SchematicReader(FileInputStream fileInputStream) throws IOException {
        this(fileInputStream, new Vector());
    }

    public SchematicReader(FileInputStream fileInputStream, Vector vector) throws IOException {
        final NBTTagCompound nbtData = NBTCompressedStreamTools.a(fileInputStream, NBTReadLimiter.a());

        this.version = nbtData.h("Version");
        this.dataVersion = nbtData.h("DataVersion");
        this.width = nbtData.g("Width");
        this.height = nbtData.g("Height");
        this.length = nbtData.g("Length");

        this.blocks = getBlocks(vector, nbtData.m("BlockData"), getPalette(nbtData));

        fileInputStream.close();
    }

    private HashMap<SimpleLocation, BlockData> getBlocks(Vector vector, byte[] data, Map<Integer, String> palette) {

        final HashMap<SimpleLocation, BlockData> blocks = new HashMap<>();

        int index = 0;
        int i = 0;
        int value;
        int varint_length;
        while (i < data.length) {
            value = 0;
            varint_length = 0;
            while (true) {
                value |= (data[i] & 127) << (varint_length++ * 7);
                if (varint_length > 5) {
                    throw new RuntimeException("VarInt too big (probably corrupted data)");
                }
                if ((data[i] & 128) != 128) {
                    i++;
                    break;
                }
                i++;
            }

            final BlockData blockData = Bukkit.createBlockData(palette.get(value));
            if(blockData.getMaterial() == Material.AIR) {
                index++;
                continue;
            }

            // index = (y * length + z) * width + x
            final int y = index / (width * length);
            final int z = (index % (width * length)) / width;
            final int x = (index % (width * length)) % width;

            blocks.put(new SimpleLocation(x, y, z).substractVector(vector), blockData);

            index++;
        }

        return blocks;
    }

    private Map<Integer, String> getPalette(NBTTagCompound nbtTagCompound) {
        final Map<String, Integer> map = new GsonBuilder().create().fromJson(nbtTagCompound.p("Palette").t_(),
                new TypeToken<Map<String, Integer>>(){}.getType());

        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public int getVersion() {
        return version;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public short getLength() {
        return length;
    }

    public Map<SimpleLocation, BlockData> getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return "SchematicReader{"+"version="+version+", dataVersion="+dataVersion+", width="+width+", height="+height+", length="+length+"}";
    }
}
