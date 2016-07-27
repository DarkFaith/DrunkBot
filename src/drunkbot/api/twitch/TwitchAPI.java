/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot.api.twitch;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import drunkbot.api.API;
import drunkbot.twitchai.bot.TwitchChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
//            //System.out.println("Stream loaded: " + currentStream.getChannel().getDisplayName());
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
        try
        {
            twitch.streams().get(channelName, new StreamResponseHandler() {

                @Override
                public void onSuccess(Stream stream)
                {
                    currentStream = stream;
                    setLastUpdateTime();
                }

                @Override
                public void onFailure(int i, String string, String string1)
                {
                }

                @Override
                public void onFailure(Throwable thrwbl)
                {
                }
            });

        } catch (Exception e) {
            return false;
        }
        return true;
    }

//    public ChannelResponseHandler channelResponseHandler = new ChannelResponseHandler() {
//
//
//        @Override
//        public void onSuccess(Channel chnl)
//        {
//            currentChannel = chnl;
//        }
//
//        @Override
//        public void onFailure(int i, String string, String string1)
//        {
//        }
//
//        @Override
//        public void onFailure(Throwable thrwbl)
//        {
//        }
//    };
 
    public void sendUpTime()
    {
        twitch.streams().get(channelName, new StreamResponseHandler() {

            @Override
            public void onSuccess(Stream stream)
            {
                currentStream = stream;
                if (currentStream == null)
                {
                    channel.sendMessage(channel.getNameNoTag() + " is offline. Check the schedule for usual stream times");
                    return;
                }
                setLastUpdateTime();

                long uptime = System.currentTimeMillis() - currentStream.getCreatedAt().getTime();
                String replyString = channel.getNameNoTag() + " has been live for ";
                if (uptime < 10000 && uptime >= 0) {
                    channel.sendMessage("Just started! Calm yo tits!");
                    return;
                } else if (uptime >= 10000 && uptime < 60000) {
                    replyString += "less than a minute";
                } else if (uptime >= 60000 && uptime < 3600000) { // over a minute, under an hour
                    replyString += uptime/1000/60 + " minutes";
                } else if (uptime >= 3600000) {
                    String hourString;
                    String minuteString;
                    long minutes = (uptime/(1000*60));
                    long hours = minutes / 60;
                    minutes = minutes - (hours * 60);
                    minuteString = minutes > 1 ? "minutes" : "minute";
                    hourString = hours > 1 ? "hours" : "hour";
                    replyString += hours + " " + hourString + " and " + minutes + " " + minuteString;
                }
                channel.sendMessage(replyString);
            }

            @Override
            public void onFailure(int i, String string, String string1)
            {
            }

            @Override
            public void onFailure(Throwable thrwbl)
            {
            }
        });
    }
    
    public String getCurrentGame()
    {
        twitch.streams().get(channelName, new StreamResponseHandler() {

            @Override
            public void onSuccess(Stream stream)
            {
                currentStream = stream;
                if (currentStream == null)
                {
                    channel.sendMessage(channel.getNameNoTag() + " is offline. Check the schedule for usual stream times");
                    return;
                }
//                String game = currentStream.getGame();
//                if (game != null && !game.isEmpty())
//                    channel.sendMessage(game);
//                else
//                    channel.sendMessage("No game detected.");
            }

            @Override
            public void onFailure(int i, String string, String string1)
            {
            }

            @Override
            public void onFailure(Throwable thrwbl)
            {
            }
        });
        String game = currentStream.getGame();
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
