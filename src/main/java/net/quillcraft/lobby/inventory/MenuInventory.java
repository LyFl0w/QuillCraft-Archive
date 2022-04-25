package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

public class MenuInventory {

    private final static RedissonClient redisClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    public final Inventory getMenuInventory(final Player player){
        return getMenuInventory(LanguageManager.getLanguage(player));
    }

    public final Inventory getMenuInventory(final Account account){
        return getMenuInventory(LanguageManager.getLanguage(account));
    }

    public final Inventory getMenuInventory(final LanguageManager language){
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.INVENTORY_NAME_MENU));
        menuBuilder.setItem(4, new ItemBuilder(Material.IRON_BOOTS, ItemFlag.HIDE_ATTRIBUTES)
                .setName(language.getMessage(Text.ITEMS_INVENTORY_LOBBY_PARKOURPVP_NAME)+" / party : "+getEveryGame())
                .setLore(language.getMessage(TextList.ITEMS_INVENTORY_LOBBY_PARKOURPVP_LORE))
                .toItemStack());

        return menuBuilder.toInventory();
    }

    private int getEveryGame(){
        return Math.toIntExact(redisClient.getKeys().getKeysStreamByPattern("ParkourPvPGame:*").parallel().filter(key -> {
            final RBucket<ParkourPvPGame> gameRBucket = redisClient.getBucket(key);
            Bukkit.getLogger().info(key+" / "+gameRBucket.get().getGameStatus().name());
            return gameRBucket.get().actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING);
        }).count());
    }
}