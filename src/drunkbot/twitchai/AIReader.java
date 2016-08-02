package drunkbot.twitchai;

import drunkbot.Quotes;
import drunkbot.twitchai.bot.TwitchAI;
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.bot.TwitchUser;
import drunkbot.twitchai.util.Globals;
import drunkbot.twitchai.util.LogUtils;
import org.jibble.pircbot.IrcException;
import org.omg.SendingContext.RunTime;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Kevin on 01/08/2016.
 */
public class AIReader
{
    private TwitchAI twitchAI = null;
    private TwitchChannel twitchChannel = null;
    private String hostNameSuffix = ".tmi.twitch.tv";
    private Thread readerThread;

    public AIReader(TwitchAI twitchAI) {
        this.twitchAI = twitchAI;
    }

    public AIReader(TwitchAI twitchAI, TwitchChannel channel) {
        this(twitchAI);
        setTwitchChannel(channel);
    }

    public void setTwitchChannel(TwitchChannel channel) {
        this.twitchChannel = channel;
    }

    public TwitchChannel getTwitchChannel() {
        return twitchChannel;
    }

    public void init_input_reader() {
        readerThread = new Thread(new Runnable() {
            Scanner scan = new Scanner(System.in);

            @Override
            public void run()
            {
                String message;
                String[] splitMessage;
                String command;
                String option;
                while (true) {
                    if (scan.hasNextLine()) {
                        message = scan.nextLine();

                        // Execute commands via command prompt
                        if (message.startsWith("//")) {
                            command = message.replaceFirst("//", "!");
                            // Forward message to be treated as a standard "!" message in chat
                            twitchAI.onMessage(twitchChannel.getName(), Globals.g_bot_name, Globals.g_bot_name, Globals.g_bot_name + hostNameSuffix, command);
                            // Send message as DrunkevBot in chat
                        } else if (message.startsWith("/")) {
                            command = message.replaceFirst("/", "");
                            processCommand(command);
                        } else {
                            twitchChannel.sendMessage(message);
                        }
                    }
                } // end while loop
            } // end run
        }); // end thread
        readerThread.start();
    }

    private void processCommand(String command) {
        switch (command) {
            case "save":
                twitchChannel.getQuotes().save();
                twitchChannel.getCustomCommands().save();
                twitchChannel.getCurrencyManager().save();
                break;

            case "exit":
                System.exit(0);
                break;
        }
    }
}
