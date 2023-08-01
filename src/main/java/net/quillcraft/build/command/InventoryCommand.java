package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;
import net.quillcraft.core.utils.CommandUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.List;


public class InventoryCommand implements CommandExecutor, TabCompleter {

    private final static String[] COMPLETIONS = new String[]{"get", "set", "remove"};

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(args.length == 2 && commandSender instanceof final Player player) {
            final String action = args[0];
            final String inventoryName = args[1];
            if(action.equalsIgnoreCase("get")) {
                final ConfigurationSection configurationSection = ConfigurationManager.INVENTORY.getConfiguration().getConfigurationSection(commandSender.getName());
                if(configurationSection == null || !configurationSection.contains(inventoryName)) {
                    player.sendMessage("§cL'inventaire "+inventoryName+" n'existe pas.\n§bPour le créer faite un /inventory set "+inventoryName);
                    return true;
                }

                final ConfigurationSection configurationSectionInventory = configurationSection.getConfigurationSection(inventoryName);
                final PlayerInventory playerInventory = player.getInventory();

                playerInventory.clear();
                for(String key : configurationSectionInventory.getKeys(false)) {
                    playerInventory.setItem(Integer.parseInt(key), configurationSectionInventory.getItemStack(key));
                }

                player.sendMessage("§2L'inventaire "+inventoryName+" a bien été chargé");
                return true;
            }

            if(action.equalsIgnoreCase("set")) {
                final ConfigurationManager configurationManager = ConfigurationManager.INVENTORY;
                final FileConfiguration fileConfiguration = configurationManager.getConfiguration();

                final String path = player.getName()+"."+inventoryName;
                final boolean alreadyExist = fileConfiguration.contains(path);

                if(player.getInventory().isEmpty()) {
                    player.sendMessage("§cTon inventaire est vide. Si tu veux supprimer l'inventaire fait /inventory remove "+inventoryName);
                    return true;
                }

                final ItemStack[] contents = player.getInventory().getContents();
                for(int i=0; i<contents.length; i++) fileConfiguration.set(path+"."+i, contents[i]);

                configurationManager.saveFile();

                player.sendMessage("§2"+ ((alreadyExist) ? "L'inventaire "+inventoryName+" a été mis à jour" : "L'inventaire "+inventoryName+" a été créé"));
                return true;
            }

            if(action.equalsIgnoreCase("remove")) {
                final ConfigurationManager configurationManager = ConfigurationManager.INVENTORY;
                final FileConfiguration fileConfiguration = configurationManager.getConfiguration();

                final String path = player.getName()+"."+inventoryName;
                if(!fileConfiguration.contains(path)) {
                    player.sendMessage("§cL'inventaire "+inventoryName+" n'existe pas");
                    return true;
                }

                fileConfiguration.set(path, null);

                if(fileConfiguration.getConfigurationSection(player.getName()).getKeys(false).isEmpty()) fileConfiguration.set(player.getName(), null);

                configurationManager.saveFile();

                player.sendMessage("§2L'inventaire "+inventoryName+" a bien été supprimé");
                return true;
            }

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof Player){
            if(args.length == 1){
                if(args[0] == null) return null;
                return CommandUtils.completionTable(args[0], COMPLETIONS);
            }
            if(args.length == 2){
                if(args[1] == null) return null;
                final ConfigurationSection configurationSection = ConfigurationManager.INVENTORY.getConfiguration().getConfigurationSection(commandSender.getName());
                if(configurationSection == null) return null;

                return CommandUtils.completionTable(args[1], configurationSection.getKeys(false).stream().toList());
            }
        }
        return null;
    }
}
