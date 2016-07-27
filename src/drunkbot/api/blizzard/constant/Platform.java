package drunkbot.api.blizzard.constant;

/**
 * Created by Kevin on 26/07/2016.
 */
public enum Platform
{
    PC("pc"),
    XBOXLIVE("xbl"),
    PSN("psn");

    private String platform;

    Platform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return platform;
    }

    @Override
    public String toString() {
        return getName();
    }
}
