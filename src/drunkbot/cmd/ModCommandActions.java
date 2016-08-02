/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot.cmd;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Lagacé <kevlag100@hotmail.com>
 */
public class ModCommandActions
{
    private static final Logger LOG = Logger.getLogger(ModCommandActions.class.getName());

    public static String timeout(String channel, String user, int time) {
        LOG.log(Level.INFO, "kicked {0} for {1}", new Object[]{user, time});
        return "/timeout " + user + " " + time;
    }
    
    public static void purge(String channel, String user) {
        timeout(channel, user, 1);
    }

}
