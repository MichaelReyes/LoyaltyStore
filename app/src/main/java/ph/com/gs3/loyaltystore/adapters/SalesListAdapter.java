package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
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

    public void setSalesList(List<Sales> salesList){

        this.salesList.clear();
        this.salesList.addAll(salesList);
        notifyDataSetChanged();

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

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        viewHolder.tvTransactionDate.setText(formatter.format(sales.getTransaction_date()));
        viewHolder.tvTransactionNumber.setText(sales.getTransaction_number());
        viewHolder.tvAmount.setText(decimalFormat.format(sales.getAmount()));
        viewHolder.tvRemarks.setText(sales.getRemarks() == null ? "" : sales.getRemarks());
        viewHolder.tvAmountReceived.setText(decimalFormat.format(sales.getAmount_received()));
        viewHolder.tvChange.setText(decimalFormat.format(sales.getChange()));

        return row;
    }

    private static class SalesProductViewHolder {

        final TextView tvTransactionDate;
        final TextView tvTransactionNumber;
        final TextView tvAmount;
        final TextView tvRemarks;
        final TextView tvAmountReceived;
        final TextView tvChange;

        public SalesProductViewHolder(View view) {
            tvTransactionDate = (TextView) view.findViewById(R.id.Sales_tvTransactionDate);
            tvTransactionNumber = (TextView) view.findViewById(R.id.Sales_tvTransactionNumber);
            tvAmount = (TextView) view.findViewById(R.id.Sales_tvAmount);
            tvRemarks = (TextView) view.findViewById(R.id.Sales_tvRemarks);
            tvAmountReceived = (TextView) view.findViewById(R.id.Sales_tvAmountReceived);
            tvChange = (TextView) view.findViewById(R.id.Sales_tvChange);
        }

    }
}
