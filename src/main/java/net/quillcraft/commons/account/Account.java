package net.quillcraft.commons.account;

import net.lyflow.sqlrequest.SQLRequest;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.management.sql.table.SQLTablesManager;
import net.quillcraft.bungee.manager.ProfileSerializationManager;

import java.util.Arrays;
import java.util.HashMap;
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
    private SQLRequest sqlRequest;

    //Redis
    public Account(){}

    public Account(final ProxiedPlayer player){
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
        final SQLTablesManager sqlTablesManager = SQLTablesManager.PLAYER_DATA;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), uuid.toString());
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
        getSQLRequest().addData("rankid", getRankID());
    }

    public void setVisibility(final Visibility visibility){
        this.visibility = visibility;
        getSQLRequest().addData("visibility", getVisibility().name());
    }

    public void setParticles(HashMap<Particles, Boolean> particles){
        this.particles = particles;
        getSQLRequest().addData("jsonparticles", new ProfileSerializationManager().serialize(getParticles()));
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
