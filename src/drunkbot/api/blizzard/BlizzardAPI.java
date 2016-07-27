package drunkbot.api.blizzard;

import drunkbot.api.API;
import drunkbot.api.blizzard.api.BlizzardApi;
import drunkbot.api.blizzard.api.dto.Competitive;
import drunkbot.api.blizzard.api.dto.Data;
import drunkbot.api.blizzard.constant.Platform;
import drunkbot.api.blizzard.constant.Region;
import drunkbot.twitchai.bot.TwitchChannel;

/**
 * Created by Kevin on 26/07/2016.
 */
public abstract class BlizzardAPI extends API
{
    BlizzardApi api = new BlizzardApi(Region.US, Platform.PC);
    Data data = new Data();
    String bnetID = "DarkFaith#1405";
    public BlizzardAPI(TwitchChannel channel)
    {
        super(channel);
    }

    @Override
    public void init()
    {
        setUpdateInverval(1000 * 60 * 5); // 5 minutes
    }

    @Override
    protected boolean update()
    {
        try
        {
            Competitive competitive = api.getCompetitive(bnetID);
            data.setCompetitive(competitive);
            return true;
        } catch (BlizzardApiException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public String getRank()
    {
        doUpdate(data.getCompetitive() == null);

        if (data.getCompetitive() == null)
            return "Could not get Overwatch rank";
        String rank = data.getCompetitive().getRank();
        return "Rank: " + rank;
    }

}
