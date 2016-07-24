package drunkbot;

import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.bot.TwitchUser;

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
public abstract class CurrencyManager
{
    private final HashMap<String, Currency> currencyMap = new HashMap<>();
    private int generateInterval = 1000 * 60 * 10; // 10 minutes in milliseconds
    //private int generateInterval = 5000; // Every 5 seconds for testing
    private double generateAmount = 6.66;
    TwitchChannel channel;

    // Generate currency for all users every x minutes (default 10)
    private ScheduledFuture<?> currencyScheduledFuture = null;
    private ScheduledExecutorService currencyGenerateExecutor = Executors.newSingleThreadScheduledExecutor();
    private Runnable currencyGenerateRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            // Detect new users
            if (channel == null)
                return;
            ArrayList<TwitchUser> users = channel.getUsers();
            if (users.isEmpty())
            {
                System.out.println("User list is empty");
                return;
            }
            for (int i = 0; i < users.size(); i++)
            {
                String userName = users.get(i).getName();


                // Add user if not in currency list
                if (!currencyMap.containsKey(userName))
                    currencyMap.put(userName, new Currency());

                Currency userCurrency = currencyMap.get(userName);
                // Increment user currency
                userCurrency.add(generateAmount);
            }
            onCurrencyGenerated(generateAmount);
        }
    };

    public abstract void onCurrencyGenerated(double amountGenerated);

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
        generateAmount = amount;
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
        currencyScheduledFuture = currencyGenerateExecutor.scheduleAtFixedRate(currencyGenerateRunnable, generateInterval, generateInterval, TimeUnit.MILLISECONDS);
    }

    public void load()
    {
        try (FileInputStream fis = new FileInputStream(channel.getDir() + "Currency.properties");
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
        System.out.println("Loaded currency");
    }

    public void save()
    {

        try (FileOutputStream fos = new FileOutputStream(channel.getDir() + "Currency.properties");
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
        System.out.println("Saved currency");
    }

    public Currency getCurrency(String user)
    {
        System.out.println("Looking for user: " + user + " in currencyMap");
        return currencyMap.get(user);
    }

}
