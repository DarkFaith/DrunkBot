package drunkbot;

import drunkbot.twitchai.bot.TwitchChannel;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kevin on 26/06/2016.
 */
public class Currency implements Serializable
{
    static final long serialVersionUID = 1L;
    double total = 0.0;
    long dateCreated = System.currentTimeMillis();
    public Currency()
    {

    }


//    public void load() {
//        System.out.println("Loading quotes...");
//        try (FileReader reader = new FileReader(channel.getDir() + "currency.txt")) {
//            BufferedReader br = new BufferedReader(reader);
//            String line;
//            while ((line = br.readLine()) != null) {
//                quoteList.add(line);
//            }
//            reader.close();
//            //FileInputStream is = new FileInputStream();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void save() {
//        System.out.println("Saving quotes...");
//        try (FileWriter writer = new FileWriter(channel.getDir() + "currency.txt")) {
//            for (String quote : quoteList) {
//                writer.write(quote + System.getProperty("line.separator"));
//            }
//            writer.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void set(double newTotal)
    {
        this.total = newTotal;
    }

    public double get()
    {
        return this.total;
    }

    public void add(double amt)
    {
        this.total += amt;
    }

    public void spend(double amt)
    {
        this.total -= amt;
    }

    public long getStartDate()
    {
        return dateCreated;
    }


}
