package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Reward;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class RewardDiscountListAdapter extends BaseAdapter {

    private Context context;
    private List<Reward> rewardList;
    private ProductDao productDao;

    public RewardDiscountListAdapter(Context context, List<Reward> rewardList) {
        this.context = context;
        this.rewardList = rewardList;
        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
    }

    @Override
    public int getCount() {
        return rewardList.size();
    }

    @Override
    public Object getItem(int position) {
        return rewardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RewardViewHolder viewHolder;

        Reward reward = (Reward) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_rewards_discount, parent, false);

            viewHolder = new RewardViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (RewardViewHolder) row.getTag();

        switch (reward.getReward_condition()) {

            case "Number of Products Purchased / Product Purchased":

                String sql = " WHERE " + ProductDao.Properties.Id.columnName + "=?";

                List<Product> products = productDao.queryRaw(sql, new String[]{reward.getCondition_product_id() + ""});

                for (Product product : products) {
                    viewHolder.tvRewardName.setText(
                            product.getName() + " " +
                                    reward.getCondition().toLowerCase() + " " +
                                    reward.getCondition_value()
                    );
                }

                break;

            case "Purchase Amount":
                viewHolder.tvRewardName.setText(
                        reward.getReward_condition() + " " +
                                reward.getCondition().toLowerCase() + " " +
                                reward.getCondition_value()
                );
                break;

        }

        viewHolder.tvDiscount.setText(reward.getReward_value());

        return row;
    }

    private static class RewardViewHolder {

        final TextView tvRewardName;
        final TextView tvDiscount;

        public RewardViewHolder(View view) {
            tvRewardName = (TextView) view.findViewById(R.id.RewardDiscount_tvName);
            tvDiscount = (TextView) view.findViewById(R.id.RewardDiscount_tvDiscount);
        }

    }

}
