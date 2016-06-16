package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class DeliveryByDateListAdapter extends BaseAdapter {

    private Context context;
    private List<ProductForDelivery> productForDeliveryHistoryList;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);


    public DeliveryByDateListAdapter(Context context) {
        this.context = context;
        this.productForDeliveryHistoryList = new ArrayList<>();

    }

    public void setDeliveryByDateList(List<ProductForDelivery> productForDeliveryHistoryList){

        this.productForDeliveryHistoryList.clear();
        this.productForDeliveryHistoryList.addAll(productForDeliveryHistoryList);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return productForDeliveryHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return productForDeliveryHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeliveryByDateViewHolder viewHolder;

        ProductForDelivery productForDelivery = (ProductForDelivery) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_delivery_by_date, parent, false);

            viewHolder = new DeliveryByDateViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (DeliveryByDateViewHolder) row.getTag();
        viewHolder.tvDate.setText(formatter.format(productForDelivery.getDate_received()));

        return row;
    }

    private static class DeliveryByDateViewHolder {

        final TextView tvDate;

        public DeliveryByDateViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.ViewDeliveryByDate_tvDate);
        }

    }

}
