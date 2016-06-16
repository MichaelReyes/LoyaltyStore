package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.ExpensesListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;

/**
 * Created by Bryan-PC on 27/04/2016.
 */
public class ExpensesFragment extends Fragment {

    public static final String TAG = ExpensesFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    private ExpenseFragmentListener listener;

    private ExpensesListAdapter adapter;

    private List<Expenses> expensesList;

    private ListView lvExpenses;

    private EditText etDescription;
    private EditText etAmount;
    private EditText etId;

    private Button bAdd;

    public ExpensesFragment createInstance() {

        ExpensesFragment expensesFragment = new ExpensesFragment();
        return expensesFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if (context instanceof ExpenseFragmentListener) {
            listener = (ExpenseFragmentListener) context;
        } else {
            throw new RuntimeException(getContext().getClass().getSimpleName() + " must implement ExpenseFragmentListener");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activity = (FragmentActivity) activity;

        if (activity instanceof ExpenseFragmentListener) {
            listener = (ExpenseFragmentListener) activity;
        } else {
            throw new RuntimeException(getContext().getClass().getSimpleName() + " must implement ExpenseFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_expenses, container, false);

        expensesList = new ArrayList<>();
        adapter = new ExpensesListAdapter(context, expensesList);

        lvExpenses = (ListView) rootView.findViewById(R.id.Expenses_lvExpenses);
        lvExpenses.setAdapter(adapter);
        lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Expenses expenses = (Expenses) adapter.getItem(position);

                if (!expenses.getIs_synced()) {
                    etId.setText(String.valueOf(expenses.getId()));
                    etDescription.setText(expenses.getDescription());
                    etAmount.setText(String.valueOf(expenses.getAmount()));

                }

            }
        });

        etDescription = (EditText) rootView.findViewById(R.id.Expenses_etDescription);
        etAmount = (EditText) rootView.findViewById(R.id.Expenses_etAmount);
        etId = (EditText) rootView.findViewById(R.id.Expenses_etId);

        bAdd = (Button) rootView.findViewById(R.id.Expenses_bAdd);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String expenseIdString = etId.getText().toString().trim();

                if("".equals(expenseIdString)){
                    listener.onAddExpense(-1);
                }else{
                    listener.onAddExpense(Long.valueOf(etId.getText().toString()));
                }


            }
        });

        listener.onExpenseViewReady();

        return rootView;

    }

    public void clearInputFields() {

        etDescription.setText("");
        etAmount.setText("");
        etId.setText("");

    }

    public String getDescription(){
        return etDescription.getText().toString().trim();
    }

    public String getAmountString(){
        return etAmount.getText().toString().trim();
    }

    public void setDescriptionError(String error){

        etDescription.setError(error);

    }

    public void setAmountError(String error){

        etAmount.setError(error);

    }

    public void setExpensesList(List<Expenses> expensesList){
        if(adapter != null){
            adapter.setExpensesList(expensesList);
        }
    }

    public interface ExpenseFragmentListener {

        void onExpenseViewReady();

        void onAddExpense(long expenseId);

    }

}
