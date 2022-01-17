package net.quillcraft.lobby.task;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.lobby.QuillCraftLobby;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;

public class AutoMessageTask {

    /*private int lastChoice;
    private final int size;
    private final QuillCraftLobby quillCraftLobby;
    public AutoMessageTask(QuillCraftLobby quillCraftLobby){
        this.quillCraftLobby = quillCraftLobby;
        this.size = LanguageManager.DEFAULT.getMessage(Text).getFileConfiguration(quillCraftLobby).getConfigurationSection("auto_text.lobby").getKeys(false).size();
        this.lastChoice = -1;
    }

    @Override
    public void run(){
        final HashMap<Player, LanguageManager> languageManagers = new HashMap<>();
        Bukkit.getOnlinePlayers().stream().parallel().forEach(player -> languageManagers.put(player, LanguageManager.getLanguage(player)));
        final HashSet<LanguageManager> languagesToGenerate = new HashSet<>(languageManagers.values());

        final int choice = getChoice(size);
        lastChoice = choice;

        languagesToGenerate.forEach(language -> {
            final ConfigurationSection configurationSection = language.getFileConfiguration(quillCraftLobby).getConfigurationSection("auto_text.lobby");

            final ConfigurationSection configurationSectionText = configurationSection.
                    getConfigurationSection(configurationSection.getKeys(false).toArray()[choice].toString());

            final String text = configurationSectionText.getString("text");
            final String link = configurationSectionText.getString("link");
            final String over = configurationSectionText.getString("over");

            final TextComponent finalText = new TextComponent(text);
            if(link != null && !link.isBlank()) finalText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
            if(over != null && !over.isBlank()) finalText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder(over).create())));

            languageManagers.entrySet().stream().parallel().filter(entry -> entry.getValue() == language).forEach(entry -> entry.getKey().spigot().sendMessage(finalText));
        });
    }

    @Override
    public synchronized void cancel() throws IllegalStateException{
        System.out.println("task cancelled : "+getTaskId());
        super.cancel();
    }

    private int getChoice(int size){
        int choice = new SecureRandom().nextInt(size);
        if(choice == lastChoice){
            choice += 1;
            if(choice >= size){
                choice = 0;
            }
        }
        return choice;
    }*/

}