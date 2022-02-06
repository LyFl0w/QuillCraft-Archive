package net.quillcraft.commons.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.lyflow.sqlrequest.SQLRequest;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import net.quillcraft.bungee.data.management.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationAccount;
import net.quillcraft.commons.friend.Friend;
import net.quillcraft.commons.friend.FriendProvider;

import java.util.*;

public class Account {

    private int id;
    private UUID uuid;
    private UUID partyUUID;
    private int quillCoin;
    private byte rankID;
    private Visibility visibility;
    private HashMap<Particles, Boolean> particles;
    private String languageISO;

    @JsonIgnore
    private SQLRequest sqlRequest;

    //Redis
    private Account(){}

    public Account(ProxiedPlayer player){
        this(player.getUniqueId());
    }

    public Account(UUID uuid){
        this(0, uuid, null, 10, (byte) 0, Visibility.EVERYONE, defaultParticles(), "en_us");
    }

    public Account(int id, UUID uuid, UUID partyUUID, int quillCoin, byte rankID, Visibility visibility,
                   HashMap<Particles, Boolean> particles, String languageISO){
        this.id = id;
        this.uuid = uuid;
        this.partyUUID = partyUUID;
        this.quillCoin = quillCoin;
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

    public int getQuillCoin(){
        return quillCoin;
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

    public String getLanguageISO(){
        return languageISO;
    }

    public boolean hasParty(){
        return getPartyUUID() != null;
    }

    public void setPartyUUID(UUID partyUUID){
        this.partyUUID = partyUUID;
        getSQLRequest().addData("party_uuid", getPartyUUID());
    }

    public void setQuillCoin(int quillCoin){
        this.quillCoin = quillCoin;
        getSQLRequest().addData("quillcoins", quillCoin);
    }

    public void setRankID(byte rankID){
        this.rankID = rankID;
        getSQLRequest().addData("rank_id", rankID);
    }

    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
        getSQLRequest().addData("visibility", getVisibility().name());
    }

    public void setParticle(HashMap<Particles, Boolean> particles){
        this.particles = particles;
        getSQLRequest().addData("json_particles", new ProfileSerializationAccount.Particle().serialize(getParticles()));
    }

    protected void setId(int id) {
        this.id = id;
    }

    public void setLanguage(String languageISO){
        this.languageISO = languageISO;
        sqlRequest.addData("language", languageISO);
    }

    public SQLRequest getSQLRequest(){
        return sqlRequest;
    }

    protected void setSQLRequest(){
        final SQLTablesManager sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), uuid.toString());
    }

    private static HashMap<Particles, Boolean> defaultParticles(){
        HashMap<Particles, Boolean> defaultParticles = new HashMap<>();
        for(Particles particles : Particles.values()){
            defaultParticles.put(particles, false);
        }
        return defaultParticles;
    }

    public enum Visibility {
        EVERYONE(2, (byte)10), FRIENDS(4, (byte)6), NOBODY(6, (byte)8);

        private final int slot;
        private final byte data;
        Visibility(final int slot, final byte data){
            this.slot = slot;
            this.data = data;
        }

        public int getSlot(){
            return slot;
        }

        public byte getData(){
            return data;
        }

        public static Visibility getVisibilityByData(byte data){
            return Arrays.stream(values()).parallel().filter(visibility -> visibility.getData() == data).findFirst().get();
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
