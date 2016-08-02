package drunkbot;

import static drunkbot.twitchai.util.Globals.*;
import static drunkbot.twitchai.util.LogUtils.logMsg;
import static drunkbot.twitchai.util.LogUtils.logErr;
import static drunkbot.twitchai.util.GenUtils.exit;
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.bot.TwitchUser;
import drunkbot.twitchai.bot.TwitchAI;
import drunkbot.twitchai.util.ConfUtils;
import drunkbot.twitchai.util.FileUtils;

public class Main
{

    public static void main(String[] args)
    {
        FileUtils.directoryExists("data");
        FileUtils.directoryExists("data/channels");
        ConfUtils.init();
        TwitchAI twitchai = new TwitchAI();
        twitchai.init_twitch();

        int init_time = 5;
        while (!twitchai.isInitialized())
        {
            init_time--;
            try
            {
                logMsg("Waiting for twitch member/cmd/tag responses... " + init_time);
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        if (!twitchai.isInitialized())
        {
            logErr("Failed to receive twitch member/cmd/tag permissions!");
            exit(1);
        }

        twitchai.init_channels();
        twitchai.init_reader();

        float time;
        long timeStart, timeEnd;

        while (twitchai.isConnected())
        {
            timeStart = System.nanoTime();
            g_date.setTime(System.currentTimeMillis());

            for (TwitchChannel c : twitchai.getTwitchChannels())
            {
                if (c.getCmdSent() > 0)
                {
                    c.setCmdSent(c.getCmdSent() - 1);
                }
            }

            for (TwitchUser u : twitchai.getAllUsers())
            {
                if (u.getCmdTimer() > 0)
                {
                    u.setCmdTimer(u.getCmdTimer() - 1);
                }
            }

            timeEnd = System.nanoTime();
            time = (float) (timeEnd - timeStart) / 1000000.0f;

            twitchai.setCycleTime(time);

            /*
             * Main loop ticks only once per second.
             */
            try
            {
                if (time < 1000.0f)
                {
                    Thread.sleep((long) (1000.0f - time));
                }
                else
                {
                    logErr("Warning! Main thread cycle time is longer than a second! Skipping sleep! Cycle-time: " + time);
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
