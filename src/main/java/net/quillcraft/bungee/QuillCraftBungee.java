package net.quillcraft.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.quillcraft.bungee.manager.DataManager;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.manager.PluginManager;

public class QuillCraftBungee extends Plugin {

    private static QuillCraftBungee INSTANCE;

    @Override
    public void onEnable(){
        INSTANCE = this;

        DataManager.initAllData();
        LanguageManager.initAllLanguage();

        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        DataManager.closeAllData();
    }

    public static QuillCraftBungee getInstance(){
        return INSTANCE;
    }
}