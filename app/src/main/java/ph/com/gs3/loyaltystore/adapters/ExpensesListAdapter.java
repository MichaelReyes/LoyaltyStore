package ph.com.gs3.loyaltystore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;

/**
 * Created by Bryan-PC on 05/02/2016.
 */
public class ExpensesListAdapter extends BaseAdapter {

    private Context context;
    private List<Expenses> expensesList;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);


    public ExpensesListAdapter(Context context, List<Expenses> expensesList) {
        this.context = context;
        this.expensesList = expensesList;

    }

    @Override
    public int getCount() {
        return expensesList.size();
    }

    @Override
    public Object getItem(int position) {
        return expensesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ExpenseViewHolder viewHolder;

        Expenses expenses = (Expenses) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_expenses, parent, false);

            viewHolder = new ExpenseViewHolder(row);
            row.setTag(viewHolder);
        }

        viewHolder = (ExpenseViewHolder) row.getTag();

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        if(expenses.getDate()!= null){
            viewHolder.tvDate.setText(formatter.format(expenses.getDate()));
        }
        viewHolder.tvDescription.setText(expenses.getDescription());

        viewHolder.tvAmount.setText(decimalFormat.format(expenses.getAmount()));

        viewHolder.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                ExpensesDao expensesDao = LoyaltyStoreApplication.getInstance().getSession().getExpensesDao();

                                expensesDao.delete((Expenses) getItem(position));

                                expensesList.remove(position);

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

        if(expenses.getIs_synced() == null){
            viewHolder.bDelete.setVisibility(View.VISIBLE);
        }else if(expenses.getIs_synced()){
            viewHolder.bDelete.setVisibility(View.INVISIBLE);
        }



        return row;
    }

    private static class ExpenseViewHolder {

        final TextView tvDate;
        final TextView tvDescription;
        final TextView tvAmount;
        final Button bDelete;

        public ExpenseViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.VE_tvDate);
            tvDescription = (TextView) view.findViewById(R.id.VE_tvDescription);
            tvAmount = (TextView) view.findViewById(R.id.VE_tvAmount);
            bDelete = (Button) view.findViewById(R.id.VE_bDelete);
        }

    }

}
