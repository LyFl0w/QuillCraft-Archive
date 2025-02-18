package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.game.Game;
import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.game.GameItemEnum;
import net.quillcraft.lumy.api.text.Text;
import net.quillcraft.lumy.api.text.TextList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

public class MenuInventory {

    private static final RedissonClient redisClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    public final Inventory getMenuInventory(final Player player) {
        return getMenuInventory(LanguageManager.getLanguage(player));
    }

    public final Inventory getMenuInventory(final Account account) {
        return getMenuInventory(LanguageManager.getLanguage(account));
    }

    public final Inventory getMenuInventory(final LanguageManager language) {
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.INVENTORY_NAME_MENU));
        addItemToInventory(menuBuilder, 4, GameItemEnum.IRON_BOOTS, language);

        return menuBuilder.toInventory();
    }

    private void addItemToInventory(InventoryBuilder inventoryBuilder, int slot, GameItemEnum gameItemEnum, LanguageManager language) {
        inventoryBuilder.setItem(slot, new ItemBuilder(Material.valueOf(gameItemEnum.name()), ItemFlag.HIDE_ATTRIBUTES).setName(language.getMessage(Text.ITEMS_INVENTORY_LOBBY_PARKOURPVP_NAME)+" / en attente : "+getNumberOfWaitingGame(gameItemEnum)).setLore(language.getMessage(TextList.ITEMS_INVENTORY_LOBBY_PARKOURPVP_LORE)).toItemStack());
    }

    private int getNumberOfWaitingGame(GameItemEnum gameItemEnum) {
        return Math.toIntExact(redisClient.getKeys().getKeysStreamByPattern(gameItemEnum.getGameEnum().name()+":*").parallel().filter(key -> {
            final RBucket<? extends Game> gameRBucket = redisClient.getBucket(key);
            return gameRBucket.get().actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING);
        }).count());
    }

    //GameItemToGameEnum.valueOf(item.getType().name()).getGameEnum()
}