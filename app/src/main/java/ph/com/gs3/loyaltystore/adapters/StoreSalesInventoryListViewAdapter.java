package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

/**
 * Created by Bryan-PC on 07/04/2016.
 */
public class StoreSalesInventoryListViewAdapter extends BaseAdapter {

    public static final String TAG = StoreSalesInventoryListViewAdapter.class.getSimpleName();

    private List<SalesProduct> salesProducts;
    private Context context;

    private static final int LAYOUT = R.layout.view_store_product_sales;

    public StoreSalesInventoryListViewAdapter(Context context) {
        this.context = context;
        this.salesProducts = new ArrayList<>();
    }

    public void setSalesProducts(List<SalesProduct> salesProducts) {
        this.salesProducts.clear();
        this.salesProducts.addAll(salesProducts);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return salesProducts.get(position).getId();
    }

    @Override
    public int getCount() {
        return salesProducts.size();
    }

    @Override
    public SalesProduct getItem(int position) {
        return salesProducts.get(position);
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvQuantity;
        TextView tvSales;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(LAYOUT, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.SPS_tvName);
            viewHolder.tvQuantity = (TextView) rowView.findViewById(R.id.SPS_tvQuantity);
            viewHolder.tvSales = (TextView) rowView.findViewById(R.id.SPS_tvSales);
            rowView.setTag(viewHolder);
        }

        SalesProduct salesProduct = salesProducts.get(position);

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        List<Product>  products =
                productDao
                    .queryBuilder()
                    .where(
                            ProductDao.Properties.Id.eq(salesProduct.getProduct_id())
                    ).list();

        for(Product product : products){

            viewHolder.tvName.setText(product.getName());
            viewHolder.tvQuantity.setText(String.valueOf(salesProduct.getQuantity()));
            viewHolder.tvSales.setText(String.valueOf(salesProduct.getSub_total()));

        }

        Log.v(TAG, "Item detected " + position);
        Log.v(TAG, salesProduct.toString());

        return rowView;

    }

}
