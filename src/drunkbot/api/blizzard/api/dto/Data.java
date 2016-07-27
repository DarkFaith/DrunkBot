
package drunkbot.api.blizzard.api.dto;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("games")
    @Expose
    private Games games;
    @SerializedName("playtime")
    @Expose
    private Playtime playtime;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("competitive")
    @Expose
    private Competitive competitive;

    /**
     * 
     * @return
     *     The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     *     The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return
     *     The level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 
     * @param level
     *     The level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 
     * @return
     *     The games
     */
    public Games getGames() {
        return games;
    }

    /**
     * 
     * @param games
     *     The games
     */
    public void setGames(Games games) {
        this.games = games;
    }

    /**
     * 
     * @return
     *     The playtime
     */
    public Playtime getPlaytime() {
        return playtime;
    }

    /**
     * 
     * @param playtime
     *     The playtime
     */
    public void setPlaytime(Playtime playtime) {
        this.playtime = playtime;
    }

    /**
     * 
     * @return
     *     The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 
     * @param avatar
     *     The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 
     * @return
     *     The competitive
     */
    public Competitive getCompetitive() {
        return competitive;
    }

    /**
     * 
     * @param competitive
     *     The competitive
     */
    public void setCompetitive(Competitive competitive) {
        this.competitive = competitive;
    }

}
