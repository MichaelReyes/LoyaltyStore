package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class DeliveryListAdapter extends BaseAdapter {

    private Context context;
    private List<ProductDelivery> productDeliveryList;


    public DeliveryListAdapter(Context context, List<ProductDelivery> productDeliveryList) {
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
        DeliveryViewHolder viewHolder;

        ProductDelivery productDelivery = (ProductDelivery) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_delivery, parent, false);

            viewHolder = new DeliveryViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (DeliveryViewHolder) row.getTag();

        viewHolder.tvName.setText("Name : " + productDelivery.getName());
        viewHolder.tvQuantity.setText("Quantity : " + String.valueOf(productDelivery.getQuantity()));
        viewHolder.tvStatus.setText("Status :" + productDelivery.getStatus());

        return row;
    }

    private static class DeliveryViewHolder {

        final TextView tvName;
        final TextView tvQuantity;
        final TextView tvStatus;

        public DeliveryViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.VD_tvName);
            tvQuantity = (TextView) view.findViewById(R.id.VD_tvQuantity);
            tvStatus = (TextView) view.findViewById(R.id.VD_tvStatus);

        }

    }

}
