package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class DeliveryListAdapter extends BaseAdapter {

    private Context context;
    private List<ProductForDelivery> productForDeliveryList;


    public DeliveryListAdapter(Context context) {
        this.context = context;
        this.productForDeliveryList = new ArrayList<>();

    }

    public void setDeliveryList(List<ProductForDelivery> productForDeliveryList){

        this.productForDeliveryList.clear();
        this.productForDeliveryList.addAll(productForDeliveryList);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return productForDeliveryList.size();
    }

    @Override
    public Object getItem(int position) {
        return productForDeliveryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeliveryViewHolder viewHolder;

        ProductForDelivery productForDelivery = (ProductForDelivery) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_delivery, parent, false);

            viewHolder = new DeliveryViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (DeliveryViewHolder) row.getTag();

        viewHolder.tvName.setText("Name : " + productForDelivery.getName());
        if ("CASH".equals(productForDelivery.getDistribution_type().toUpperCase()))
            viewHolder.tvQuantity.setText("Amount : " + String.valueOf(productForDelivery.getCash()));
        else
            viewHolder.tvQuantity.setText("Quantity To Deliver : " + String.valueOf(productForDelivery.getQuantity()));
        //viewHolder.tvQuantity.setText("Quantity : " + String.valueOf(productForDelivery.getQuantity()));
        viewHolder.tvQuantityReceived.setText("Quantity Received : " + productForDelivery.getQuantity_received());
        viewHolder.tvStatus.setText("Status :" + productForDelivery.getStatus());

        return row;
    }

    private static class DeliveryViewHolder {

        final TextView tvName;
        final TextView tvQuantity;
        final TextView tvQuantityReceived;
        final TextView tvStatus;

        public DeliveryViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.ViewDeliveryHistory_tvName);
            tvQuantity = (TextView) view.findViewById(R.id.ViewDeliveryHistory_tvQuantity);
            tvQuantityReceived = (TextView) view.findViewById(R.id.ViewDeliveryHistory_tvQuantityReceived);
            tvStatus = (TextView) view.findViewById(R.id.ViewDeliveryHistory_tvStatus);

        }

    }

}
