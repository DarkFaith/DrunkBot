package drunkbot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 03/08/2016.
 */
public final class CurrencyThreshold extends HashMap<Integer, Double>
{

    public CurrencyThreshold() {
        put(5, 4.00);
        put(10, 6.00);
        put(15, 8.00);
        put(20, 10.00);
        put(25, 12.00);
        put(30, 15.00);
    }

}
