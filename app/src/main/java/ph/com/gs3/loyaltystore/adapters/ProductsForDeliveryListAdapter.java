package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class ProductsForDeliveryListAdapter extends BaseAdapter {

    public static final String TAG = ProductsForDeliveryListAdapter.class.getSimpleName();

    private ProductsForDeliveryListAdapterListener listener;

    private Context context;
    private List<ProductForDelivery> productForDeliveryList;

    public ProductsForDeliveryListAdapter(Context context) {
        this.context = context;
        this.productForDeliveryList = new ArrayList<>();

        if(context instanceof ProductsForDeliveryListAdapterListener){
            listener = (ProductsForDeliveryListAdapterListener) context;
        }else{
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement ProductsForDeliveryListAdapterListener");
        }

    }

    public void setProductForDeliveryList(List<ProductForDelivery> productForDeliveryList){

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
        ProductForDeliveryViewHolder viewHolder;

        final ProductForDelivery productForDelivery = (ProductForDelivery) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_product_for_delivery, parent, false);

            viewHolder = new ProductForDeliveryViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (ProductForDeliveryViewHolder) row.getTag();

        SimpleDateFormat formatter = Constants.SIMPLE_DATE_TIME_FORMAT;

        Log.d(TAG, " productForDelivery.getDate_created() >> " + productForDelivery.getDate_created());

        viewHolder.tvName.setText(productForDelivery.getName());
        viewHolder.tvDate.setText(formatter.format(productForDelivery.getDate_created()));
        viewHolder.tvTrackingNumber.setText("Tracking No: " + productForDelivery.getTrack_no());
        viewHolder.tvQuantity.setText("Quantity: " + productForDelivery.getQuantity());
        viewHolder.tvStatus.setText(productForDelivery.getStatus());

        viewHolder.bReceiveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductForDeliveryReceiveAll(productForDelivery);
            }
        });

        return row;
    }

    private static class ProductForDeliveryViewHolder {

        final TextView tvName;
        final TextView tvDate;
        final TextView tvTrackingNumber;
        final TextView tvQuantity;
        final TextView tvStatus;
        final Button bReceiveAll;

        public ProductForDeliveryViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.ProductForDelivery_tvName);
            tvDate = (TextView) view.findViewById(R.id.ProductForDelivery_tvDate);
            tvTrackingNumber = (TextView) view.findViewById(R.id.ProductForDelivery_tvTrackNo);
            tvQuantity = (TextView) view.findViewById(R.id.ProductForDelivery_tvQuantity);
            tvStatus = (TextView) view.findViewById(R.id.ProductForDelivery_tvStatus);
            bReceiveAll = (Button) view.findViewById(R.id.ProductForDelivery_bReceiveAll);
        }

    }

    public interface ProductsForDeliveryListAdapterListener{

        void onProductForDeliveryReceiveAll(ProductForDelivery productForDelivery);

    }

}
