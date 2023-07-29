package net.quillcraft.commons.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.lyflow.sqlrequest.SQLRequest;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.friend.FriendProvider;
import net.quillcraft.commons.party.PartyProvider;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.sql.table.SQLTablesManager;
import net.quillcraft.core.serialization.ProfileSerializationAccount;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Account {

    private int id;
    private UUID uuid;
    private UUID partyUUID;
    private int quillCoins;
    private byte rankID;
    private Visibility visibility;
    private HashMap<Particles, Boolean> particles;
    private String languageISO;

    @JsonIgnore
    private SQLRequest sqlRequest;

    //Redis
    private Account() {}

    public Account(final Player player) {
        this(0, player.getUniqueId(), null, 10, (byte) 0, Visibility.EVERYONE, defaultParticles(), "en_us");
    }

    public Account(final int id, final UUID uuid, final int quillCoins, final byte rankID, final Visibility visibility, final HashMap<Particles, Boolean> particles, final String languageISO) {
        this(id, uuid, null, quillCoins, rankID, visibility, particles, languageISO);
    }

    public Account(final int id, final UUID uuid, final UUID partyUUID, final int quillCoins, final byte rankID, final Visibility visibility, final HashMap<Particles, Boolean> particles, final String languageISO) {
        this.id = id;
        this.uuid = uuid;
        this.partyUUID = partyUUID;
        this.quillCoins = quillCoins;
        this.rankID = rankID;
        this.visibility = visibility;
        this.particles = particles;
        this.languageISO = languageISO;

        setSQLRequest();
    }

    private static HashMap<Particles, Boolean> defaultParticles() {
        final HashMap<Particles, Boolean> defaultParticles = new HashMap<>();
        Arrays.stream(Particles.values()).forEach(particles -> defaultParticles.put(particles, false));
        return defaultParticles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        //sqlRequest.addData("id", getId());
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getPartyUUID() {
        return partyUUID;
    }

    public void setPartyUUID(final UUID partyUUID) {
        this.partyUUID = partyUUID;
        getSQLRequest().addData("partyuuid", getPartyUUID());
    }

    public int getQuillCoins() {
        return quillCoins;
    }

    public void setQuillCoins(final int quillCoins) {
        this.quillCoins = quillCoins;
        getSQLRequest().addData("quillcoins", getQuillCoins());
    }

    public byte getRankID() {
        return rankID;
    }

    public void setRankID(final byte rankID) {
        this.rankID = rankID;
        getSQLRequest().addData("rank_id", getRankID());
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
        getSQLRequest().addData("visibility", getVisibility().name());
    }

    public HashMap<Particles, Boolean> getParticles() {
        return particles;
    }

    public void setParticles(HashMap<Particles, Boolean> particles) {
        this.particles = particles;
        getSQLRequest().addData("json_particles", new ProfileSerializationAccount.Particle().serialize(getParticles()));
    }

    public boolean hasParty() {
        return getPartyUUID() != null;
    }

    public String getLanguageISO() {
        return languageISO;
    }

    public void setLanguage(final String languageISO) {
        this.languageISO = languageISO;
        sqlRequest.addData("language", languageISO);
    }

    public SQLRequest getSQLRequest() {
        return sqlRequest;
    }

    public void playVisibilityEffect() {
        final QuillCraftCore quillCraftCore = QuillCraftCore.getInstance();
        final Server server = quillCraftCore.getServer();
        final Player player = server.getPlayer(uuid);

        switch(getVisibility()) {
            case NOBODY -> server.getOnlinePlayers().forEach(players -> player.hidePlayer(quillCraftCore, players));
            case PARTY -> {
                try {
                    final List<UUID> playersUUID = new PartyProvider(new AccountProvider(player).getAccount()).getParty().getPlayersUUID();
                    playersUUID.remove(player);
                    if(playersUUID.isEmpty()) {
                        server.getOnlinePlayers().forEach(players -> player.hidePlayer(quillCraftCore, players));
                        break;
                    }
                    server.getOnlinePlayers().forEach(players -> {
                        if(playersUUID.contains(players.getUniqueId())) {
                            player.showPlayer(quillCraftCore, players);
                        } else {
                            player.hidePlayer(quillCraftCore, players);
                        }
                    });
                } catch(AccountNotFoundException|PartyNotFoundException exception) {
                    quillCraftCore.getLogger().severe(exception.getMessage());
                }
            }
            case FRIENDS -> {
                try {
                    final List<UUID> friendListUUID = new FriendProvider(player).getFriends().getFriendsUUID();
                    if(friendListUUID.isEmpty()) {
                        server.getOnlinePlayers().forEach(players -> player.hidePlayer(quillCraftCore, players));
                        break;
                    }
                    server.getOnlinePlayers().forEach(players -> {
                        if(friendListUUID.contains(players.getUniqueId())) {
                            player.showPlayer(quillCraftCore, players);
                        } else {
                            player.hidePlayer(quillCraftCore, players);
                        }
                    });
                } catch(FriendNotFoundException exception) {
                    server.getLogger().severe(exception.getMessage());
                }
            }
            case EVERYONE -> server.getOnlinePlayers().forEach(players -> player.showPlayer(quillCraftCore, players));
        }
    }

    protected void setSQLRequest() {
        final SQLTablesManager sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), uuid.toString());
    }

    public enum Visibility {
        EVERYONE(1, Material.LIME_DYE), PARTY(3, Material.ORANGE_DYE), FRIENDS(5, Material.CYAN_DYE), NOBODY(7, Material.GRAY_DYE);

        private final int slot;
        private final Material material;

        Visibility(final int slot, final Material material) {
            this.slot = slot;
            this.material = material;
        }

        public static Visibility getVisibilityByData(Material material) {
            return Arrays.stream(values()).filter(visibility -> visibility.getMaterial() == material).findFirst().get();
        }

        public int getSlot() {
            return slot;
        }

        public Material getMaterial() {
            return material;
        }
    }

    public enum Particles {
        FIRE(10), WATER(102), THUNDER(1), SPEED(20), LIGHT(32), EXPLODE(57);

        private final int price;

        Particles(final int price) {
            this.price = price;
        }

        public int getPrice() {
            return price;
        }
    }
}
