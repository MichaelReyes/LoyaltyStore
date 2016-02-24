package ph.com.gs3.loyaltystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;

/**
 * Created by Michael Reyes on 8/17/2015.
 */
public class CashReturnListAdapter extends BaseAdapter {

    private Context context;
    private List<CashReturn> cashReturns;
    private ProductDao productDao;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "HH:mm:ss", Locale.ENGLISH);

    public CashReturnListAdapter(Context context, List<CashReturn> cashReturns) {
        this.context = context;
        this.cashReturns = cashReturns;

        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

    }

    @Override
    public int getCount() {
        return cashReturns.size();
    }

    @Override
    public Object getItem(int position) {
        return cashReturns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CashReturnViewHolder viewHolder;

        CashReturn cashReturn = (CashReturn) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_item_return, parent, false);

            viewHolder = new CashReturnViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (CashReturnViewHolder) row.getTag();


        viewHolder.tvType.setText(cashReturn.getType());
        viewHolder.tvQuantityOrAmount.setText(String.valueOf(cashReturn.getAmount()));
        viewHolder.tvRemarks.setText(cashReturn.getRemarks() == null ? "" : cashReturn.getRemarks());
        viewHolder.tvDepositedToBank.setText(cashReturn.getDeposited_to_bank());
        viewHolder.tvTimeOfDeposit.setText(formatter.format(cashReturn.getTime_of_deposit()));

        if(!cashReturn.getType().toUpperCase().equals("CASH ON BANK")){
            viewHolder.llBankDeposit.setVisibility(View.GONE);
        }



        return row;
    }

    private static class CashReturnViewHolder {

        final TextView tvType;
        final TextView tvDepositedToBank;
        final TextView tvQuantityOrAmount;
        final TextView tvRemarks;
        final TextView tvTimeOfDeposit;
        final LinearLayout llBankDeposit;

        public CashReturnViewHolder(View view) {
            tvType = (TextView) view.findViewById(R.id.ITR__CR_tvType);
            tvQuantityOrAmount = (TextView) view.findViewById(R.id.ITR_CR_tvQuantityOrAmount);
            tvRemarks = (TextView) view.findViewById(R.id.ITR_CR_tvRemarks);
            tvDepositedToBank = (TextView) view.findViewById(R.id.ITR_CR_tvBank);
            tvTimeOfDeposit = (TextView) view.findViewById(R.id.ITR_CR_tvTimeOfDeposit);
            llBankDeposit = (LinearLayout) view.findViewById(R.id.ITR_CR_llBankDeposit);
        }

    }
}
