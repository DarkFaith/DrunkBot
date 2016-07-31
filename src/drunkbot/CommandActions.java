package drunkbot;

/**
 * Created by Kevin on 24/06/2016.
 */
public class CommandActions
{
    public static String getRunes(String userName) {
        if (userName.equalsIgnoreCase("Darkfaith")) {
            return "DarkFaith: HTTP_ASYNC://www.lolking.net/summoner/na/20445322#runes";
        } else if (userName.equalsIgnoreCase("Drunkev")) {
            return "Drunkev: HTTP_ASYNC://www.lolking.net/summoner/na/46120915#runes";
        } else {
            return getRunes();
        }
    }

    public static String getRunes() {
        return "DarkFaith: HTTP_ASYNC://www.lolking.net/summoner/na/20445322#runes         "
                + "Drunkev: HTTP_ASYNC://www.lolking.net/summoner/na/46120915#runes";
    }
}
