package drunkbot.manager;

import drunkbot.twitchai.bot.TwitchChannel;

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
    public final int DEFAULT_RUN_INTERVAL = 100 * 60 * 30; // 30 minutes in milliseconds
    private int runInterval = DEFAULT_RUN_INTERVAL; // 30 minutes in milliseconds
    private int onlineRunInterval = DEFAULT_RUN_INTERVAL;
    private int offlineRunInterval = 1000 * 60 * 120;

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
     * @param interval interval in seconds to execute the drunkbot.manager.ScheduledManager#onScheduledRun() method
     */
    public void setRunInterval(int interval)
    {
        runInterval = interval * 1000;
        initCurrencyScheduler();
    }

    public int getOfflineRunInterval() {
        return offlineRunInterval;
    }

    public int getOnlineRunInterval() {
        return onlineRunInterval;
    }

    public abstract void setOnline(boolean online);
}
