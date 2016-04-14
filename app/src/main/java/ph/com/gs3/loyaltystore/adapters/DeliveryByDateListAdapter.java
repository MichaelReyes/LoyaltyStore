package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class DeliveryByDateListAdapter extends BaseAdapter {

    private Context context;
    private List<ProductDelivery> productDeliveryList;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);


    public DeliveryByDateListAdapter(Context context, List<ProductDelivery> productDeliveryList) {
        this.context = context;
        this.productDeliveryList = productDeliveryList;

    }

    @Override
    public int getCount() {
        return productDeliveryList.size();
    }

    @Override
    public Object getItem(int position) {
        return productDeliveryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeliveryByDateViewHolder viewHolder;

        ProductDelivery productDelivery = (ProductDelivery) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_delivery_by_date, parent, false);

            viewHolder = new DeliveryByDateViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (DeliveryByDateViewHolder) row.getTag();
        viewHolder.tvDate.setText("Date delivered : " + formatter.format(productDelivery.getDate_delivered()));
        viewHolder.tvAgent.setText("Delivered By : " + productDelivery.getDelivered_by_agent_name());

        return row;
    }

    private static class DeliveryByDateViewHolder {

        final TextView tvDate;
        final TextView tvAgent;

        public DeliveryByDateViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.VDBD_tvDate);
            tvAgent = (TextView) view.findViewById(R.id.VDBD_tvAgent);
        }

    }

}
