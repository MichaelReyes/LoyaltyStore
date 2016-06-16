package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.RewardDiscountListAdapter;
import ph.com.gs3.loyaltystore.adapters.RewardFreeItemListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;

;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class RewardViewFragment extends Fragment {

    public static final String TAG = RewardViewFragment.class.getSimpleName();
    public static final String EXTRA_REWARDS_LIST = "rewards_list";
    public static final String EXTRA_TOTAL_DISCOUNT = "total_discount";

    private ListView lvRewardsFreeItemList;
    private ListView lvRewardsDiscountList;

    private TextView tvTotalDiscount;

    private RewardFreeItemListAdapter rewardFreeItemListAdapter;
    private RewardDiscountListAdapter rewardDiscountListAdapter;

    private Activity activity;

    private List<Reward> rewards;

    private float totalDiscount;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss zzz yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rewards = new ArrayList<>();

        Bundle args = this.getArguments();

        Gson gson = new Gson();

        String rewardsJsonString = args.getString(EXTRA_REWARDS_LIST);
        Reward[] rewardsArray = gson.fromJson(rewardsJsonString, Reward[].class);
        rewards = Arrays.asList(rewardsArray);

        /*String dataJsonString = args.getString(CheckoutActivity.EXTRA_DATA_JSON_STRING);
        try {


            Log.d(TAG, "DATA JSON STRING : " + dataJsonString);

            JSONObject jsonObject = new JSONObject(dataJsonString);

            Reward[] rewardsArray = gson.fromJson(jsonObject.getJSONArray(Reward.class.getSimpleName()).toString(),Reward[].class);
            rewards = Arrays.asList(rewardsArray);
            *//*JSONArray rewardsJsonArray = jsonObject.getJSONArray(Reward.class.getSimpleName());

            for(int i=0;i<rewardsJsonArray.length();i++){

                JSONObject rewardJsonObject = rewardsJsonArray.getJSONObject(i);

                Reward reward = new Reward();
                reward.setId(rewardJsonObject.getLong(RewardDao.Properties.Id.columnName));
                reward.setReward_condition(rewardJsonObject.getString(RewardDao.Properties.Reward_condition.columnName));
                reward.setCondition_product_id(rewardJsonObject.getInt(RewardDao.Properties.Condition_product_id.columnName));
                reward.setCondition(rewardJsonObject.getString(RewardDao.Properties.Condition.columnName));
                reward.setCondition_value(Float.valueOf(rewardJsonObject.get(RewardDao.Properties.Condition_value.columnName).toString()));
                reward.setReward_type(rewardJsonObject.getString(RewardDao.Properties.Reward_type.columnName));
                reward.setReward(rewardJsonObject.getString(RewardDao.Properties.Reward.columnName));
                reward.setReward_value(rewardJsonObject.getString(RewardDao.Properties.Reward_value.columnName));
                reward.setValid_from(formatter.parse(
                                rewardJsonObject.get(RewardDao.Properties.Valid_from.columnName).toString())
                );
                reward.setValid_until(formatter.parse(
                                rewardJsonObject.get(RewardDao.Properties.Valid_until.columnName).toString())
                );
                reward.setCreated_at(formatter.parse(
                                rewardJsonObject.get(RewardDao.Properties.Created_at.columnName).toString())
                );
                *//**//*reward.setUpdated_at(formatter.parse(
                                rewardJsonObject.get(RewardDao.Properties.Updated_at.columnName).toString())
                );*//**//*

                rewards.add(reward);
            }*//*
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        totalDiscount = args.getFloat(EXTRA_TOTAL_DISCOUNT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_rewards, container, false);

        List<Reward> rewardsFreeItem = new ArrayList<>();
        List<Reward> rewardsDiscount = new ArrayList<>();

        for(Reward reward : rewards){

            switch (reward.getReward_type().toUpperCase()){
                case "FREE_PRODUCT":

                    Log.d(TAG, reward.getId() + " ~ " +
                            reward.getReward_condition() +
                            " ~ " + reward.getCondition() + " ~ " + reward.getCondition_value() );

                    rewardsFreeItem.add(reward);
                    break;
                case "DISCOUNT":



                    rewardsDiscount.add(reward);
                    break;
            }
        }

        rewardFreeItemListAdapter = new RewardFreeItemListAdapter(getActivity(),rewardsFreeItem);
        rewardFreeItemListAdapter.notifyDataSetChanged();

        lvRewardsFreeItemList = (ListView) rootView.findViewById(R.id.Rewards_lvFreeProductList);
        lvRewardsFreeItemList.setAdapter(rewardFreeItemListAdapter);
        lvRewardsFreeItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        rewardDiscountListAdapter = new RewardDiscountListAdapter(getActivity(),rewardsDiscount);
        rewardDiscountListAdapter.notifyDataSetChanged();

        lvRewardsDiscountList = (ListView) rootView.findViewById(R.id.Rewards_lvDiscountList);
        lvRewardsDiscountList.setAdapter(rewardDiscountListAdapter);
        lvRewardsDiscountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        tvTotalDiscount = (TextView) rootView.findViewById(R.id.Rewards_tvTotalDiscount);
        tvTotalDiscount.setText(decimalFormat.format(totalDiscount));

        return rootView;
    }

}

