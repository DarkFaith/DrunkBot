package drunkbot.api.riot;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Created by Kevin on 26/07/2016.
 */
public class Util
{

    public static int compareRanks(String rank1, String rank2) throws IllegalArgumentException
    {
        String[] rank1Split = rank1.split(" ", 3);
        String[] rank2Split = rank2.split(" ", 3);
        if (rank1Split.length != 3 && rank2Split.length != 3)
        {
            throw new IllegalArgumentException("Rank must be Tier, Division and LP: e.g. DIAMOND IV 98");
        }
        // Compare tiers
        RankTier tier1 = RankTier.valueOf(rank1Split[0]);
        RankTier tier2 = RankTier.valueOf(rank2Split[0]);
        int tierCompare = tier1.compareTo(tier2);
        if (tierCompare != 0)
            return tierCompare;

        // Compare divisions
        RankDivision div1 = RankDivision.valueOf(rank1Split[1]);
        RankDivision div2 = RankDivision.valueOf(rank2Split[1]);
        int divCompare = tier1.compareTo(tier2);
        if (divCompare != 0)
            return div1.compareTo(div2);

        // Compare LP
        rank1Split[2].replaceAll("LP", "");
        rank2Split[2].replaceAll("LP", "");
        int lp1 = Integer.parseInt(rank1Split[2]);
        int lp2 = Integer.parseInt(rank2Split[2]);
        if (lp1 > lp2)
            return 1;
        else if (lp2 > lp1)
            return -1;
        else
            return 0;
    }
}
