package net.quillcraft.bungee.utils.builder;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.quillcraft.bungee.QuillCraftBungee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class YamlConfigurationBuilder{

    private final File file;
    private String parentPath = "";
    private final ClassLoader classLoader;
    private File dataFolder;
    private Configuration config;

    public YamlConfigurationBuilder(final String fileName, final boolean isDefault){
        this(fileName, null, isDefault);
    }

    public YamlConfigurationBuilder(final String fileName, final String parentPath, final boolean isDefault){
        QuillCraftBungee quillCraftBungee = QuillCraftBungee.getInstance();
        classLoader = quillCraftBungee.getClass().getClassLoader();
        dataFolder = quillCraftBungee.getDataFolder();

        if(parentPath != null) this.parentPath = parentPath;
        if(!this.parentPath.isEmpty()) dataFolder =  new File(dataFolder, parentPath);

        if(!dataFolder.exists()) dataFolder.mkdir();

        file = new File(dataFolder, fileName);

        if(!file.exists()){
            if(isDefault){
                saveDefaultConfig();
                return;
            }
            try{
                file.createNewFile();
            }catch(IOException exception){
                exception.printStackTrace();
            }
        }
    }

    private YamlConfigurationBuilder saveDefaultConfig(){
        if(!file.exists()){
            try{
                Files.copy(Objects.requireNonNull(classLoader.getResourceAsStream(parentPath.isEmpty() ? file.getName() : parentPath+"/"+file.getName())), file.toPath());
            }catch(IOException exception){
                exception.printStackTrace();
            }
            return this;
        }
        return this;
    }

    public Configuration getConfig(){
        try{
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(dataFolder, file.getName()));
            return config;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(){
        if(config == null) return;
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(dataFolder, file.getName()));
            config = null;
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}