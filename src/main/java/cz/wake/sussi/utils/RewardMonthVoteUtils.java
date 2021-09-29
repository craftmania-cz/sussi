package cz.wake.sussi.utils;

import cz.wake.sussi.Sussi;
import okhttp3.*;
import org.json.JSONObject;

public class RewardMonthVoteUtils {
    public static int getAmount(int i) {
        switch (i) {
            case 1:
                return 15;
            case 2:
                return 10;
            case 3:
                return 7;
            case 4:
                return 5;
            case 5:
                return 5;
            default:
                return 0;
        }
    }

    public static String getRewardCode(int amount) {
        JSONObject craftingStoreJSON;
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient caller = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, "{\"amount\": \"" + amount +"\"}");
            Request request = new Request.Builder().url("https://api.craftingstore.net/v7/gift-cards")
                    .addHeader("token", Sussi.getConfig().getCraftingStoreToken())
                    .post(body)
                    .build();
            Response response = caller.newCall(request).execute();
            craftingStoreJSON = new JSONObject(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
            SussiLogger.fatalMessage("Internal error when retrieving data from CraftingStore api!");
            return "ERROR";
        }
        return craftingStoreJSON.getJSONObject("data").getString("code");
    }
}
