package drunkbot.twitchai.util;

/**
 * Created by Kevin on 26/07/2016.
 */
public class StringUtils
{
    public static String toTitleCase(String s)
    {
        StringBuilder sb = new StringBuilder(s.length());
        if (s.length() > 0)
        {
            sb.append(Character.toUpperCase(s.charAt(0)));
            for (int i = 1; i < s.length(); i++)
            {
                sb.append(Character.toLowerCase(s.charAt(i)));
            }
        }
        return sb.toString();
    }
}
