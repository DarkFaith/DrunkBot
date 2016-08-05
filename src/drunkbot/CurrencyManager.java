package drunkbot;

import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.bot.TwitchChannelListener;
import drunkbot.twitchai.bot.TwitchUser;
import drunkbot.twitchai.util.LogUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kevin on 28/06/2016.
 */
public abstract class CurrencyManager implements TwitchChannelListener
{
    private final HashMap<String, Currency> currencyMap = new HashMap<>();
    private int generateInterval = 1000 * 60 * 10; // 10 minutes in milliseconds
    private double offlineGenerateAmount = 1.00;
    //private int generateInterval = 5000; // Every 5 seconds for testing
    private double generateAmount = 4.00;
    private long timestampLastGenerate = System.currentTimeMillis();
    TwitchChannel channel;

    private int maxNumUsers = 0;
    // Generate currency for all users every x minutes (default 10)
    private ScheduledFuture<?> currencyScheduledFuture = null;
    private ScheduledExecutorService currencyGenerateExecutor = Executors.newSingleThreadScheduledExecutor();
    private Runnable currencyGenerateRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            double amount;
            double bonusGenerateAmount = 0;
            if (channel.getTwitchAPI().isOnline()) {
                //bonusGenerateAmount = 2 * maxNumUsers/5;
                bonusGenerateAmount = 0; // not enabled
                amount = generateAmount + bonusGenerateAmount;
                maxNumUsers = channel.getUsers().size();
            } else {
                amount = offlineGenerateAmount;
            }

            if (generateInterval > 0)
            {
                if (giveToAll(amount))
                {
                    timestampLastGenerate = System.currentTimeMillis();
                    onCurrencyGenerated(amount, bonusGenerateAmount);
                }
            }
        }
    };

    public boolean giveToAll(double amount) {
        if (amount > 0)
        {
            if (channel == null)
                return false;
            // Detect new users
            ArrayList<TwitchUser> users = channel.getUsers();
            if (users.isEmpty())
            {
                LogUtils.logMsg("User list is empty. No currency given");
                return false;
            }
            for (int i = 0; i < users.size(); i++)
            {
                String userName = users.get(i).getName();

                // Add user if not in currency list
                if (!currencyMap.containsKey(userName))
                    currencyMap.put(userName, new Currency());

                Currency userCurrency = currencyMap.get(userName);
                // Increment user currency
                userCurrency.add(amount);
            }
            return true;
        }
        return false;
    }


    public abstract void onCurrencyGenerated(double amountGenerated, double bonusGenerated);

    public CurrencyManager(TwitchChannel channel)
    {
        this.channel = channel;
    }

    public double getGenerateAmount()
    {
        return generateAmount;
    }

    public void setGenerateAmount(double amount)
    {
        if (amount < 0)
            amount = 0;
        generateAmount = amount;
    }

    public double getOfflineGenerateAmount()
    {
        return offlineGenerateAmount;
    }

    public void setOfflineGenerateAmount(double amount)
    {
        if (amount < 0)
            amount = 0;
        offlineGenerateAmount = amount;
    }

    public int getGenerateInterval()
    {
        return generateInterval;
    }

    public void setGenerateInterval(int interval)
    {
        generateInterval = interval;
        initCurrencyScheduler();
    }

    public void init()
    {
        load();
        initCurrencyScheduler();
    }

    private void initCurrencyScheduler()
    {
        if (currencyScheduledFuture != null)
        {
            currencyScheduledFuture.cancel(false);
        }

        // time elapsed since last update counts in new interval instead of restarting
        long timeSinceLastGenerate = System.currentTimeMillis() - timestampLastGenerate;;
        long timeBeforeFirstGenerate = generateInterval - timeSinceLastGenerate;
        if (generateInterval > 0)
        {
            currencyScheduledFuture = currencyGenerateExecutor.scheduleAtFixedRate(currencyGenerateRunnable, timeBeforeFirstGenerate, generateInterval, TimeUnit.MILLISECONDS);
            LogUtils.logMsg("Currency scheduler started");
        } else {
            LogUtils.logMsg("Currency scheduler stopped (generate interval is 0 or less");
        }

    }

    public void load()
    {
        try (FileInputStream fis = new FileInputStream(channel.getDir() + "Currency.ser");
        ObjectInputStream ois = new ObjectInputStream(fis)) {
            currencyMap.putAll((HashMap) ois.readObject());
            //FileInputStream is = new FileInputStream();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        LogUtils.logMsg("Loaded currency");
    }

    public void save()
    {

        try (FileOutputStream fos = new FileOutputStream(channel.getDir() + "Currency.ser");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            if (!currencyMap.isEmpty())
            {
                oos.writeObject(currencyMap);
            }
            //FileInputStream is = new FileInputStream();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        }
        LogUtils.logMsg("Saved currency");
    }

    public Currency getCurrency(String user)
    {
        return currencyMap.get(user);
    }

    public boolean userExists(String user) {
        for (String currencyUser : currencyMap.keySet()) {
            return user.equalsIgnoreCase(currencyUser);
        }
        return false;
    }

    public String getUserKey(String user) {
        for (String currencyUser : currencyMap.keySet()) {
            if (currencyUser.equalsIgnoreCase(user)) {
                return currencyUser;
            }
        }
        return null;
    }


    @Override
    public void onUserAdded()
    {
        int numUsers = channel.getUsers().size();
        if (maxNumUsers < channel.getUsers().size())
            maxNumUsers = numUsers;
    }

    @Override
    public void onUserRemoved()
    {
    }
}
