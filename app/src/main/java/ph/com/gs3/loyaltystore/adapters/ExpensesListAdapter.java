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

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;

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
    public View getView(int position, View convertView, ViewGroup parent) {

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

        viewHolder.tvDate.setText(formatter.format(expenses.getDate()));
        viewHolder.tvDescription.setText(expenses.getDescription());
        viewHolder.tvAmount.setText(String.valueOf(expenses.getAmount()));


        return row;
    }

    private static class ExpenseViewHolder {

        final TextView tvDate;
        final TextView tvDescription;
        final TextView tvAmount;

        public ExpenseViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.VE_tvDate);
            tvDescription = (TextView) view.findViewById(R.id.VE_tvDescription);
            tvAmount = (TextView) view.findViewById(R.id.VE_tvAmount);
        }

    }

}
