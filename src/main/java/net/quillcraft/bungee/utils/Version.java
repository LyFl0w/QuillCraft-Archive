package net.quillcraft.bungee.utils;

public class Version {

    public static String getMinecraftVersion(int protocolVersion){
        for(ProtocolVersion protocolVersions : ProtocolVersion.values()){
            if(protocolVersion >= protocolVersions.getProtocolVersionMin() && protocolVersion <= protocolVersions.getProtocolVersionMax()) return protocolVersions.getVersion();
        }
        return null;
    }

    public static boolean hasProtocolVersionListed(int protocolVersion){
        for(ProtocolVersion protocolVersions : ProtocolVersion.values()){
            if(protocolVersion > protocolVersions.getProtocolVersionMin() && protocolVersion < protocolVersions.getProtocolVersionMax()) return true;
        }
        return false;
    }

    private enum ProtocolVersion {

        A("1.7", 2, 5),
        B("1.8", 47, 47),
        C("1.9", 107, 110),
        D("1.10", 210, 210),
        E("1.11", 315, 316),
        F("1.12", 	335, 340),
        G("1.13", 393, 404),
        H("1.14", 477, 498),
        I("1.15", 573, 578),
        J("1.16", 735, 753);

        private String version;
        private int protocolVersionMin, protocolVersionMax;

        ProtocolVersion(String version, int protocolVersionMin, int protocolVersionMax){
            this.version = version;
            this.protocolVersionMin = protocolVersionMin;
            this.protocolVersionMax = protocolVersionMax;
        }

        public boolean hasProtocolVersion(){
            return true;
        }

        public String getVersion(){
            return version;
        }

        public int getProtocolVersionMin(){
            return protocolVersionMin;
        }

        public int getProtocolVersionMax(){
            return protocolVersionMax;
        }
    }

}
