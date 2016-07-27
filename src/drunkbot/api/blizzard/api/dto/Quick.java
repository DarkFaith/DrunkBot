
package drunkbot.api.blizzard.api.dto;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Quick {

    @SerializedName("wins")
    @Expose
    private String wins;
    @SerializedName("lost")
    @Expose
    private Integer lost;
    @SerializedName("played")
    @Expose
    private String played;

    /**
     * 
     * @return
     *     The wins
     */
    public String getWins() {
        return wins;
    }

    /**
     * 
     * @param wins
     *     The wins
     */
    public void setWins(String wins) {
        this.wins = wins;
    }

    /**
     * 
     * @return
     *     The lost
     */
    public Integer getLost() {
        return lost;
    }

    /**
     * 
     * @param lost
     *     The lost
     */
    public void setLost(Integer lost) {
        this.lost = lost;
    }

    /**
     * 
     * @return
     *     The played
     */
    public String getPlayed() {
        return played;
    }

    /**
     * 
     * @param played
     *     The played
     */
    public void setPlayed(String played) {
        this.played = played;
    }

}
