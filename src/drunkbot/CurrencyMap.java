package drunkbot;

import drunkbot.twitchai.bot.TwitchChannel;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kevin on 28/06/2016.
 */
public class CurrencyMap extends HashMap<String, Currency>
{
    TwitchChannel channel;
    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            // Detect new users

            // Increase by x amount
            Iterator it = entrySet().iterator();
            while (it.hasNext())
            {

            }
        }
    };

    public CurrencyMap(TwitchChannel channel)
    {
        this.channel = channel;
    }

    public void init()
    {
        load();

    }

    public void load()
    {
        try (FileInputStream fis = new FileInputStream(channel.getDir() + "Currency.properties");
        ObjectInputStream ois = new ObjectInputStream(fis)) {
            putAll((CurrencyMap) ois.readObject());
            //FileInputStream is = new FileInputStream();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        try (FileOutputStream fos = new FileOutputStream(channel.getDir() + "Currency.properties");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            if (!this.isEmpty())
            {
                oos.writeObject(this);
            }
            //FileInputStream is = new FileInputStream();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
