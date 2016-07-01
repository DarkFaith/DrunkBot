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
import drunkbot.twitchai.bot.TwitchChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Kevin Lagacé <kevlag100@hotmail.com>
 */
public class TwitchAPI
{
    private Twitch twitch = new Twitch();
    private String channelName = "";
    //private String baseURL = "https://api.twitch.tv/";
    TwitchChannel channel;
    Stream currentStream = null;
    Channel currentChannel = null;
    private ScheduledExecutorService updateTwitchAPI = Executors.newSingleThreadScheduledExecutor();
    Runnable updateTwitchRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            //twitch.channels().get(channelName, channelResponseHandler);
            twitch.streams().get(channelName, streamResponseHandler);
            //System.out.println("Stream loaded: " + currentStream.getChannel().getDisplayName());
        }
    };
    
    public TwitchAPI(TwitchChannel channel)
    {
        this.channel = channel;
    }

    public void init()
    {
        this.channelName = channel.getNameNoTag();
        updateTwitchAPI.scheduleAtFixedRate(updateTwitchRunnable, 0, 1, TimeUnit.MINUTES);
    }
    
    public ChannelResponseHandler channelResponseHandler = new ChannelResponseHandler() {
        
        
        @Override
        public void onSuccess(Channel chnl)
        {
            currentChannel = chnl;
        }

        @Override
        public void onFailure(int i, String string, String string1)
        {
        }

        @Override
        public void onFailure(Throwable thrwbl)
        {
        }
    };
    
    public StreamResponseHandler streamResponseHandler = new StreamResponseHandler() {
        @Override
        public void onSuccess(Stream stream)
        {
            currentStream = stream;
        }

        @Override
        public void onFailure(int i, String string, String string1)
        {
        }

        @Override
        public void onFailure(Throwable thrwbl)
        {
        }
    };
 
    public long getUpTime()
    {
        if (currentStream != null)
            return System.currentTimeMillis() - currentStream.getCreatedAt().getTime();
        else
            return 0;
    }
    
    public String getCurrentGame()
    {
        if (currentStream != null)
            return currentStream.getGame();
        else
            return "No game detected";
    }
   

    
    
//    private String upTime() {
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(baseURL);
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        
//    }
}
