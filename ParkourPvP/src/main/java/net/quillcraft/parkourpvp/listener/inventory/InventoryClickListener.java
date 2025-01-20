package net.quillcraft.parkourpvp.listener.inventory;

import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPData;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import net.quillcraft.parkourpvp.game.shop.ShopCategory;
import net.quillcraft.parkourpvp.game.shop.items.ShopItem;
import net.quillcraft.parkourpvp.inventory.shop.ShopCategoriesInventory;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class InventoryClickListener implements Listener{

    private final ParkourPvP parkourPvP;
    private final GameManager gameManager;

    public InventoryClickListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
        this.gameManager = parkourPvP.getGameManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        final Inventory inventory = event.getClickedInventory();
        if(inventory == null) return;

        final ItemStack item = event.getCurrentItem();
        if(item == null || item.getType() == Material.AIR) return;

        final InGameStatus inGameStatus = gameManager.getInGameStatus();

        if(inGameStatus.actualInGameStatusIs(InGameStatus.WAITING_BEFORE_PVP)){
            final Player player = (Player) event.getWhoClicked();
            final String title = event.getView().getTitle();

            if(inventory.equals(player.getInventory())){
                if(item.getType() == Material.NETHER_STAR){
                    event.setCancelled(true);

                    player.updateInventory();
                    player.openInventory(new ShopCategoriesInventory().getInventory(player));
                }
                return;
            }
            if(title.equalsIgnoreCase("Shop")){
                final ShopCategory shopCategory = ShopCategory.getCategory(item.getType());
                player.openInventory(shopCategory.getInventory(player));

                event.setCancelled(true);
                return;
            }
            if(title.contains("Shop")){
                final ShopItem shopItem = ShopItem.getShopItem(item.getType());
                final PlayerParkourPvPData PlayerParkourPvPData = gameManager.getPlayersDataGame().get(player.getName());

                if(shopItem.getPrice() > PlayerParkourPvPData.getCoins()){
                    player.sendMessage("§cVous n'avez pas assez de Coins pour pouvoir acheter cette objet !");
                    event.setCancelled(true);
                    return;
                }
                PlayerParkourPvPData.removeCoins(shopItem.getPrice());
                final ItemBuilder itemBuilder = new ItemBuilder(item.getType(), item.getAmount());
                if(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION) itemBuilder.setPotionData(((PotionMeta) item.getItemMeta()).getBasePotionData());
                player.getInventory().addItem(itemBuilder.toItemStack());
                player.updateInventory();

                new PvPScoreboard(parkourPvP).updateCoins(player.getName());

                event.setCancelled(true);
                return;
            }
            return;
        }

        if(!inGameStatus.actualInGameStatusIs(InGameStatus.PVP)) event.setCancelled(true);

    }
}