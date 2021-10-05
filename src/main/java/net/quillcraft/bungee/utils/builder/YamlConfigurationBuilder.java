package net.quillcraft.bungee.utils.builder;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.quillcraft.bungee.QuillCraftBungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class YamlConfigurationBuilder{

    private final File file;
    private String parentPath = "";
    private final QuillCraftBungee main = QuillCraftBungee.getInstance();
    private File dataFolder = main.getDataFolder();
    private Configuration config;

    public YamlConfigurationBuilder(final String fileName, final boolean isDefault){
        this(fileName, null, isDefault);
    }

    public YamlConfigurationBuilder(final String fileName, final String parentPath, final boolean isDefault){
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

                if(parentPath.isEmpty()){
                    System.out.println("is empty");
                }else{
                    System.out.println("is not empty");
                }

                System.out.println("value = "+(parentPath.isEmpty() ? file.getName() : parentPath+"/"+file.getName()));

                InputStream in = main.getResourceAsStream(parentPath.isEmpty() ? file.getName() : parentPath+"/"+file.getName());

                Files.copy(in, file.toPath());
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