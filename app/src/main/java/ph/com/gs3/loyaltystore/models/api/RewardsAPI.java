package ph.com.gs3.loyaltystore.models.api;

import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ervinne Sodusta on 2/8/2016.
 */
public interface RewardsAPI {

    @GET("rewards?dataType=json")
    Call<List<Reward>> getRewards(@Query("lastUpdate") String lastUpdate);

    @GET("pos/rewards")
    Call<List<Reward>> getRewardsFromFormalistics();

}
