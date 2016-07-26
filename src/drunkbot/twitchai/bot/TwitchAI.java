package drunkbot.twitchai.bot;

import static drunkbot.twitchai.util.Globals.*;
import static drunkbot.twitchai.util.LogUtils.logMsg;
import static drunkbot.twitchai.util.LogUtils.logErr;
import static drunkbot.twitchai.util.GenUtils.exit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import drunkbot.*;
import drunkbot.twitchai.util.Globals;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import drunkbot.twitchai.util.FileUtils;

public class TwitchAI extends PircBot
{

    private float                    m_cycleTime;
    private float                    m_cmdTime;
    private boolean                  m_hasMembership;
    private boolean                  m_hasCommands;
    private boolean                  m_hasTags;
    private ArrayList<TwitchUser>    m_moderators;
    private ArrayList<TwitchChannel> m_channels;

    public TwitchAI()
    {
        m_cycleTime = 0.0f;
        m_cmdTime = 0.0f;
        m_hasMembership = false;
        m_hasCommands = false;
        m_hasTags = true;
        m_moderators = new ArrayList<TwitchUser>();
        m_channels = new ArrayList<TwitchChannel>();

        setName(g_bot_name);
        setVersion(g_lib_version);
        setVerbose(false);
    }

    public void init_twitch()
    {
        logMsg("Loading all registered DrunkevBot moderators...");
        ArrayList<String> loadedModerators = FileUtils.readTextFile("data/moderators.txt");
        for (String m : loadedModerators)
        {
            String[] m_split = m.split(" ");
            TwitchUser newmod = new TwitchUser(m_split[0], m_split[1]);
            logMsg("Added a DrunkevBot moderator (" + newmod + ") to m_moderators");
            m_moderators.add(newmod);
        }

        logMsg("Attempting to connect to irc.twitch.tv...");
        try
        {
            connect("irc.twitch.tv", 6667, g_bot_oauth);
        } catch (IOException | IrcException e)
        {
            logErr(e.getStackTrace().toString());
            exit(1);
        }

        if (g_bot_reqMembership)
        {
            logMsg("Requesting twitch membership capability for NAMES/JOIN/PART/MODE messages...");
            sendRawLine(g_server_memreq);
        }
        else
        {
            logMsg("Membership request is disabled!");
            m_hasMembership = true;
        }

        if (g_bot_reqCommands)
        {
            logMsg("Requesting twitch commands capability for NOTICE/HOSTTARGET/CLEARCHAT/USERSTATE messages... ");
            sendRawLine(g_server_cmdreq);
        }
        else
        {
            logMsg("Commandsold request is disabled!");
            m_hasCommands = true;
        }

        if (g_bot_reqTags)
        {
            logMsg("Requesting twitch tags capability for PRIVMSG/USERSTATE/GLOBALUSERSTATE messages... ");
            sendRawLine(g_server_tagreq);
        }
        else
        {
            logMsg("Tags request is disabled!");
            m_hasTags = true;
        }
    }

    public void init_channels()
    {
        logMsg("Attempting to join all registered channels...");
        ArrayList<String> loadedChannels = FileUtils.readTextFile("data/channels.txt");
        for (String c : loadedChannels)
        {
            if (!c.startsWith("#"))
            {
                c = "#" + c;
            }
            joinToChannel(c);
        }
    }

    public void joinToChannel(final String channel)
    {
        logMsg("Attempting to join channel " + channel);
        joinChannel(channel);
        m_channels.add(new TwitchChannel(channel)
        {

            @Override
            public void sendMessage(String message)
            {
                sendTwitchMessage(channel, message);
            }
        });
        FileUtils.directoryExists("data/channels/" + channel);
    }

    public void partFromChannel(String channel)
    {
        logMsg("Attempting to part from channel " + channel);
        partChannel(channel);
        m_channels.remove(getTwitchChannel(channel));
    }

    public void addChannel(String channel, String sender, String addChan)
    {
        ArrayList<String> addchan_channels = FileUtils.readTextFile("data/channels.txt");
        if (addchan_channels.size() <= 0 || !addchan_channels.contains(addChan))
        {
            logMsg("Registering a new channel: " + addChan);
            sendTwitchMessage(channel, "Registering a new channel: " + addChan);
            FileUtils.writeToTextFile("data/", "channels.txt", addChan);
            joinToChannel(addChan);
        }
        else
        {
            logErr("Failed to register a new channel: " + addChan);
            sendTwitchMessage(channel, "That channel is already registered!");
        }
        return;
    }

    public void delChannel(String channel, String sender, String delChan)
    {
        if (!Arrays.asList(getChannels()).contains(delChan))
        {
            logErr("Can't delete channel " + delChan + " from the global channels list because it isn't in the joined channels list!");
            return;
        }
        logMsg(sender + " Requested a deletion of channel: " + delChan);
        sendTwitchMessage(channel, sender + " Requested a deletion of channel: " + delChan);
        partFromChannel(delChan);
        FileUtils.removeFromTextFile("data", "/channels.txt", delChan);
    }

    public void sendTwitchMessage(String channel, String message)
    {
        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(g_bot_name);

        if (twitch_user == null)
        {
            twitch_user = g_nulluser;
        }

        if (twitch_user.isOperator())
        {
            if (twitch_channel.getCmdSent() <= 48)
            {
                twitch_channel.setCmdSent(twitch_channel.getCmdSent() + 1);
                sendMessage(channel, message);
            }
            else
            {
                logErr("Cannot send a message to channel (" + twitch_channel + ")! 100 Messages per 30s limit nearly exceeded! (" + twitch_channel.getCmdSent() + ")");
            }
        }
        else
        {
            if (twitch_channel.getCmdSent() <= 16)
            {
                twitch_channel.setCmdSent(twitch_channel.getCmdSent() + 1);
                sendMessage(channel, message);
            }
            else
            {
                logErr("Cannot send a message to channel (" + twitch_channel + ")! 20 Messages per 30s limit nearly exceeded! (" + twitch_channel.getCmdSent() + ")");
            }
        }
    }

    @Override
    public void handleLine(String line)
    {
        logMsg("handleLine | " + line);

        super.handleLine(line);

        if (!isInitialized())
        {
            if (line.equals(g_server_memans))
            {
                m_hasMembership = true;
            }

            if (line.equals(g_server_cmdans))
            {
                m_hasCommands = true;
            }

            if (line.equals(g_server_tagans))
            {
                m_hasTags = true;
            }
        }

        if (line.contains(":jtv "))
        {
            line = line.replace(":jtv ", "");
            String[] line_array = line.split(" ");

            if (line_array[0].equals("MODE") && line_array.length >= 4)
            {
                onMode(line_array[1], line_array[3], line_array[3], "", line_array[2]);
            }
        }
    }

    @Override
    public void onUserList(String channel, User[] users)
    {
        super.onUserList(channel, users);

        TwitchChannel twitch_channel = getTwitchChannel(channel);

        if (twitch_channel == null)
        {
            logErr("Error on USERLIST, channel (" + channel + ") doesn't exist!");
            return;
        }

        for (User u : users)
        {
            if (twitch_channel.getUser(u.getNick()) == null)
            {
                TwitchUser twitch_mod = getOfflineModerator(u.getNick());
                String prefix = "";
                if (twitch_mod != null)
                {
                    prefix = twitch_mod.getPrefix();
                }
                TwitchUser user = new TwitchUser(u.getNick(), prefix);
                twitch_channel.addUser(user);
                logMsg("Adding new user (" + user + ") to channel (" + twitch_channel.toString() + ")");
            }
        }
    }

    @Override
    public void onJoin(String channel, String sender, String login, String hostname)
    {
        super.onJoin(channel, sender, login, hostname);

        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sender);
        TwitchUser twitch_mod = getOfflineModerator(sender);

        if (twitch_channel != null && twitch_user == null)
        {
            String prefix = "";
            if (twitch_mod != null)
            {
                prefix = twitch_mod.getPrefix();
            }
            TwitchUser user = new TwitchUser(sender, prefix);
            twitch_channel.addUser(user);
            logMsg("Adding new user (" + user + ") to channel (" + twitch_channel.toString() + ")");
        }
    }

    @Override
    public void onPart(String channel, String sender, String login, String hostname)
    {
        super.onPart(channel, sender, login, hostname);

        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sender);

        if (twitch_channel != null && twitch_user != null)
        {
            twitch_channel.delUser(twitch_user);
            logMsg("Removing user (" + twitch_user + ") from channel (" + twitch_channel.toString() + ")");
        }
    }

    @Override
    public void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode)
    {
        super.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);

        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sourceNick);

        if (twitch_user == null)
        {
            logErr("Error on MODE, cannot find (" + twitch_user + ") from channel (" + twitch_channel.toString() + ")");
            return;
        }

        if (mode.equals("+o"))
        {
            logMsg("Adding +o MODE for user (" + twitch_user + ") in channel (" + twitch_channel.toString() + ")");
            twitch_user.addPrefixChar("@");
        }
        else if (mode.equals("-o"))
        {
            logMsg("Adding -o MODE for user (" + twitch_user + ") in channel (" + twitch_channel.toString() + ")");
            twitch_user.delPrefixChar("@");
        }
    }


    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        logMsg("data/channels/" + channel + "/logs/", "/onMessage", "User: " + sender + " Hostname: " + hostname + " Message: " + message);

        TwitchChannel twitch_channel = getTwitchChannel(channel);

        /*
         * Handle all chat commands
         */
        if (message.startsWith("!"))
        {
            TwitchUser twitch_user = twitch_channel.getUser(sender);

//            if (twitch_user == null)
//            {
//                logErr("Error on ONMESSAGE, user (" + sender + ") doesn't exist! Creating a temp null user object for user!");
//                twitch_user = g_nulluser;
//            }

//            if (message.length() > 3)
//            {
//                if (twitch_user.getCmdTimer() > 0)
//                {
//                    if (twitch_user.getCmdTimer() > 10 && twitch_channel.getCmdSent() < 32)
//                    {
//                        sendTwitchMessage(channel, twitch_user + " Please wait " + twitch_user.getCmdTimer() + " seconds before sending a new command.");
//                    }
//                    twitch_user.setCmdTimer(twitch_user.getCmdTimer() + 5);
//                    return;
//                }
//                else
//                {
//                    if (!twitch_user.getName().equals("null"))
//                    {
//                        twitch_user.setCmdTimer(5);
//                    }
//                }
//            }

            message = message.replaceFirst("!", "");
            String[] msg_array = message.split(" ");
            String msg_command = msg_array[0];
            String user_sender = sender;
            String user_target;
            String chan_sender = channel;
            String chan_target;
            boolean senderIsMod = twitch_channel.getOperator(sender) != null;
            float time;
            long timeStart, timeEnd;

            timeStart = System.nanoTime();

//            /*
//             * Commands available on the bot's own channel
//             */
//            if (channel.equals(g_bot_chan))
//            {
//                switch (msg_command)
//                {
//                    case "help":
//                        sendTwitchMessage(channel, "List of available commands on this channel: " + g_commands_bot);
//                        break;
//                    case "register":
//                        addChannel(channel, g_bot_name, "#" + user_sender);
//                        break;
//                    case "unregister":
//                        delChannel(channel, g_bot_name, "#" + user_sender);
//                        break;
//                }
//            }

            /*
             * Commands available on channel
             */
            switch (msg_command)
            {

                case (Command.LAST_SONG):
                    sendTwitchMessage(channel, twitch_channel.getMediaReader().getLastSong());
                    break;

                case (Command.SONG):
                    sendTwitchMessage(channel, twitch_channel.getMediaReader().getCurrentSong());
                    break;

                case (Command.HELP):
                    sendTwitchMessage(channel, "Commands=https://bitbucket.org/DarkFaith/drunkbot/wiki/Commands");
                    break;

                case (Command.RUNES):
                    if (msg_array.length > 1)
                        sendTwitchMessage(channel, CommandActions.getRunes(msg_array[1]));
                    else
                        sendTwitchMessage(channel, CommandActions.getRunes());
                    break;

                case (Command.QUOTE):
                    Quotes quotes = twitch_channel.getQuotes();
                    boolean notAnInteger = false;
                    // no param
                    if (msg_array.length == 1)
                    {
                        sendTwitchMessage(channel, quotes.getRandom());
                    }
                    // integer param (quote index)
                    else
                    {
                        try
                        {
                            int quoteIndex = Integer.parseInt(msg_array[1]);
                            String quoteString = quotes.get(quoteIndex);
                            if (!quoteString.isEmpty())
                            {
                                sendTwitchMessage(channel, quoteString);
                            }
                        } catch (NumberFormatException ex)
                        {
                            notAnInteger = true;
                        }

                        // string param (mod only)
                        if (notAnInteger && senderIsMod)
                        {
                            if (msg_array[1].equals("add"))
                            {
                                StringBuilder quoteBuilder = new StringBuilder();
                                for (int i = 2; i < msg_array.length; i++)
                                {
                                    quoteBuilder.append(msg_array[i]);
                                }
                                if (quoteBuilder.length() >= 1)
                                {
                                    if (quotes.add(quoteBuilder.toString()))
                                    {
                                        sendTwitchMessage(channel, "Quote added [" + quotes.getNumQuotes() + "]." +
                                                "Use \"!quote\" to get a random quote or \"!quote #\" to get a specific quote");
                                    }
                                }
                            } else if (msg_array[1].equals("remove"))
                            {
                                try
                                {
                                    if (msg_array.length == 3)
                                    {
                                        int quoteIndex = Integer.parseInt(msg_array[2]);
                                        String quoteToRemove = quotes.get(quoteIndex);
                                        quotes.remove(quoteIndex);
                                        sendTwitchMessage(channel, "Removed quote: " + quoteToRemove);

                                    } else
                                    {
                                        sendTwitchMessage(channel, "Proper syntax is !quote remove index");
                                    }
                                } catch (NumberFormatException ex)
                                {
                                    sendTwitchMessage(channel, "Proper syntax is !quote remove index");
                                }
                            } else
                            {
                                sendTwitchMessage(channel, "You're too drunk to type commands correctly. Here's a complimentary quote instead:");
                                sendTwitchMessage(channel, quotes.getRandom());
                            }
                        }
                    }
                    break;

                case Command.LIST_COMMANDS:
                    String commandList = twitch_channel.getCustomCommands().getList();
                    final int MAX_MSG_SIZE = 487;
                    if (commandList.length() > MAX_MSG_SIZE)
                    {
                        String[] cmdParts = commandList.split(" ");
                        StringBuilder builder = new StringBuilder(MAX_MSG_SIZE);
                        builder.append("Commands are: ");
                        for (int i = 0; i < cmdParts.length; i++)
                        {
                            int cmdLength = cmdParts[i].length() + 1;
                            if (cmdLength > MAX_MSG_SIZE - builder.length())
                            {
                                sendTwitchMessage(channel, builder.toString());
                                builder = new StringBuilder(MAX_MSG_SIZE);
                            } else {
                                builder.append(cmdParts[i]).append(" ");
                            }
                        }
                        if (builder.length() > 1)
                        {
                            sendTwitchMessage(channel, builder.toString());
                        }
                    } else
                    {
                        sendTwitchMessage(channel, "Commands are: " + twitch_channel.getCustomCommands().getList());
                    }
                    break;

                case Command.MODS:
                    StringBuilder chanMods = new StringBuilder();
                    chanMods.append("Mods are: ");
                    for (TwitchUser user : twitch_channel.getOperators())
                    {
                        chanMods.append(user.getName()).append(", ");
                    }
                    sendTwitchMessage(channel, chanMods.toString());
                    break;

                case Command.UPTIME:
                    twitch_channel.sendUpTime();
                    //sendTwitchMessage(channel, twitch_channel.sendUpTime());
                    break;

                case Command.CURRENT_GAME:
                    sendTwitchMessage(channel, twitch_channel.getTwitchAPI().getCurrentGame());
                    break;

                case Command.RANK:
                    //TODO: Remove hardcoded rank
                    //sendTwitchMessage(channel, twitch_channel.getRiotAPI().getRank("20445322"));
                    sendTwitchMessage(channel, twitch_channel.getRiotAPI().getHighestRank());
//                    if (twitch_channel.getCustomCommands().exists("!rank"))
//                    {
//                        sendTwitchMessage(channel, twitch_channel.getCustomCommands().get("!rank"));
//                    }
                    break;
                case Command.CURRENCY:
                    Currency userCurrency = twitch_channel.getCurrencyManager().getCurrency(user_sender);
                    double currencyValue;
                    if (userCurrency == null)
                        currencyValue = 0;
                    else
                        currencyValue = userCurrency.get();
                    String currencyString = Globals.g_currencyFormat.format(currencyValue);
                    sendTwitchMessage(channel, user_sender + " has " + currencyString + " souls.");
                    break;
                // Mod Commands
                case ModCommand.PURGE:
                    if (!senderIsMod)
                    {
                        break;
                    }
                    if (msg_array.length == 2)
                    {
                        String user = msg_array[1];
                        ModCommandActions.purge(channel, user);
                    }
                    break;

                case ModCommand.COMMAND:
                    if (!senderIsMod)
                    {
                        break;
                    }
                    if (msg_array.length == 3)
                    {
                        String action = msg_array[1];
                        String msgCmd = msg_array[2];
                        if (action.equals("remove") || action.equals("delete") || action.equals("del"))
                        {
                            if (twitch_channel.getCustomCommands().remove(msgCmd))
                            {
                                sendTwitchMessage(channel, "Removed command: " + msgCmd);
                            } else
                            {
                                sendTwitchMessage(channel, "That shitty \"" + msgCmd + "\" command doesn't exist m8.");
                            }
                        }
                        // !command add [cmd] [msg]
                    } else if (msg_array.length > 3)
                    {
                        String action = msg_array[1];
                        String msgCmd = msg_array[2];
                        if (action.equals("add") || action.equals("update"))
                        {
                            StringBuilder cmdTextBuilder = new StringBuilder();
                            for (int i = 3; i < msg_array.length; i++)
                            {
                                cmdTextBuilder.append(msg_array[i]);
                            }
                            String cmdText = cmdTextBuilder.toString();

                            CommandsCustom customCommands = twitch_channel.getCustomCommands();
                            boolean exists = customCommands.exists(msgCmd);
                            customCommands.add(msgCmd, cmdText);
                            if (exists)
                            {
                                sendTwitchMessage(channel, "Updated command: " + msgCmd);
                            } else if (action.equals("add"))
                            {
                                sendTwitchMessage(channel, "Added new command: " + msgCmd);
                            } else
                            {
                                sendTwitchMessage(channel, "Command \"" + msgCmd + "\" doesn't exist.");
                            }
                        }
                    }
                    break;

                default:
                {
                    CommandsCustom customCmds = twitch_channel.getCustomCommands();
                    if (customCmds.exists(msg_command))
                    {
                        sendTwitchMessage(channel, customCmds.get(msg_command));
                    }
                }
                break;
            }



//            /*
//             * Commands available on all channels
//             */
//            switch (msg_command)
//            {
//
//            /*
//             * Normal channel user commands below
//             */
//                case "help":
//                    String help_text = "List of available commands to you: " + g_commands_user;
//
//                    if (twitch_user.isOperator())
//                    {
//                        help_text += " " + g_commands_op;
//                    }
//
//                    if (twitch_user.isModerator())
//                    {
//                        help_text += " " + g_commands_mod;
//                    }
//
//                    if (twitch_user.isAdmin())
//                    {
//                        help_text += " " + g_commands_admin;
//                    }
//
//                    sendTwitchMessage(channel, help_text);
//                    break;
//
//                case "info":
//                    sendTwitchMessage(channel, "Language: Java Core: " + g_bot_version + " Library: " + getVersion());
//                    break;
//
//                case "performance":
//                    sendTwitchMessage(channel, "My current main loop cycle time: " + m_cycleTime + "ms. My current cmd loop cycle time: " + m_cmdTime + "ms.");
//                    break;
//
//                case "date":
//                    sendTwitchMessage(channel, g_dateformat.format(g_date));
//                    break;
//
//                case "time":
//                    sendTwitchMessage(channel, g_timeformat.format(g_date));
//                    break;
//
//                case "users":
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Users in this channel: " + twitch_channel.getUsers().size());
//                        break;
//                    }
//
//                    if (msg_array[1].equals("all"))
//                    {
//                        sendTwitchMessage(channel, "Users in all channels: " + getAllUsers().size());
//                        break;
//                    }
//
//                    chan_target = msg_array[1];
//
//                    if (!chan_target.startsWith("#"))
//                    {
//                        chan_target = "#" + chan_target;
//                    }
//
//                    TwitchChannel users_channel = getTwitchChannel(chan_target);
//
//                    if (users_channel == null)
//                    {
//                        logErr("Error on !users channel, channel (" + chan_target + ") doesn't exist!");
//                        break;
//                    }
//
//                    sendTwitchMessage(channel, "Users in channel (" + users_channel + "): " + users_channel.getUsers().size());
//                    break;
//
//                case "ops":
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Operators in this channel: " + twitch_channel.getOperators());
//                        break;
//                    }
//
//                    if (msg_array[1].equals("all"))
//                    {
//                        sendTwitchMessage(channel, "Operators in all channels: " + getAllOperators().size());
//                        break;
//                    }
//
//                    chan_target = msg_array[1];
//
//                    if (!chan_target.startsWith("#"))
//                    {
//                        chan_target = "#" + chan_target;
//                    }
//
//                    TwitchChannel ops_channel = getTwitchChannel(chan_target);
//
//                    if (ops_channel == null)
//                    {
//                        logErr("Error on !ops channel, channel (" + chan_target + ") doesn't exist!");
//                        break;
//                    }
//
//                    sendTwitchMessage(channel, "Operators in channel (" + ops_channel + "): " + ops_channel.getOperators());
//                    break;
//
//                case "mods":
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "DrunkevBot Moderators in this channel: " + twitch_channel.getModerators());
//                        break;
//                    }
//
//                    if (msg_array[1].equals("all"))
//                    {
//                        sendTwitchMessage(channel, "DrunkevBot Moderators: " + getOfflineModerators());
//                        break;
//                    }
//
//                    chan_target = msg_array[1];
//
//                    if (!chan_target.startsWith("#"))
//                    {
//                        chan_target = "#" + chan_target;
//                    }
//
//                    TwitchChannel mods_channel = getTwitchChannel(chan_target);
//
//                    if (mods_channel == null)
//                    {
//                        logErr("Error on !mods channel, channel (" + chan_target + ") doesn't exist!");
//                        break;
//                    }
//
//                    sendTwitchMessage(channel, "DrunkevBot Moderators in channel (" + mods_channel + "): " + mods_channel.getModerators());
//                    break;
//
//                case "channel":
//                    sendTwitchMessage(channel, "Current channel info: " + twitch_channel);
//                    break;
//
//                case "channels":
//                    sendTwitchMessage(channel, "Registered channels: " + getTwitchChannels().size());
//                    break;
//
//                /*
//                 * Normal channel operator commands below
//                 */
//                case "permit":
//                    if (!twitch_user.isOperator())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 2)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !permit username true/false");
//                        break;
//                    }
//
//                    user_target = msg_array[1];
//
//                    TwitchUser permit_user = twitch_channel.getUser(user_target);
//                    if (permit_user == null)
//                    {
//                        logErr("Error on !permit user on channel (" + twitch_channel + ")! Target user (" + user_target + ") not found!");
//                        break;
//                    }
//
//                    if (msg_array[2].equals("true"))
//                    {
//                        sendTwitchMessage(channel, sender + " Gave " + permit_user + " a permission to post links!");
//                        permit_user.setUrlPermit(true);
//                    }
//                    else if (msg_array[2].equals("false"))
//                    {
//                        sendTwitchMessage(channel, sender + " Took " + permit_user + " permissions to post links!");
//                        permit_user.setUrlPermit(false);
//                    }
//                    break;
//
//                /*
//                 * Normal TwitchAI moderator commands below
//                 */
//                case "joinchan":
//                    if (!twitch_user.isModerator())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !joinchan channel");
//                        break;
//                    }
//
//                    if (!msg_array[1].startsWith("#"))
//                    {
//                        msg_array[1] = "#" + msg_array[1];
//                    }
//
//                    logMsg(sender + " Requested a join to channel: " + msg_array[1]);
//                    joinToChannel(msg_array[1]);
//                    break;
//
//                case "partchan":
//                    if (!twitch_user.isModerator())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !joinchan channel");
//                        break;
//                    }
//
//                    if (!msg_array[1].startsWith("#"))
//                    {
//                        msg_array[1] = "#" + msg_array[1];
//                    }
//
//                    if (!Arrays.asList(getChannels()).contains(msg_array[1]))
//                    {
//                        logErr("Can't part channel " + msg_array[1] + " because it isn't in the joined channels list!");
//                        break;
//                    }
//
//                    logMsg(sender + " Requested a quit from channel: " + msg_array[1]);
//                    partFromChannel(msg_array[1]);
//                    break;
//
//                case "addchan":
//                    if (!twitch_user.isModerator())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !addchan channel");
//                        break;
//                    }
//
//                    if (!msg_array[1].startsWith("#"))
//                    {
//                        msg_array[1] = "#" + msg_array[1];
//                    }
//
//                    addChannel(channel, sender, msg_array[1]);
//                    break;
//
//                case "delchan":
//                    if (!twitch_user.isModerator())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !delchan channel");
//                        break;
//                    }
//
//                    if (!msg_array[1].startsWith("#"))
//                    {
//                        msg_array[1] = "#" + msg_array[1];
//                    }
//
//                    delChannel(channel, sender, msg_array[1]);
//                    break;
//
//                /*
//                 * Normal TwitchAI admin commands below
//                 */
//                case "addmod":
//                    if (!twitch_user.isAdmin())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !addmod username");
//                        break;
//                    }
//
//                    user_target = msg_array[1];
//
//                    TwitchUser addmod_user = getOfflineModerator(user_target);
//                    if (addmod_user == null)
//                    {
//                        TwitchUser moderator = new TwitchUser(user_target, "*");
//                        m_moderators.add(moderator);
//                        twitch_channel.getUser(user_target).addPrefixChar("*");
//                        FileUtils.writeToTextFile("data/", "moderators.txt", user_target + " *");
//                        sendTwitchMessage(channel, sender + " Added a new moderator: " + moderator);
//                        logMsg(sender + " Added a new moderator: " + moderator);
//                    }
//                    else
//                    {
//                        logErr(sender + " Tried to add " + addmod_user + " as a moderator, but the user already is a moderator.");
//                        sendTwitchMessage(channel, addmod_user + " Already is a moderator!");
//                    }
//                    break;
//
//                case "delmod":
//                    if (!twitch_user.isAdmin())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !delmod username");
//                        break;
//                    }
//
//                    user_target = msg_array[1];
//
//                    TwitchUser delmod_user = getOfflineModerator(user_target);
//                    if (delmod_user != null)
//                    {
//                        m_moderators.remove(delmod_user);
//                        twitch_channel.getUser(user_target).delPrefixChar("*");
//                        FileUtils.removeFromTextFile("data/", "moderators.txt", user_target + " " + delmod_user.getPrefix());
//                        sendTwitchMessage(channel, sender + " Removed a moderator: " + delmod_user);
//                        logMsg(sender + " Removed a moderator: " + delmod_user);
//                    }
//                    else
//                    {
//                        logErr(sender + " Tried to remove a moderator: " + user_target + " that doesn't exist.");
//                        sendTwitchMessage(channel, sender + " Tried to remove a moderator: " + user_target + " that doesn't exist.");
//                    }
//                    break;
//
//                case "broadcast":
//                    if (!twitch_user.isAdmin())
//                    {
//                        break;
//                    }
//
//                    if (msg_array.length <= 1)
//                    {
//                        sendTwitchMessage(channel, "Wrong syntax! Usage: !broadcast message");
//                        break;
//                    }
//
//                    String broadcast_message = message.replace(msg_array[0], "");
//
//                    for (TwitchChannel c : m_channels)
//                    {
//                        logMsg("Sending a broadcast message to channel (" + c + ") Message: " + broadcast_message);
//                        sendTwitchMessage(c.getName(), "System broadcast message: " + broadcast_message);
//                    }
//                    break;
//            }

            timeEnd = System.nanoTime();
            time = (float) (timeEnd - timeStart) / 1000000.0f;

            setCmdTime(getCmdTime() * 0.1f + time * 0.9f);
        }
    }

    @Override
    public void onPrivateMessage(String sender, String login, String hostname, String message)
    {
        logMsg("data", "/privmsg", "User: " + sender + " Hostname: " + hostname + " Message: " + message);
    }

    public ArrayList<TwitchChannel> getTwitchChannels()
    {
        return m_channels;
    }

    public TwitchChannel getTwitchChannel(String name)
    {
        TwitchChannel result = null;

        for (TwitchChannel tc : m_channels)
        {
            if (tc.getName().equals(name))
            {
                result = tc;
                break;
            }
        }

        return result;
    }

    public ArrayList<TwitchUser> getAllUsers()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchChannel tc : m_channels)
        {
            result.addAll(tc.getUsers());
        }

        return result;
    }

    public ArrayList<TwitchUser> getAllOperators()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchChannel tc : m_channels)
        {
            result.addAll(tc.getOperators());
        }

        return result;
    }

    public ArrayList<TwitchUser> getOnlineModerators()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchChannel tc : m_channels)
        {
            result.addAll(tc.getModerators());
        }

        return result;
    }

    public ArrayList<TwitchUser> getOfflineModerators()
    {
        return m_moderators;
    }

    public TwitchUser getOfflineModerator(String nick)
    {
        TwitchUser result = null;

        for (TwitchUser tu : m_moderators)
        {
            if (tu.getName().equals(nick))
            {
                result = tu;
            }
        }

        return result;
    }

    public float getCycleTime()
    {
        return m_cycleTime;
    }

    public void setCycleTime(float cycleTime)
    {
        m_cycleTime = cycleTime;
    }

    public float getCmdTime()
    {
        return m_cmdTime;
    }

    public void setCmdTime(float cmdTime)
    {
        m_cmdTime = cmdTime;
    }

    public boolean isInitialized()
    {
        return m_hasMembership & m_hasCommands & m_hasTags;
    }

}
