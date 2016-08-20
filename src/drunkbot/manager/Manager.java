package drunkbot.manager;

import drunkbot.twitchai.bot.TwitchChannel;

/**
 * Created by Kevin on 19/08/2016.
 */
public abstract class Manager
{
    private TwitchChannel channel;

    // Empty private constructor to prevent implementation
    private Manager(){}

    public Manager(TwitchChannel channel)
    {
        this.channel = channel;
    }

    public TwitchChannel getTwitchChannel()
    {
        return channel;
    }

    public void init() {
        load();
    }

    public abstract boolean load();

    public abstract boolean save();

    protected String getSaveLocation()
    {
        return channel.getDir();
    }
}
