/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drunkbot;

import drunkbot.twitchai.bot.TwitchChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Lagacé <kevlag100@hotmail.com>
 */
public class MediaReader
{
    private static final Logger LOG = Logger.getLogger(MediaReader.class.getName());
    
    private static final String PLACEHOLDER_SONG = "Darude Sandstorm Kappa";
    private String lastSong = PLACEHOLDER_SONG;
    private String currentSong = PLACEHOLDER_SONG;
    private String currentPlaylist = "No valid playlist detected";
    private long lastSongUpdate = 0;
    
    //private File songFile = new File(System.getProperty("user.home") + "\\Documents\\unp\\unp_now_playingunp_now_playing.txt");
    private File songFile;
    
    private BufferedReader songFileReader;
    private final ScheduledExecutorService songFileWatcher = Executors.newSingleThreadScheduledExecutor();
    TwitchChannel channel;
    public MediaReader(TwitchChannel channel)
    {
        this.channel = channel;

    }

    public void start() {
        songFile = new File(channel.getDir() + "Snip.txt");
        if (songFile.exists())
            songFileWatcher.scheduleWithFixedDelay(songFileRunnable, 0, 10, TimeUnit.SECONDS);
    }
    
    public void stop() {
        songFileWatcher.shutdown();
    }
    
    private final Runnable songFileRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            try {
                try {
                    songFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(songFile), StandardCharsets.UTF_8));
                } catch (FileNotFoundException fe) {
                    LOG.log(Level.INFO, "Song file not found (Snip.txt)", fe);
                }
               // try {
                    //playlistFileReader = new BufferedReader(new FileReader(playlistURL));
                //} catch (FileNotFoundException fe) {
                //}
                if (songFile.lastModified() > lastSongUpdate && songFileReader.ready()) {
                    lastSong = currentSong;
                    currentSong = songFileReader.readLine();
                    lastSongUpdate = songFile.lastModified();
                    //currentPlaylist = playlistFileReader.readLine();
                    LOG.log(Level.FINEST, "Song file updated");
                } else {
                    LOG.log(Level.FINEST, "Song file no updated needed. Using cached string");
                }
            } catch (IOException ex)
            {
                LOG.log(Level.SEVERE, "Error reading song file", ex);
            }
        }
    };
    
    public String getCurrentSong() {
        return currentSong;
    }
    
    public String getLastSong() {
        return lastSong;
    }
    
    public String getPlaylistURL() {
        return currentPlaylist;
    }
}
