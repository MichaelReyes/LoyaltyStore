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

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;

/**
 * Created by Michael Reyes on 8/17/2015.
 */
public class SalesListAdapter extends BaseAdapter {

    private Context context;
    private List<Sales> salesList;
    private ProductDao productDao;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public SalesListAdapter(Context context, List<Sales> salesList) {
        this.context = context;
        this.salesList = salesList;

        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

    }

    @Override
    public int getCount() {
        return salesList.size();
    }

    @Override
    public Object getItem(int position) {
        return salesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        SalesProductViewHolder viewHolder;

        Sales sales = (Sales) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_sales, parent, false);

            viewHolder = new SalesProductViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (SalesProductViewHolder) row.getTag();

        viewHolder.tvTransactionDate.setText(formatter.format(sales.getTransaction_date()));
        viewHolder.tvTransactionNumber.setText(sales.getTransaction_number());
        viewHolder.tvAmount.setText(String.valueOf(sales.getAmount()));
        viewHolder.tvRemarks.setText(sales.getRemarks() == null ? "" : sales.getRemarks());

        return row;
    }

    private static class SalesProductViewHolder {

        final TextView tvTransactionDate;
        final TextView tvTransactionNumber;
        final TextView tvAmount;
        final TextView tvRemarks;

        public SalesProductViewHolder(View view) {
            tvTransactionDate = (TextView) view.findViewById(R.id.Sales_tvTransactionDate);
            tvTransactionNumber = (TextView) view.findViewById(R.id.Sales_tvTransactionNumber);
            tvAmount = (TextView) view.findViewById(R.id.Sales_tvAmount);
            tvRemarks = (TextView) view.findViewById(R.id.Sales_tvRemarks);
        }

    }
}
