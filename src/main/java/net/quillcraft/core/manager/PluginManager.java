package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.command.*;
import net.quillcraft.core.listener.player.PlayerMoveListener;
import org.bukkit.Server;
import org.bukkit.plugin.messaging.Messenger;

public class PluginManager {

    private final QuillCraftCore main;
    public PluginManager(QuillCraftCore main){
        this.main = main;
        final Server server = main.getServer();
        registerEvents(server.getPluginManager());
        registerPluginMessage(server.getMessenger());
        registerCommands();
    }

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager){
        pluginManager.registerEvents(new PlayerMoveListener(main), main);
    }

    private void registerPluginMessage(Messenger messenger){
        messenger.registerOutgoingPluginChannel(main, "BungeeCord");
        messenger.registerOutgoingPluginChannel(main, "quillcraft:party");
        messenger.registerOutgoingPluginChannel(main, "quillcraft:message");
        messenger.registerOutgoingPluginChannel(main, "quillcraft:friend");
    }

    private void registerCommands(){
        final LanguageCommand commandLanguage = new LanguageCommand(main);
        main.getCommand("lang").setExecutor(commandLanguage);
        main.getCommand("lang").setTabCompleter(commandLanguage);

        final PartyCommand partyCommand = new PartyCommand(main);
        main.getCommand("party").setExecutor(partyCommand);
        main.getCommand("party").setTabCompleter(partyCommand);

        final FriendCommand friendCommand = new FriendCommand(main);
        main.getCommand("friend").setExecutor(friendCommand);
        main.getCommand("friend").setTabCompleter(friendCommand);

        main.getCommand("msg").setExecutor(new MessageCommand(main));
        main.getCommand("r").setExecutor(new ReponseMessageCommand(main));

        main.getCommand("lobby").setExecutor(new LobbyCommand(main));
    }

}
