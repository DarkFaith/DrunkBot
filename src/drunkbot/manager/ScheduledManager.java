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
    public final int DEFAULT_RUN_INTERVAL = 1000 * 60 * 15; // 30 minutes in milliseconds
    private int runInterval = DEFAULT_RUN_INTERVAL; // 30 minutes in milliseconds
    private int onlineRunInterval = DEFAULT_RUN_INTERVAL;
    private int offlineRunInterval = 1000 * 60 * 15;

    private Runnable scheduledRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            doScheduledRun();
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

    protected abstract void doScheduledRun();

    public int getRunInterval()
    {
        return runInterval;
    }

    /**
     *
     * @param interval interval in minutes to execute the drunkbot.manager.ScheduledManager#doScheduledRun() method
     */
    public void setRunInterval(int interval)
    {
        runInterval = interval;
        initCurrencyScheduler();
    }

    public int getOfflineRunInterval() {
        return offlineRunInterval;
    }

    public int getOnlineRunInterval() {
        return onlineRunInterval;
    }

    public abstract void onOnline();

    public abstract void onOffline();
}
