package net.quillcraft.commons.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.lyflow.sqlrequest.SQLRequest;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationAccount;

import java.util.*;

public class Account {

    private int id;
    private UUID uuid;
    private UUID partyUUID;
    private int quillCoin;
    private byte rankID;
    private Visibility visibility;
    private Map<Particles, Boolean> particles;
    private String languageISO;

    @JsonIgnore
    private SQLRequest sqlRequest;

    //Redis
    private Account() {}

    public Account(ProxiedPlayer player) {
        this(player.getUniqueId());
    }

    public Account(UUID uuid) {
        this(0, uuid, null, 10, (byte) 0, Visibility.EVERYONE, defaultParticles(), "en_us");
    }

    public Account(int id, UUID uuid, UUID partyUUID, int quillCoin, byte rankID, Visibility visibility, Map<Particles, Boolean> particles, String languageISO) {
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

    private static Map<Particles, Boolean> defaultParticles() {
        final EnumMap<Particles, Boolean> defaultParticles = new EnumMap<>(Particles.class);
        Arrays.stream(Particles.values()).forEach(particles -> defaultParticles.put(particles, false));
        return defaultParticles;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getPartyUUID() {
        return partyUUID;
    }

    public void setPartyUUID(UUID partyUUID) {
        this.partyUUID = partyUUID;
        getSQLRequest().addData("party_uuid", getPartyUUID());
    }

    public int getQuillCoin() {
        return quillCoin;
    }

    public void setQuillCoin(int quillCoin) {
        this.quillCoin = quillCoin;
        getSQLRequest().addData("quillcoins", quillCoin);
    }

    public byte getRankID() {
        return rankID;
    }

    public void setRankID(byte rankID) {
        this.rankID = rankID;
        getSQLRequest().addData("rank_id", rankID);
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
        getSQLRequest().addData("visibility", getVisibility().name());
    }

    public Map<Particles, Boolean> getParticles() {
        return particles;
    }

    public String getLanguageISO() {
        return languageISO;
    }

    public boolean hasParty() {
        return getPartyUUID() != null;
    }

    public void setParticle(Map<Particles, Boolean> particles) {
        this.particles = particles;
        getSQLRequest().addData("json_particles", new ProfileSerializationAccount.Particle().serialize(getParticles()));
    }

    public void setLanguage(String languageISO) {
        this.languageISO = languageISO;
        sqlRequest.addData("language", languageISO);
    }

    public SQLRequest getSQLRequest() {
        return sqlRequest;
    }

    public void setSQLRequest() {
        final SQLTablesManager sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), uuid.toString());
    }

    public enum Visibility {
        EVERYONE, PARTY, FRIENDS, NOBODY
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
