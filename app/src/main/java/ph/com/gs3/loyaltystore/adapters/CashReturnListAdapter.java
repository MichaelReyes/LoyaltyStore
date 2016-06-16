package ph.com.gs3.loyaltystore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;

/**
 * Created by Michael Reyes on 8/17/2015.
 */
public class CashReturnListAdapter extends BaseAdapter {

    private Context context;
    private List<CashReturn> cashReturns;
    private ProductDao productDao;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public CashReturnListAdapter(Context context, List<CashReturn> cashReturns) {
        this.context = context;
        this.cashReturns = cashReturns;

        this.productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

    }

    public void setCashReturnList(List<CashReturn> cashReturns){
        this.cashReturns.clear();
        this.cashReturns.addAll(cashReturns);
        notifyDataSetChanged();
    }

    public void addCashReturn(CashReturn cashReturns){
        this.cashReturns.add(cashReturns);
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CashReturnViewHolder viewHolder;

        CashReturn cashReturn = (CashReturn) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_cash_return, parent, false);

            viewHolder = new CashReturnViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (CashReturnViewHolder) row.getTag();

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        viewHolder.tvType.setText("Type : " + cashReturn.getCash_type());

        String dateString = cashReturn.getDate_created() != null ? formatter.format(cashReturn.getDate_created()) : "";

        viewHolder.tvDate.setText("Date : " +  dateString );

        viewHolder.tvQuantityOrAmount.setText("Amount : " + decimalFormat.format(cashReturn.getAmount()));

        String remarks = cashReturn.getRemarks() == null ? "" : cashReturn.getRemarks();

        viewHolder.tvRemarks.setText("Remarks : " + remarks);
        viewHolder.tvDepositedToBank.setText("Bank : " + cashReturn.getDeposited_to_bank());
        viewHolder.tvStatus.setText("Status : " + cashReturn.getStatus());
        if(cashReturn.getTime_of_deposit() != null){
            viewHolder.tvTimeOfDeposit.setText("Date : " + formatter.format(cashReturn.getTime_of_deposit()));
        }

        viewHolder.llBankDeposit.setVisibility(View.VISIBLE);

        if(!cashReturn.getType().toUpperCase().equals("CASH ON BANK")){
            viewHolder.llBankDeposit.setVisibility(View.GONE);
        }

        viewHolder.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                CashReturnDao cashReturnDao = LoyaltyStoreApplication.getInstance().getSession().getCashReturnDao();

                                cashReturnDao.delete((CashReturn) getItem(position));

                                cashReturns.remove(position);

                                notifyDataSetChanged();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this item?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        viewHolder.bDelete.setVisibility(View.VISIBLE);

        if(cashReturn.getIs_synced()){
            viewHolder.bDelete.setVisibility(View.INVISIBLE);
        }

        return row;
    }

    private static class CashReturnViewHolder {

        final TextView tvType;
        final TextView tvDate;
        final TextView tvDepositedToBank;
        final TextView tvQuantityOrAmount;
        final TextView tvRemarks;
        final TextView tvTimeOfDeposit;
        final TextView tvStatus;
        final LinearLayout llBankDeposit;
        final Button bDelete;

        public CashReturnViewHolder(View view) {
            tvType = (TextView) view.findViewById(R.id.CashReturn_tvType);
            tvDate = (TextView) view.findViewById(R.id.CashReturn_tvDate);
            tvQuantityOrAmount = (TextView) view.findViewById(R.id.CashReturn_tvQuantityOrAmount);
            tvRemarks = (TextView) view.findViewById(R.id.CashReturn_tvRemarks);
            tvDepositedToBank = (TextView) view.findViewById(R.id.CashReturn_tvBank);
            tvTimeOfDeposit = (TextView) view.findViewById(R.id.CashReturn_tvTimeOfDeposit);
            tvStatus = (TextView) view.findViewById(R.id.CashReturn_tvStatus);
            llBankDeposit = (LinearLayout) view.findViewById(R.id.CashReturn_llBankDeposit);
            bDelete = (Button) view.findViewById(R.id.CashReturn_bDelete);
        }

    }
}
