package drunkbot.api;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kevin on 26/07/2016.
 */
interface Updatable
{
    void onSuccess(String messageReply);
}
public abstract class API implements Updatable
{
    private long updateInterval = 1000 * 60 * 30;
    private long lastUpdateTime = System.currentTimeMillis();
    //private ScheduledExecutorService updateAPIExec = Executors.newSingleThreadScheduledExecutor();
    //private ScheduledFuture updateAPIFuture = null;

    private Runnable updateRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            update();
        }
    };

    public abstract void init();

    public void setUpdateInverval(long millis)
    {
        updateInterval = millis;
//        if (updateAPIFuture != null)
//        {
//            updateAPIFuture.cancel(false);
//        }
//        // Negative or 0 timeBeforeFirstRun means execute immediately
//        long timeBeforeFirstRun = updateInterval - (System.currentTimeMillis() - lastUpdateTime);
//        updateAPIExec.scheduleAtFixedRate(updateRunnable, timeBeforeFirstRun, updateInterval, TimeUnit.MILLISECONDS);
    }

    public long getUpdateInterval()
    {
        return updateInterval;
    }

    protected abstract boolean update();

    public void doUpdate(boolean force)
    {
        if (isUpdateDue() || force)
        {
            updateRunnable.run();
            setUpdateInverval(updateInterval);
        }
    }

    public boolean isUpdateDue()
    {
       return updateInterval - (System.currentTimeMillis() - lastUpdateTime) <= 0;
    }

    public void setLastUpdateTime() {
        lastUpdateTime = System.currentTimeMillis();
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
