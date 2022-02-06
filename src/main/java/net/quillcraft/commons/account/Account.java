package net.quillcraft.commons.account;

import net.lyflow.sqlrequest.SQLRequest;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.friend.FriendProvider;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.sql.table.SQLTablesManager;
import net.quillcraft.core.serialization.ProfileSerializationAccount;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Account(){}

    public Account(final Player player){
        this(0, player.getUniqueId(), null, 10, (byte) 0, Visibility.EVERYONE, defaultParticles(), "en_us");
    }

    public Account(final int id, final UUID uuid, final int quillCoins, final byte rankID, final Visibility visibility,
                   final HashMap<Particles, Boolean> particles, final String languageISO){
        this(id, uuid, null, quillCoins, rankID, visibility, particles, languageISO);
    }

    public Account(final int id, final UUID uuid, final UUID partyUUID, final int quillCoins, final byte rankID, final Visibility visibility,
                   final HashMap<Particles, Boolean> particles, final String languageISO){
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

    public int getId(){
        return id;
    }

    public UUID getUUID(){
        return uuid;
    }

    public UUID getPartyUUID(){
        return partyUUID;
    }

    public int getQuillCoins(){
        return quillCoins;
    }

    public byte getRankID(){
        return rankID;
    }

    public Visibility getVisibility(){
        return visibility;
    }

    public HashMap<Particles, Boolean> getParticles(){
        return particles;
    }

    public boolean hasParty(){
        return getPartyUUID() != null;
    }

    public void setPartyUUID(final UUID partyUUID){
        this.partyUUID = partyUUID;
        getSQLRequest().addData("partyuuid", getPartyUUID());
    }

    public void setQuillCoins(final int quillCoins){
        this.quillCoins = quillCoins;
        getSQLRequest().addData("quillcoins", getQuillCoins());
    }

    public void setRankID(final byte rankID){
        this.rankID = rankID;
        getSQLRequest().addData("rank_id", getRankID());
    }

    public void setVisibility(final Visibility visibility){
        this.visibility = visibility;
        getSQLRequest().addData("visibility", getVisibility().name());
    }

    public void setParticles(HashMap<Particles, Boolean> particles){
        this.particles = particles;
        getSQLRequest().addData("json_particles", new ProfileSerializationAccount.Particle().serialize(getParticles()));
    }

    public void setId(int id){
        this.id = id;
        //sqlRequest.addData("id", getId());
    }

    public String getLanguageISO(){
        return languageISO;
    }

    public void setLanguage(final String languageISO){
        this.languageISO = languageISO;
        sqlRequest.addData("language", languageISO);
    }

    public SQLRequest getSQLRequest(){
        return sqlRequest;
    }

    public void playVisibilityEffect(){
        final Player player = Bukkit.getPlayer(uuid);
        final QuillCraftCore quillCraftCore = QuillCraftCore.getInstance();

        switch(getVisibility()){
            case NOBODY -> Bukkit.getOnlinePlayers().forEach(players -> player.hidePlayer(quillCraftCore, players));
            case EVERYONE -> Bukkit.getOnlinePlayers().forEach(players -> player.showPlayer(quillCraftCore, players));
            case FRIENDS -> {
                try{
                    final List<UUID> friendListUUID = new FriendProvider(player).getFriends().getFriendsUUID();
                    if(friendListUUID.size() == 0) {
                        Bukkit.getOnlinePlayers().forEach(players -> player.hidePlayer(quillCraftCore, players));
                        break;
                    }
                    Bukkit.getOnlinePlayers().forEach(players -> {
                        if(friendListUUID.contains(players.getUniqueId())){
                            player.showPlayer(quillCraftCore, players);
                        }else{
                            player.hidePlayer(quillCraftCore, players);
                        }
                    });
                }catch(FriendNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

    protected void setSQLRequest(){
        final SQLTablesManager sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), uuid.toString());
    }

    private static HashMap<Particles, Boolean> defaultParticles(){
        final HashMap<Particles, Boolean> defaultParticles = new HashMap<>();
        Arrays.stream(Particles.values()).forEach(particles -> defaultParticles.put(particles, false));
        return defaultParticles;
    }

    public enum Visibility {
        EVERYONE(2, Material.LIME_DYE), FRIENDS(4, Material.CYAN_DYE), NOBODY(6, Material.GRAY_DYE);

        private final int slot;
        private final Material material;

        Visibility(final int slot, final Material material){
            this.slot = slot;
            this.material = material;
        }

        public int getSlot(){
            return slot;
        }

        public Material getMaterial(){
            return material;
        }

        public static Visibility getVisibilityByData(Material material){
            return Arrays.stream(values()).filter(visibility -> visibility.getMaterial() == material).findFirst().get();
        }
    }

    public enum Particles {
        FIRE(10), WATER(102), THUNDER(1), SPEED(20), LIGHT(32), EXPLODE(57);

        private final int price;

        Particles(final int price){
            this.price = price;
        }

        public int getPrice(){
            return price;
        }
    }
}
