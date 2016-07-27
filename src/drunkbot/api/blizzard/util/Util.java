package drunkbot.api.blizzard.util;

/**
 * Created by Kevin on 26/07/2016.
 */
public class Util
{
    public static String bTagToURL(String battleTag)
    {
        return battleTag.replace("#", "-");
    }
}
