package drunkbot.api.blizzard.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import drunkbot.api.blizzard.BlizzardApiException;
import drunkbot.api.blizzard.api.dto.AccountProfile;
import drunkbot.api.blizzard.api.dto.Competitive;
import drunkbot.api.blizzard.api.dto.Data;
import drunkbot.api.blizzard.constant.Platform;
import drunkbot.api.blizzard.constant.Region;
import drunkbot.api.blizzard.util.Util;

/**
 * Created by Kevin on 26/07/2016.
 */
public class AccountApi
{
    private static final String VERSION = "/v0.5/";
    private static final String endpoint = "https://api.lootbox.eu/";

    public static AccountProfile getAccountProfile(String key, Region region, Platform platform, String battleTag) throws BlizzardApiException {
        String url = endpoint + platform.getName() + "/" + region.getName() + "/" + Util.bTagToURL(battleTag) + "/profile";
        AccountProfile accountProfile = null;
        try {
            String json = Request.sendGet(url);
            accountProfile = new Gson().fromJson(json, AccountProfile.class);
        } catch (JsonSyntaxException e) {
            throw new BlizzardApiException(BlizzardApiException.PARSE_FAILURE);
        }
        return accountProfile;
    }
}
