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
public class RecievedProductsForDeliveryAdapter extends BaseAdapter {

    public static final String TAG = RecievedProductsForDeliveryAdapter.class.getSimpleName();

    private Context context;
    private List<ProductForDelivery> receivedProductsForDelivery;

    public RecievedProductsForDeliveryAdapter(Context context) {
        this.context = context;
        this.receivedProductsForDelivery = new ArrayList<>();

    }

    public void setReceivedProductsForDelivery(List<ProductForDelivery> receivedProductsForDelivery){

        this.receivedProductsForDelivery.clear();
        this.receivedProductsForDelivery.addAll(receivedProductsForDelivery);
        notifyDataSetChanged();

    }

    public List<ProductForDelivery> getReceivedProductsForDelivery(){
        return this.receivedProductsForDelivery;
    }

    public void addReceivedProductForDelivery(ProductForDelivery productForDelivery){

        this.receivedProductsForDelivery.add(productForDelivery);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return receivedProductsForDelivery.size();
    }

    @Override
    public Object getItem(int position) {
        return receivedProductsForDelivery.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ProductForDeliveryViewHolder viewHolder;

        ProductForDelivery productForDelivery = (ProductForDelivery) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_recieved_product_for_delivery, parent, false);

            viewHolder = new ProductForDeliveryViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (ProductForDeliveryViewHolder) row.getTag();

        viewHolder.tvName.setText(productForDelivery.getName());
        viewHolder.tvQuantityReceived.setText(String.valueOf(productForDelivery.getQuantity_received()));


        return row;
    }

    private static class ProductForDeliveryViewHolder {

        final TextView tvName;
        final TextView tvQuantityReceived;

        public ProductForDeliveryViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.ReceivedProductForDelivery_tvName);
            tvQuantityReceived = (TextView) view.findViewById(R.id.ReceivedProductForDelivery_tvQuantityReceived);
        }

    }

}
