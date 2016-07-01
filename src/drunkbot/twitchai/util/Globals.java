package drunkbot.twitchai.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import drunkbot.twitchai.bot.TwitchUser;

public class Globals
{

    // Config
    public static boolean          g_debug;
    public static boolean          g_bot_reqMembership;
    public static boolean          g_bot_reqCommands;
    public static boolean          g_bot_reqTags;
    public static String           g_bot_name;
    public static String           g_bot_oauth;
    public static String           g_bot_chan;
    public static String           g_bot_version    = "DrunkevBot 1.0.0";
    public static String           g_lib_version    = "PircBot 1.5.0";
    public static String           g_channel_base_dir    = System.getProperty("user.dir") + "/data/channels/";

    // APIS
    public static String           g_api_twitch_oauth;
    public static String           g_api_riot_oauth;

    // Time & Date
    public static DateFormat       g_datetimeformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static DateFormat       g_dateformat     = new SimpleDateFormat("dd.MM.yyyy");
    public static DateFormat       g_timeformat     = new SimpleDateFormat("HH:mm:ss");
    public static Date             g_date           = new Date();

    // Global variables
    public static final String     g_commands_user  = "!help !info !performance !date !time !users !ops !mods !channel !channels !slots";
    public static final String     g_commands_op    = "!permit";
    public static final String     g_commands_mod   = "!joinchan !partchan !addchan !delchan";
    public static final String     g_commands_admin = "!addmod !delmod ";
    public static final String     g_commands_bot   = "!help !register !unregister";
    public static final String[]   g_emotes_faces   = { "4Head", "BibleThump", "BloodTrail", "VaultBoy", "deIlluminati", "DOOMGuy", "FailFish", "Kappa", "Keepo" };

    // Server messages
    public static final String     g_server_memreq  = "CAP REQ :twitch.tv/membership";
    public static final String     g_server_memans  = ":tmi.twitch.tv CAP * ACK :twitch.tv/membership";
    public static final String     g_server_cmdreq  = "CAP REQ :twitch.tv/commands";
    public static final String     g_server_cmdans  = ":tmi.twitch.tv CAP * ACK :twitch.tv/commands";
    public static final String     g_server_tagreq  = "CAP REQ :twitch.tv/tags";
    public static final String     g_server_tagans  = ":tmi.twitch.tv CAP * ACK :twitch.tv/tags";

    // Java objects
    public static final TwitchUser g_nulluser       = new TwitchUser("null", "");

}
