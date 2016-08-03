/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot;

import drunkbot.twitchai.bot.TwitchAI;
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class Quotes {
    TwitchChannel channel;
    public final ArrayList<String> quoteList = new ArrayList();
    private static final Random random = new Random();
//    static {
//        load();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                save();
//            }
//        });
//    }

    public Quotes(TwitchChannel channel)
    {
        this.channel = channel;
    }
    
    public void load() {
        LogUtils.logMsg("Loading quotes...");
        try (FileReader reader = new FileReader(channel.getDir() + "quotes.txt")) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                quoteList.add(line);
            }
            reader.close();
            //FileInputStream is = new FileInputStream();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save() {
        LogUtils.logMsg("Saving quotes...");
        try (FileWriter writer = new FileWriter(channel.getDir() + "quotes.txt")) {
            for (String quote : quoteList) {
                writer.write(quote + System.getProperty("line.separator"));
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public String getRandom() {
        int index = random.nextInt(quoteList.size());
        String quoteString = quoteList.get(index) + " [" + (index + 1) + "]";
        
        return quoteString;
    }

    public String getRandom(String sample) {
        String sampleLC = sample.toLowerCase();
        ArrayList<String> matches = new ArrayList<>();
        for (int i = 0; i < quoteList.size(); i++) {
            String quote = quoteList.get(i);
            String quoteLC = quote.toLowerCase();
            if (quoteLC.contains(sampleLC)) {
                matches.add(quote + String.format(" [%d]", i + 1));
            }
        }
        if (matches.size() > 0)
        {
            int index = random.nextInt(matches.size());
            String quoteString = matches.get(index);
            return quoteString;
        } else {
            return null;
        }

    }
    
    public boolean add(String s) {
        quoteList.add(s);
        save();
        return true;
    }
    
    public boolean remove(int index) {
        if (index > 0 && index < quoteList.size() + 1) {
            quoteList.remove(index - 1);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean removeLast() {
        quoteList.remove(quoteList.size() - 1);
        return true;
    }
    
    public String get(int index) {
        if (index > 0 && index < quoteList.size() + 1)
            return quoteList.get(index - 1);
        return "";
    }
    
    public String getLast() {
        return quoteList.get(quoteList.size() - 1);
    }
    
    public int getNumQuotes() {
        return quoteList.size();
    }

}
