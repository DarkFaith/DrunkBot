package drunkbot.twitchai.bot;

import drunkbot.*;
import drunkbot.api.blizzard.BlizzardAPI;
import drunkbot.api.riot.RiotAPI;
import drunkbot.api.twitch.TwitchAPI;
import drunkbot.cmd.CommandsCustom;
import drunkbot.CurrencyManager;
import drunkbot.twitchai.bot.channel.Settings;
import drunkbot.twitchai.util.Globals;

import java.util.ArrayList;

public abstract class TwitchChannel implements TwitchChannelListener
{
    private Settings settings = new Settings();
    private String m_name;
    private ArrayList<TwitchUser> m_users;
    private int m_cmd_sent;
    private String m_dir;
    private CommandsCustom commandsCustom = new CommandsCustom(this);
    private MediaReader mediaReader = new MediaReader(this);
    private Quotes quotes = new Quotes(this);
    private CurrencyManager currencyManager = new CurrencyManager(this)
    {
        @Override
        public void onCurrencyGenerated(double amountGenerated, double bonusGenerated)
        {
            if (bonusGenerated > 0)
            {
                sendMessage("Everyone here gets " + Globals.g_currencyFormat.format(amountGenerated)
                        + " (+" + Globals.g_currencyFormat.format(bonusGenerated) + ") souls for being awesome! (+2 souls for every 5 viewers)");
            } else {
                sendMessage("Everyone here gets " + Globals.g_currencyFormat.format(amountGenerated) + " souls for being awesome!");
            }
            currencyManager.save();
        }
    };

    private TwitchAPI twitchAPI = new TwitchAPI(this)
    {
        @Override
        public void onSuccess(String messageReply)
        {

        }
    };
    private RiotAPI riotAPI = new RiotAPI(this)
    {
        @Override
        public void onSuccess(String messageReply)
        {

        }
    };
    private BlizzardAPI blizzAPI = new BlizzardAPI(this)
    {
        @Override
        public void onSuccess(String messageReply)
        {

        }
    };

    public TwitchChannel(String name)
    {
        m_name = name;
        m_dir = Globals.g_channel_base_dir + m_name + "/";
        m_users = new ArrayList<TwitchUser>();
        m_cmd_sent = 0;
        quotes.load();
        mediaReader.start();
        commandsCustom.load();
        riotAPI.load();
        twitchAPI.init();
        currencyManager.init();
    }
    {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                currencyManager.save();
            }
        }));
    }

    public abstract void sendMessage(String message);

    @Override
    public String toString()
    {
        return "TwitchChannel[" + m_name + ", " + m_users.size() + ", " + m_cmd_sent + "]";
    }

    public String getName()
    {
        return m_name;
    }

    public String getNameNoTag()
    {
        return m_name.replaceFirst("#", "");
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public void addUser(TwitchUser user)
    {
        m_users.add(user);
        onUserAdded();
        currencyManager.onUserAdded();
    }

    public void delUser(TwitchUser user)
    {
        m_users.remove(user);
        onUserRemoved();
        currencyManager.onUserRemoved();
    }

    public ArrayList<TwitchUser> getUsers()
    {
        return m_users;
    }

    public ArrayList<TwitchUser> getOperators()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchUser u : m_users)
        {
            if (u.isOperator())
            {
                result.add(u);
            }
        }

        return result;
    }

    public ArrayList<TwitchUser> getModerators()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchUser u : m_users)
        {
            if (u.isModerator())
            {
                result.add(u);
            }
        }

        return result;
    }

    public TwitchUser getUser(String name)
    {
        TwitchUser result = null;

        for (TwitchUser u : m_users)
        {
            if (u.getName().equals(name))
            {
                result = u;
                break;
            }
        }

        return result;
    }

    public TwitchUser getOperator(String name)
    {
        TwitchUser result = null;

        for (TwitchUser u : getOperators())
        {
            if (u.getName().equals(name))
            {
                result = u;
                break;
            }
        }

        return result;
    }

    public TwitchUser getModerator(String name)
    {
        TwitchUser result = null;

        for (TwitchUser u : getModerators())
        {
            if (u.getName().equals(name))
            {
                result = u;
                break;
            }
        }

        return result;
    }

    public int getCmdSent()
    {
        return m_cmd_sent;
    }

    public void setCmdSent(int cmd_sent)
    {
        m_cmd_sent = cmd_sent;
    }

    public String getDir()
    {
        return m_dir;
    }

    public TwitchAPI getTwitchAPI()
    {
        return twitchAPI;
    }

    public RiotAPI getRiotAPI()
    {
        return riotAPI;
    }

    public MediaReader getMediaReader() { return mediaReader; }

    public Quotes getQuotes() { return quotes; }

    public CommandsCustom getCustomCommands() { return commandsCustom; }

    public void sendUpTime()
    {
        twitchAPI.sendUpTime();
//        long uptime = twitchAPI.sendUpTime();
//        String replyString = getNameNoTag() + " has been live for ";
//        if (uptime < 10000 && uptime >= 0) {
//            return "Just started! Calm yo tits!";
//        } else if (uptime >= 10000 && uptime < 60000) {
//            replyString += "less than a minute";
//        } else if (uptime >= 60000 && uptime < 3600000) { // over a minute, under an hour
//            replyString += uptime/1000/60 + " minutes";
//        } else if (uptime >= 3600000) {
//            String hourString;
//            String minuteString;
//            long minutes = (uptime/(1000*60));
//            long hours = minutes / 60;
//            minutes = minutes - (hours * 60);
//            minuteString = minutes > 1 ? "minutes" : "minute";
//            hourString = hours > 1 ? "hours" : "hour";
//            replyString += hours + " " + hourString + " and " + minutes + " " + minuteString;
//        } else {
//            return getName() + " is offline. Check the schedule for usual stream times";
//        }
//        return replyString;
    }

    public CurrencyManager getCurrencyManager()
    {
        return currencyManager;
    }

    public BlizzardAPI getBlizzAPI()
    {
        return blizzAPI;
    }

    @Override
    public void onUserAdded()
    {
        currencyManager.onUserAdded();
    }

    @Override
    public void onUserRemoved()
    {
        currencyManager.onUserRemoved();
    }
}
