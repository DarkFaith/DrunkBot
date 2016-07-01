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
import drunkbot.twitchai.bot.TwitchChannel;
import drunkbot.twitchai.util.Globals;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.League.League;
import net.rithms.riot.dto.League.LeagueEntry;

/**
 *
 * @author Kevin
 */
public class RiotAPI
{
    TwitchChannel channel;
    RiotApi api = new RiotApi(Globals.g_api_riot_oauth);
    ScheduledExecutorService updateRiotAPI = Executors.newSingleThreadScheduledExecutor();
    ArrayList<String> accountList = new ArrayList<>(5);
    ArrayList<CachedSummoner> cachedSummoners = new ArrayList<>();
    //Map<String, Summoner> summoners;
    Map<String, List<League>> leagueMap;
    
    Runnable updateRiotRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                System.out.println("Updating summoner ranks...");
                System.out.println("Accounts: " + accountList.get(0) + " " + accountList.get(1));
                //summoners = api.getSummonersByName(Region.NA, (String[]) accountList.toArray());
                leagueMap = api.getLeagueBySummoners((String[]) accountList.toArray());
                System.out.println("test");
                System.out.println(leagueMap.get(accountList.get(0)).get(4));
                
            } catch (RiotApiException ex)
            {
                System.out.println(ex);
                Logger.getLogger(RiotAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
    public RiotAPI(TwitchChannel channel)
    {
        this.channel = channel;
        api.setRegion(Region.NA);
    }
    

    public void load() {
        System.out.println("Loading accounts...");
        try (FileReader fr = new FileReader(channel.getDir() + "accounts.txt"); BufferedReader br = new BufferedReader(fr))
        {
            String accountName;
            while ((accountName = br.readLine()) != null)
            {
                if (!accountList.contains(accountName))
                {
                    accountList.add(accountName);
                }
            }
            updateRiotAPI.scheduleAtFixedRate(updateRiotRunnable, 0, 1, TimeUnit.HOURS);
        } catch (IOException ex)
        {
            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save() {
        try (FileWriter writer = new FileWriter("accounts.txt"))
        {
            for (String accountName : accountList)
            {
                writer.write(accountName + "\r\n");
            }
        } catch (IOException ex)
        {
            Logger.getLogger(CommandsCustom.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public String getRank(String summonerName)
    {
        if (!accountList.contains(summonerName))
            return "Summoner rank not available";
        if (leagueMap == null)
        {
            updateRiotAPI.execute(updateRiotRunnable);
            return "Fetching data from Riot API... Try again in a few minutes";
        }
        List<League> summonerLeagueList = leagueMap.get(summonerName);
        for (League league : summonerLeagueList)
        {
            if (league.getQueue().equals("RANKED_SOLO_5X5"))
            {
                List<LeagueEntry> entryList = league.getEntries();
                for (LeagueEntry entry : entryList)
                {
                    if (entry.getPlayerOrTeamId().equals(summonerName))
                    {
                        return entry.getDivision() + " " + entry.getLeaguePoints();
                    }
                    
                }
                break;
            }
        }
        return "Failed to find";
    }

    
}
