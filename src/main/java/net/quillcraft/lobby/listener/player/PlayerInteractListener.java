package net.quillcraft.lobby.listener.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import net.quillcraft.core.event.action.ActualAction;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.inventory.MenuInventory;
import net.quillcraft.lobby.inventory.VisibilityInventory;
import net.quillcraft.lobby.manager.ConfigurationManager;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        final Player player = event.getPlayer();
        final Action action = event.getAction();


        if(event.getHand() == EquipmentSlot.OFF_HAND) {return;}

        if(action == Action.RIGHT_CLICK_BLOCK){
            final Block clickedBlock = event.getClickedBlock();
            if(clickedBlock.getType() == Material.PLAYER_HEAD) {
                final FileConfiguration headConfiguration = ConfigurationManager.HEAD.getConfiguration();
                ConfigurationSection configurationSection =  headConfiguration.getConfigurationSection(player.getLocation().getWorld().getName());
                for (int i = 0; i < configurationSection.getKeys(false).size(); i++) {
                    if(clickedBlock.getX() == configurationSection.getInt( i +".x") && (clickedBlock.getZ() == configurationSection.getInt( i +".z") && clickedBlock.getY() == configurationSection.getInt( i +".y"))){
                        try { // Tentative de connection
                            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
                            final String uuid = player.getUniqueId().toString();    //Recupere l'uuid du joueur
                            final PreparedStatement preparedStatementCheck = connection.prepareStatement("SELECT headlist WHERE uuid = ?");// Précontruction d'une requète SQL
                            preparedStatementCheck.setObject(1,uuid); // Finilisation de la requête
                            preparedStatementCheck.executeQuery(); // Excute et récupere des données
                            final ResultSet resultSet = preparedStatementCheck.getResultSet(); // Récupere les données de la commande
                            if(resultSet.next()){ // Si il y a des données (headlist != null)
                                final Gson gson = new GsonBuilder().serializeNulls().create();
                                String headlist = resultSet.getString("headlist");
                                List<Integer> list = new ArrayList<>();
                                if(headlist != null){
                                    list = gson.fromJson(headlist, List.class); // Deserialise string to list
                                }
                                if(!list.contains(i)){
                                    list.add(i);
                                    final PreparedStatement preparedStatement =  connection.prepareStatement("UPDATE headfinder SET headlist = ? WHERE uuid = ?"); // Précontruction d'une requète SQL
                                    preparedStatement.setObject(1, gson.toJson(list)); // Finilisation de la requête / Serialise String to list
                                    preparedStatement.setObject(2, uuid); // Finilisation de la requête
                                    preparedStatement.execute();    //Execution de la requete
                                }else{
                                    player.sendMessage("Tête déja trouvé");
                                }

                            }else{
                            final PreparedStatement preparedStatement =  connection.prepareStatement("INSERT INTO headfinder (uuid, headlist) VALUES (?, ?)"); // Précontruction d'une requète SQL
                            preparedStatement.setObject(1, uuid); // Finilisation de la requête
                            preparedStatement.setObject(2, i);  // Finilisation de la requête
                            preparedStatement.execute();    //Execution de la requete
                            }
                            connection.close(); //Fermeture de la connection
                        } catch (SQLException e) {
                            e.printStackTrace();

                        }

                    }
                }
            }return;
        }

        if(ActualAction.hasRight(action)){
            final ItemStack item = event.getItem();

            if(item == null) return;
            //Check action with item into inventory
            switch(item.getType()){
                //Menu
                case FEATHER -> {
                    player.openInventory(new MenuInventory().getMenuInventory(LanguageManager.getLanguage(player)));
                    event.setCancelled(true);
                    return;
                }
                //Boutique      Informations   Amis       Paramètres
                case GOLD_INGOT, PLAYER_HEAD, PUFFERFISH, COMPARATOR -> {
                    player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.WORKING_PROGRESS));
                    event.setCancelled(true);
                    return;
                }
                //Particules
                case EXPERIENCE_BOTTLE -> {
                    player.updateInventory();
                    player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.WORKING_PROGRESS));
                    event.setCancelled(true);
                    return;
                }
                //Visibility
                default -> {
                    System.out.println("default");
                    if(new ItemBuilder(item).isItemDye()){
                        try{
                            player.openInventory(new VisibilityInventory().getVisibilityInventory(new AccountProvider(player).getAccount()));
                        }catch(AccountNotFoundException e){
                            e.printStackTrace();
                        }finally{
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        //TODO : Anti Spamm door
        if(action == Action.LEFT_CLICK_BLOCK){
            final Block block = event.getClickedBlock();
            if(block.getBlockData() instanceof Door door && door.isOpen()){

            }
        }
    }
}