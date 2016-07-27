
package drunkbot.api.blizzard.api.dto;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Playtime {

    @SerializedName("quick")
    @Expose
    private String quick;
    @SerializedName("competitive")
    @Expose
    private String competitive;

    /**
     * 
     * @return
     *     The quick
     */
    public String getQuick() {
        return quick;
    }

    /**
     * 
     * @param quick
     *     The quick
     */
    public void setQuick(String quick) {
        this.quick = quick;
    }

    /**
     * 
     * @return
     *     The competitive
     */
    public String getCompetitive() {
        return competitive;
    }

    /**
     * 
     * @param competitive
     *     The competitive
     */
    public void setCompetitive(String competitive) {
        this.competitive = competitive;
    }

}
