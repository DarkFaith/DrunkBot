package drunkbot;

import drunkbot.cmd.CommandsCustom;
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.LogUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kevin on 19/08/2016.
 */
public class MessageManager extends ScheduledManager
{
    ArrayList<String> messageList = new ArrayList<>();
    int lastMessageIndex = -1;

    public MessageManager(TwitchChannel channel)
    {
        super(channel);
    }

    /**
     *
     * @param commandKey message command to add to the list of messages displayed at an interval (ex: !social)
     * @return whether command was added successfully
     */
    public boolean addCommand(String commandKey)
    {
        if (!commandKey.startsWith("!"))
            commandKey = "!" + commandKey;
        boolean added = messageList.add(commandKey);
        save();
        return added;
    }

    public boolean removeCommand(String commandKey)
    {
        if (!commandKey.startsWith("!"))
            commandKey = "!" + commandKey;
        boolean removed = messageList.remove(commandKey);
        save();
        return removed;
    }

    @Override
    protected void onScheduledRun()
    {
        String message = getNextMessage();
        if (message != null)
        {
            getTwitchChannel().sendMessage(message);
        } else {
            LogUtils.logErr("Timer message was null");
        }
    }

    protected String getNextMessage() {
        if (messageList.isEmpty())
            return null;

        CommandsCustom commandsCustom = getTwitchChannel().getCustomCommands();
        lastMessageIndex++;
        if (lastMessageIndex > messageList.size() - 1)
            lastMessageIndex = 0;

        return commandsCustom.get(messageList.get(lastMessageIndex));
    }

    @Override
    public boolean load()
    {
        try (FileReader fileReader = new FileReader(getSaveLocation() + "Messages.txt");
             BufferedReader br = new BufferedReader(fileReader))
        {
            String msg;
            while ((msg = br.readLine()) != null) {
                if (!msg.isEmpty()) {
                    messageList.add(msg);
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean save()
    {
        if (messageList.isEmpty())
            return false;
        try (FileWriter fileWriter = new FileWriter(getSaveLocation() + "Messages.txt");
             BufferedWriter bw = new BufferedWriter(fileWriter))
        {

            for (String msg : messageList) {
                if (msg != null)
                {
                    bw.write(msg);
                    bw.newLine();
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getList() {
        StringBuilder sb = new StringBuilder(100);
        Iterator<String> iter = messageList.iterator();
        for (int i = 0; i < messageList.size(); i++) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
