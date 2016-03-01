package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;


/**
 * Created by Michael Reyes on 8/17/2015.
 */
public class SalesProductListAdapter extends BaseAdapter {

    private Context context;
    private List<SalesProduct> salesProducts;
    private ProductDao productDao;
    private Constants constants;

    public SalesProductListAdapter(Context context, List<SalesProduct> salesProducts) {
        this.context = context;
        this.salesProducts = salesProducts;

        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        this.constants = new Constants();

    }

    @Override
    public int getCount() {
        return salesProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return salesProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        SalesProductViewHolder viewHolder;

        SalesProduct salesProduct = (SalesProduct) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_sales_product, parent, false);

            viewHolder = new SalesProductViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (SalesProductViewHolder) row.getTag();

        Product product = productDao.load((long) salesProduct.getProduct_id());

        if (salesProduct.getSale_type().equals("FREEBIE")) {
            viewHolder.tvProductName.setText(product.getName() + " (FREEBIE)");
        } else {
            viewHolder.tvProductName.setText(product.getName());
        }

        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        viewHolder.tvQuantity.setText(Integer.toString(salesProduct.getQuantity()));
        viewHolder.tvCost.setText(decimalFormat.format(product.getUnit_cost()));
        viewHolder.tvTotal.setText(decimalFormat.format(salesProduct.getSub_total()));

        return row;
    }

    private static class SalesProductViewHolder {

        final TextView tvProductName;
        final TextView tvQuantity;
        final TextView tvCost;
        final TextView tvTotal;

        public SalesProductViewHolder(View view) {
            tvProductName = (TextView) view.findViewById(R.id.Transaction_tvProductName);
            tvQuantity = (TextView) view.findViewById(R.id.Transaction_tvQuantity);
            tvCost = (TextView) view.findViewById(R.id.Transaction_tvCost);
            tvTotal = (TextView) view.findViewById(R.id.Transaction_tvTotal);

        }

    }
}
