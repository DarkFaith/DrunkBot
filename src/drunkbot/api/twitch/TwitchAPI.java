/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot.api.twitch;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import drunkbot.api.API;
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.LogUtils;

import static drunkbot.twitchai.util.LogUtils.logMsg;

/**
 *
 * @author Kevin Lagacé <kevlag100@hotmail.com>
 */
public abstract class TwitchAPI extends API
{
    private Twitch twitch = new Twitch();
    private String channelName = "";
    //private String baseURL = "https://api.twitch.tv/";
    TwitchChannel channel;
    Stream lastValidStream = null;
    Stream currentStream = null;
    Channel currentChannel = null;
//    private ScheduledExecutorService updateTwitchAPI = Executors.newSingleThreadScheduledExecutor();
//    Runnable updateTwitchRunnable = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            //twitch.channels().get(channelName, channelResponseHandler);
//
//            //System.out.println("Stream loaded: " + lastValidStream.getChannel().getDisplayName());
//        }
//    };
    
    public TwitchAPI(TwitchChannel channel)
    {
        super(channel);
        this.channel = channel;
    }

    public void init()
    {
        this.channelName = channel.getNameNoTag();
        setUpdateInverval(1000 * 60 * 15); // 15 minutes
        //updateTwitchAPI.scheduleAtFixedRate(updateTwitchRunnable, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    protected boolean update()
    {
        currentStream = twitch.streams().get(channelName);
        if (currentStream == null)
        {
            LogUtils.logErr("data/channels/" + channel.getName() + "/logs/", "/api", "Failed to update stream object. Stream is offline or Twitch API may be down");
            return false;
        } else {
            lastValidStream = currentStream;
            setLastUpdateTime();
            LogUtils.logMsg("data/channels/" + channel.getName() + "/logs/", "/api", "Successfully updated stream object");
            return true;
        }
    }
 
    public void sendUpTime()
    {
        boolean updated = false;
        if (isUpdateDue())
            updated = update();

        if (lastValidStream == null)
        {
            channel.sendMessage(channel.getNameNoTag() + " is offline. Check the schedule for usual stream times");
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastUpdate = currentTime - getLastUpdateTime();
        long uptime = currentTime - lastValidStream.getCreatedAt().getTime() + timeSinceLastUpdate;

        String replyString = channel.getNameNoTag() + " has been live for ";
        if (!updated)
            replyString += "at least ";
        if (uptime < 10000 && uptime >= 0)
        {
            channel.sendMessage("Just started! Calm yo tits!");
            return;
        } else if (uptime >= 10000 && uptime < 60000)
        {
            replyString += "less than a minute";
        } else if (uptime >= 60000 && uptime < 3600000)
        { // over a minute, under an hour
            replyString += uptime / 1000 / 60 + " minutes";
        } else if (uptime >= 3600000)
        {
            String hourString;
            String minuteString;
            long minutes = (uptime / (1000 * 60));
            long hours = minutes / 60;
            minutes = minutes - (hours * 60);
            minuteString = minutes > 1 ? "minutes" : "minute";
            hourString = hours > 1 ? "hours" : "hour";
            replyString += hours + " " + hourString + " and " + minutes + " " + minuteString;
        }
        channel.sendMessage(replyString);
    }

    public boolean isOnline() {
        boolean updated = update();
        return currentStream != null;
    }
    
    public String getCurrentGame()
    {
        LogUtils.logMsg("GetCurrentGame start");
        boolean updated = update();
        LogUtils.logMsg("GetCurrentGame: " + updated);

        if (lastValidStream == null)
            return "No game detected.";

        String game = lastValidStream.getGame();
        if (game != null && !game.isEmpty())
            return game;
        else
            return "No game detected.";
    }


//    private String upTime() {
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(baseURL);
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        
//    }
}
