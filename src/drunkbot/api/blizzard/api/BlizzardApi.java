package drunkbot.api.blizzard.api;

import drunkbot.api.blizzard.BlizzardApiException;
import drunkbot.api.blizzard.api.dto.Competitive;
import drunkbot.api.blizzard.constant.Platform;
import drunkbot.api.blizzard.constant.Region;

/**
 * Created by Kevin on 26/07/2016.
 */
public class BlizzardApi
{
    private Region region = Region.US; // North American region default
    private Platform platform = Platform.PC;
    private String key;

    public BlizzardApi() {

    }

    public BlizzardApi(String key)
    {
        setKey(key);
    }

    public BlizzardApi(Region region, Platform platform)
    {
        setRegion(region);
        setPlatform(platform);
    }

    public BlizzardApi(String key, Region region, Platform platform)
    {
        this(region, platform);
        setKey(key);
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }

    public Platform getPlatform()
    {
        return platform;
    }

    public void setPlatform(Platform platform)
    {
        this.platform = platform;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Retrieves Competitive
     *
     * @return Competitive object for battletag
     * @throws BlizzardApiException
     *             If the API returns an error or unparsable result
     * @see Competitive
     */
    public Competitive getCompetitive(String battleTag) throws BlizzardApiException
    {
        return getCompetitive(getRegion(), getPlatform(), battleTag);
    }

    /**
     * Retrieves Competitive
     *
     * @return Competitive object for battletag
     * @throws BlizzardApiException
     *             If the API returns an error or unparsable result
     * @see Competitive
     */
    public Competitive getCompetitive(Region region, Platform platform, String battleTag) throws BlizzardApiException
    {
        return AccountApi.getAccountProfile(key, region, platform, battleTag).getData().getCompetitive();
    }


}
