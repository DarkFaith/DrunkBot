package drunkbot;

import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.LogUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kevin on 19/08/2016.
 */
public abstract class ScheduledManager extends Manager
{
    private long timestampLastRun = System.currentTimeMillis();
    private int runInterval = 1000 * 60 * 30; // 30 minutes in milliseconds

    private Runnable scheduledRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            onScheduledRun();
            timestampLastRun = System.currentTimeMillis();
        }
    };

    private ScheduledFuture<?> scheduledFuture = null;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public ScheduledManager(TwitchChannel channel)
    {
        super(channel);
    }

    @Override
    public void init()
    {
        super.init();
        initCurrencyScheduler();
    }

    private void initCurrencyScheduler()
    {
        if (scheduledFuture != null)
        {
            scheduledFuture.cancel(false);
        }

        // time elapsed since last update counts in new interval instead of restarting
        long timeSinceLastGenerate = System.currentTimeMillis() - timestampLastRun;
        long timeBeforeFirstGenerate = runInterval - timeSinceLastGenerate;
        if (runInterval > 0)
        {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(scheduledRunnable, timeBeforeFirstGenerate, runInterval, TimeUnit.MILLISECONDS);
        }
    }

    protected abstract void onScheduledRun();

    public int getRunInterval()
    {
        return runInterval;
    }

    /**
     *
     * @param interval interval in seconds to execute the drunkbot.ScheduledManager#onScheduledRun() method
     */
    public void setRunInterval(int interval)
    {
        runInterval = interval * 1000;
        initCurrencyScheduler();
    }
}
