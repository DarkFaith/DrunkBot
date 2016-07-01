/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot.api.riot;

import net.rithms.riot.dto.League.League;
import net.rithms.riot.dto.Summoner.Summoner;

/**
 *
 * @author Kevin
 */
public class CachedSummoner extends Summoner
{
    League league = new League();
    
    public CachedSummoner()
    {
    }
    
    public League getLeague()
    {
        return league;
    }
    
    public void setLeague(League league)
    {
        this.league = league;
    }
}
