package drunkbot.api.blizzard.constant;

/**
 * Created by Kevin on 26/07/2016.
 */
public enum Region
{
    EU("eu"),
    US("us"),
    KR("kr"),
    CN("cn"),
    GLOBAL("global");

    private String region;

    Region(String region) {
        this.region = region;
    }

    public String getName() {
        return region;
    }

    @Override
    public String toString() {
        return getName();
    }


}
