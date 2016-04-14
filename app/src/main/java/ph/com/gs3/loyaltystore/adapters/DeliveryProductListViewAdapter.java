package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.views.DeliveryProductListView;

/**
 * Created by Bryan-PC on 05/04/2016.
 */
public class DeliveryProductListViewAdapter extends BaseAdapter {

    public static final String TAG = DeliveryProductListViewAdapter.class.getSimpleName();

    private Context context;
    private List<ProductDelivery> productDeliveryList;

    private List<ProductDelivery> acceptedProducts;
    private List<ProductDelivery> rejectedProducts;

    public DeliveryProductListViewAdapter(Context context) {
        this.context = context;
        this.productDeliveryList = new ArrayList<>();

        acceptedProducts = new ArrayList<>();
        rejectedProducts = new ArrayList<>();

    }

    public void setProductDeliveryList(List<ProductDelivery> productDeliveryList) {
        this.productDeliveryList.clear();
        this.productDeliveryList.addAll(productDeliveryList);
        this.notifyDataSetChanged();
    }

    public List<ProductDelivery> getProductDeliveryList() {

        return productDeliveryList;

    }

    public List<ProductDelivery> getAccpetedProductDeliveryList() {

        return acceptedProducts;

    }

    public List<ProductDelivery> getRejectedProductDeliveryList() {

        return rejectedProducts;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {

            rowView = new DeliveryProductListView(context);

        }

        final ProductDelivery productDelivery = productDeliveryList.get(position);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyy");
        String dateString = simpleDateFormat.format(productDelivery.getDate_delivered());

        final DeliveryProductListView deliveryProductListView = (DeliveryProductListView) rowView;

        deliveryProductListView.tvDate.setText("Date : " + dateString);
        deliveryProductListView.tvName.setText(productDelivery.getName());

        if("PRODUCT".equals(productDelivery.getDistribution_type().trim().toUpperCase())){
            deliveryProductListView.tvQuantity.setText("Quantity : " + productDelivery.getQuantity());
        }else if("CASH".equals(productDelivery.getDistribution_type().trim().toUpperCase())){
            deliveryProductListView.tvQuantity.setText("Amount : " + productDelivery.getCash());
        }

        deliveryProductListView.tvStatus.setText(productDelivery.getStatus());

        if("Accepted".equals(productDelivery.getStatus().trim())){
            deliveryProductListView.llProductDelivery.setBackgroundColor(Color.GREEN);
            acceptedProducts.add(productDelivery);
        }else if("Rejected".equals(productDelivery.getStatus().trim())){
            deliveryProductListView.llProductDelivery.setBackgroundColor(Color.RED);
            rejectedProducts.add(productDelivery);
        }

        deliveryProductListView.bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryProductListView.llProductDelivery.setBackgroundColor(Color.GREEN);
                //deliveryProductListView.llProductDelivery.setAlpha((float) 0.9);
                if(rejectedProducts.contains(productDelivery))
                    rejectedProducts.remove(productDelivery);

                productDelivery.setStatus("Accepted");
                notifyDataSetChanged();

                acceptedProducts.add(productDelivery);

                /*
                Log.d(TAG, " ==================================== ");

                for(ProductDelivery p : acceptedProducts){
                    Log.d(TAG, "ACCEPTED PRODUCT :" + p.getName());
                }

                Log.d(TAG, " ==================================== ");

                Log.d(TAG, " ------------------------------------ ");

                for(ProductDelivery p : rejectedProducts){
                    Log.d(TAG, "REJECTED PRODUCT :" + p.getName());
                }

                Log.d(TAG, " ------------------------------------ ");
                */

            }
        });

        deliveryProductListView.bReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryProductListView.llProductDelivery.setBackgroundColor(Color.RED);
                //deliveryProductListView.llProductDelivery.setAlpha((float) 0.9);
                if(acceptedProducts.contains(productDelivery))
                    acceptedProducts.remove(productDelivery);

                productDelivery.setStatus("Rejected");
                notifyDataSetChanged();

                rejectedProducts.add(productDelivery);

                /*
                Log.d(TAG, " ==================================== ");

                for(ProductDelivery p : acceptedProducts){
                    Log.d(TAG, "ACCEPTED PRODUCT :" + p.getName());
                }

                Log.d(TAG, " ==================================== ");

                Log.d(TAG, " ------------------------------------ ");

                for(ProductDelivery p : rejectedProducts){
                    Log.d(TAG, "REJECTED PRODUCT :" + p.getName());
                }

                Log.d(TAG, " ------------------------------------ ");
                */

            }
        });


        return rowView;
    }

}
