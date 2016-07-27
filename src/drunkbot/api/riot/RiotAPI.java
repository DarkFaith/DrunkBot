/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot.api.riot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import drunkbot.CommandsCustom;
import drunkbot.api.API;
import drunkbot.api.Storable;
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.Globals;
import drunkbot.twitchai.util.StringUtils;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.QueueType;
import net.rithms.riot.constant.Region;
import net.rithms.riot.constant.Season;
import net.rithms.riot.dto.League.League;
import net.rithms.riot.dto.League.LeagueEntry;

/**
 *
 * @author Kevin
 */
public abstract class RiotAPI extends API implements Storable
{
    RiotApi api = new RiotApi(Globals.g_api_riot_oauth);
    ArrayList<String> accountList = new ArrayList<>(5);
    ArrayList<CachedSummoner> cachedSummoners = new ArrayList<>();
    //Map<String, Summoner> summoners;
    Map<String, List<League>> leagueMap;

    public RiotAPI(TwitchChannel channel)
    {
        super(channel);
        api.setRegion(Region.NA);
    }

    public void init()
    {
        setUpdateInverval(1000 * 60 * 30);
    }

    public boolean load() {
        System.out.println("Loading accounts from: " + getChannel().getDir() + "accounts.txt");
        try (FileReader fr = new FileReader(getChannel().getDir() + "accounts.txt"); BufferedReader br = new BufferedReader(fr))
        {
            String accountName;
            while ((accountName = br.readLine()) != null)
            {
                if (!accountList.contains(accountName))
                {
                    accountList.add(accountName);
                }
            }
            init();
            return true;
        } catch (IOException ex)
        {
            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean save() {
        try (FileWriter writer = new FileWriter("accounts.txt"))
        {
            for (String accountName : accountList)
            {
                writer.write(accountName + "\r\n");
            }
            return true;
        } catch (IOException ex)
        {
            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public int addAccount(String accountName)
    {
        if (!accountList.contains(accountName))
        {
            accountList.add(accountName);
            return 1;
        } else {
            return -1;
        }
    }
    
    public String getRank(String summonerID)
    {
        if (!accountList.contains(summonerID))
            return "Summoner rank not available";
        if (leagueMap == null)
        {
            doUpdate(true);
        }
        List<League> summonerLeagueList = leagueMap.get(summonerID);

        for (League league : summonerLeagueList)
        {
            System.out.println("Loading rank for: " + league.getName());
            if (league.getQueue().equals(QueueType.RANKED_SOLO_5x5.name()))
            {
                List<LeagueEntry> entryList = league.getEntries();
                for (LeagueEntry entry : entryList)
                {
                    if (entry.getPlayerOrTeamId().equals(summonerID))
                    {
                        //String tier = StringUtils.toTitleCase(league.getTier());
                        return league.getTier() + " " + entry.getDivision() + " " + entry.getLeaguePoints() + "LP";
                    }
                }
                break;
            }
        }
        return "Failed to find";
    }

    public String getHighestRank()
    {
        ArrayList<String> accountRanks = new ArrayList<>(accountList.size());
        for (String id : accountList) {
            accountRanks.add(getRank(id));
        }

        String highestRank = "BRONZE V 0";
        //String highestRank = "CHALLENGER I 0";
        for (String rank : accountRanks) {
            int rankCompare = Util.compareRanks(highestRank, rank);
            if (rankCompare < 0)
                highestRank = rank;
        }

        return highestRank;
    }

    @Override
    protected boolean update()
    {
        try
        {
            System.out.println("Updating Riot API...");
            //summoners = api.getSummonersByName(Region.NA, (String[]) accountList.toArray());
            //api.setSeason(Season.SEASON2016);
            leagueMap = api.getLeagueBySummoners(accountList.toArray(new String[accountList.size()]));
            setLastUpdateTime();
            return true;
        } catch (Exception ex)
        {
            System.out.println(ex);
            Logger.getLogger(RiotAPI.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
