package ph.com.gs3.loyaltystore.models.synchronizer;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.models.api.RewardsAPI;
import ph.com.gs3.loyaltystore.models.api.ServiceGenerator;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltystore.models.sqlite.dao.RewardDao;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Ervinne Sodusta on 2/8/2016.
 */
public class RewardsSynchronizer {

    public static final String TAG = RewardsSynchronizer.class.getSimpleName();

    public static List<Reward> sync(Context context, String server) {

        ServiceGenerator serviceGenerator = new ServiceGenerator(context, server, HttpLoggingInterceptor.Level.BODY);
        RewardsAPI rewardsAPI = serviceGenerator.createService(RewardsAPI.class);

        Log.v(TAG, "Starting test");
        //Call<List<Reward>> call = rewardsAPI.getRewards("2016-01-29 00:00:00");

        Call<List<Reward>> call = rewardsAPI.getRewardsFromFormalistics();

        try {
            Response<List<Reward>> response = call.execute();
            List<Reward> rewards = response.body();

            if (rewards != null && rewards.size() > 0) {

                RewardDao rewardDao = LoyaltyStoreApplication.getInstance().getSession().getRewardDao();

                for (Reward reward : rewards) {
                    Log.v(TAG, "Reward: " + reward.getReward());
                    rewardDao.insertOrReplace(reward);
                }

                return rewards;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
