/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot.cmd;

import drunkbot.twitchai.bot.TwitchChannel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class CommandsCustom {
    public Map<String, String> commandList = new HashMap<>();
    private TwitchChannel channel;
//
//    static {
//        load();
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                save();
//            }
//        });
//    }

    public CommandsCustom(TwitchChannel channel)
    {
        this.channel = channel;
    }

    public void load() {
        System.out.println("Loading custom commands...");
//        try (FileInputStream reader = new FileInputStream("commands.ser"); ObjectInputStream in = new ObjectInputStream(reader)) {
//            commandList = (HashMap) in.readObject();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try (FileReader fr = new FileReader(channel.getDir() + "commands.txt"); BufferedReader br = new BufferedReader(fr))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] splitLine = line.split(" ", 2);
                if (splitLine.length == 2)
                {
                    commandList.put(splitLine[0], splitLine[1]);
                }
            }
        } catch (IOException ex)
        {
            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save() {
        System.out.println("Saving custom commands...");
//        try (FileOutputStream writer = new FileOutputStream("commands.ser"); ObjectOutputStream out = new ObjectOutputStream(writer)) {
////            out.writeObject(commandList);
//            
//        } catch (IOException ex) {
//            Logger.getLogger(Quotes.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try (FileWriter writer = new FileWriter(channel.getDir() + "commands.txt"))
        {
            for (Map.Entry<String, String> entry : commandList.entrySet())
            {
                writer.write(entry.getKey() + " " + entry.getValue() + "\r\n");
            }
        } catch (IOException ex)
        {
            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param key
     * @param value
     * @return true if key was added, false if key was overwritten
     */
    public void add(String key, String value) {
        if (!key.startsWith("!"))
            key = "!" + key;
        commandList.put(key, value);
        save();
    }
    
    public boolean remove(String key) {
        if (!key.startsWith("!"))
            key = "!" + key;
        return commandList.remove(key) != null;
    }
    
    public boolean exists(String key) {
        if (key.startsWith("!"))
            return commandList.containsKey(key);
        else
            return commandList.containsKey("!" + key);
    }
    
    public String get(String key) {
        if (key.startsWith("!"))
            return commandList.get(key);
        else
            return commandList.get("!" + key);
    }
    
    public String getList() {
        StringBuilder sb = new StringBuilder(100);
        Iterator<String> iter = commandList.keySet().iterator();
        for (int i = 0; i < commandList.size(); i++) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    
}
