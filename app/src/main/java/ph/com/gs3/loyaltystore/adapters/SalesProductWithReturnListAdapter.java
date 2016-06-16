package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
public class SalesProductWithReturnListAdapter extends BaseAdapter {

    private Context context;
    private List<SalesProduct> salesProducts;
    private ProductDao productDao;
    private Constants constants;

    private SalesProductWithReturnListAdapterListener listener;

    public SalesProductWithReturnListAdapter(Context context) {
        this.context = context;
        salesProducts = new ArrayList<>();

        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();
        this.constants = new Constants();

        if(context instanceof SalesProductWithReturnListAdapterListener){
            listener = (SalesProductWithReturnListAdapterListener) context;
        }else{
            throw  new RuntimeException(getClass().getSimpleName() + " must implement SalesProductWithReturnListAdapterListener");
        }

    }

    public void setSalesProducts(List<SalesProduct> salesProductList) {
        this.salesProducts.clear();
        this.salesProducts.addAll(salesProductList);
        this.notifyDataSetChanged();
    }

    public void addSalesProduct(SalesProduct salesProduct) {
        salesProducts.add(salesProduct);
        notifyDataSetChanged();
    }

    public List<SalesProduct> getSalesProducts() {
        return salesProducts;
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

        final SalesProduct salesProduct = (SalesProduct) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_sales_product_with_return, parent, false);

            viewHolder = new SalesProductViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (SalesProductViewHolder) row.getTag();

        Product product = productDao.load(salesProduct.getProduct_id());

        if (salesProduct.getSale_type().equals("FREEBIE")) {
            viewHolder.tvProductName.setText(product.getName() + " (FREEBIE)");
        } else {
            viewHolder.tvProductName.setText(product.getName());
        }

        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;
        LinearLayout layout = (LinearLayout) viewHolder.bReturn.getParent();
        layout.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.bReturn.setVisibility(View.VISIBLE);

        viewHolder.tvQuantity.setText(Integer.toString(salesProduct.getQuantity()));
        viewHolder.tvCost.setText(decimalFormat.format(product.getUnit_cost()));
        viewHolder.tvTotal.setText(decimalFormat.format(salesProduct.getSub_total()));
        viewHolder.bReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductReturn(salesProduct);
            }
        });

        if(salesProduct.getIs_returned()){
            viewHolder.bReturn.setVisibility(View.INVISIBLE);
            layout.setBackgroundColor(Color.LTGRAY);
        }

        return row;
    }

    private static class SalesProductViewHolder {

        final TextView tvProductName;
        final TextView tvQuantity;
        final TextView tvCost;
        final TextView tvTotal;
        final ImageButton bReturn;

        public SalesProductViewHolder(View view) {
            tvProductName = (TextView) view.findViewById(R.id.SalesProductWithReturn_tvProductName);
            tvQuantity = (TextView) view.findViewById(R.id.SalesProductWithReturn_tvQuantity);
            tvCost = (TextView) view.findViewById(R.id.SalesProductWithReturn_tvCost);
            tvTotal = (TextView) view.findViewById(R.id.SalesProductWithReturn_tvTotal);
            bReturn = (ImageButton) view.findViewById(R.id.SalesProductWithReturn_bReturn);

        }

    }

    public interface SalesProductWithReturnListAdapterListener{

        void onProductReturn(SalesProduct salesProduct);

    }
}
