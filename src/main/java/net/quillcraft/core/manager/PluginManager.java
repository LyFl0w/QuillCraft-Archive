package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.command.*;
import net.quillcraft.core.listener.player.PlayerCommandPreprocessListener;
import net.quillcraft.core.listener.player.PlayerCommandSendListener;
import net.quillcraft.core.listener.player.PlayerMoveListener;
import org.bukkit.Server;
import org.bukkit.plugin.messaging.Messenger;

import java.util.List;

public class PluginManager {

    private final QuillCraftCore quillCraftCore;
    public PluginManager(QuillCraftCore quillCraftCore){
        this.quillCraftCore = quillCraftCore;
        final Server server = quillCraftCore.getServer();
        registerEvents(server.getPluginManager());
        registerPluginMessage(server.getMessenger());
        registerCommands();
    }

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager){
        pluginManager.registerEvents(new PlayerMoveListener(quillCraftCore), quillCraftCore);

        // Commands
        final List<String> commands = quillCraftCore.getCommandManager().getCommands();
        pluginManager.registerEvents(new PlayerCommandPreprocessListener(commands), quillCraftCore);
        pluginManager.registerEvents(new PlayerCommandSendListener(commands), quillCraftCore);
    }

    private void registerPluginMessage(Messenger messenger){
        messenger.registerOutgoingPluginChannel(quillCraftCore, "BungeeCord");
        messenger.registerOutgoingPluginChannel(quillCraftCore, "quillcraft:party");
        messenger.registerOutgoingPluginChannel(quillCraftCore, "quillcraft:message");
        messenger.registerOutgoingPluginChannel(quillCraftCore, "quillcraft:friend");
    }

    private void registerCommands(){
        final LanguageCommand commandLanguage = new LanguageCommand(quillCraftCore);
        quillCraftCore.getCommand("lang").setExecutor(commandLanguage);
        quillCraftCore.getCommand("lang").setTabCompleter(commandLanguage);

        final PartyCommand partyCommand = new PartyCommand(quillCraftCore);
        quillCraftCore.getCommand("party").setExecutor(partyCommand);
        quillCraftCore.getCommand("party").setTabCompleter(partyCommand);

        final FriendCommand friendCommand = new FriendCommand(quillCraftCore);
        quillCraftCore.getCommand("friend").setExecutor(friendCommand);
        quillCraftCore.getCommand("friend").setTabCompleter(friendCommand);

        quillCraftCore.getCommand("msg").setExecutor(new MessageCommand(quillCraftCore));
        quillCraftCore.getCommand("r").setExecutor(new ReponseMessageCommand(quillCraftCore));

        quillCraftCore.getCommand("lobby").setExecutor(new LobbyCommand(quillCraftCore));
    }

}
