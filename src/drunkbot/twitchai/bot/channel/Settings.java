package drunkbot.twitchai.bot.channel;

import java.io.*;
import java.util.Properties;

import static drunkbot.twitchai.util.LogUtils.logMsg;

/**
 * Created by Kevin on 24/07/2016.
 */
public class Settings
{
    Properties properties = new Properties();

    public boolean load(String fileLocation) throws IOException
    {

        try (FileInputStream fis = new FileInputStream(fileLocation + "settings.ini")) {
            properties.load(fis);
            //FileInputStream is = new FileInputStream();
        }
        return true;
    }

    public void save(String fileLocation) throws IOException
    {
        try (FileOutputStream fos = new FileOutputStream(fileLocation + "settings.ini")) {
            properties.store(fos, null);
            //FileInputStream is = new FileInputStream();
        }
    }
}
