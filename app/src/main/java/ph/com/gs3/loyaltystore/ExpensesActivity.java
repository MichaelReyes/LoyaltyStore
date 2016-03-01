package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.ExpensesListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;

/**
 * Created by Bryan-PC on 24/02/2016.
 */
public class ExpensesActivity extends Activity {

    public static final String TAG = ExpensesActivity.class.getSimpleName();

    private List<Expenses> expensesList;
    private ExpensesListAdapter expensesListAdapter;
    private ExpensesDao expensesDao;

    private ListView lvExpenses;

    private EditText etDescription;
    private EditText etAmount;

    private long expenseId;

    private String current;

    private Button bAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expenses);

        expensesList = new ArrayList<>();
        expensesListAdapter = new ExpensesListAdapter(this,expensesList);

        initializeDataAccessObject();
        initializeViews();

        expenseId = -1;

        onViewReady();


    }

    private void initializeDataAccessObject(){

        expensesDao = LoyaltyStoreApplication.getInstance().getSession().getExpensesDao();

    }

    private void initializeViews(){

        lvExpenses = (ListView) findViewById(R.id.Expenses_lvExpenses);
        lvExpenses.setAdapter(expensesListAdapter);
        lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Expenses expenses = (Expenses) expensesListAdapter.getItem(position);

                if(!expenses.getIs_synced()){
                    expenseId = expenses.getId();

                    etDescription.setText(expenses.getDescription());
                    etAmount.setText(String.valueOf(expenses.getAmount()));

                }

            }
        });


        Constants constants = new Constants();
        final DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        etDescription = (EditText) findViewById(R.id.Expenses_etDescription);
        etAmount = (EditText) findViewById(R.id.Expenses_etAmount);

        bAdd = (Button) findViewById(R.id.Expenses_bAdd);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddExpenses();
            }
        });

    }



    private void onViewReady(){

        expensesList.clear();

        List<Expenses> eList = expensesDao.loadAll();

        for(Expenses expenses : eList){

            expensesList.add(expenses);

        }

        expensesListAdapter.notifyDataSetChanged();

    }

    private void onAddExpenses(){

        boolean valid = true;

        if("".equals(etDescription.getText().toString().trim())){
            valid = false;
            etDescription.setError("This field is required");
        }

        if("".equals(etAmount.getText().toString().trim())){
            valid = false;
            etAmount.setError("This field is required");
        }

        if(valid){
            Expenses expenses = new Expenses();
            expenses.setDescription(etDescription.getText().toString());
            expenses.setAmount(Float.valueOf(etAmount.getText().toString()));
            expenses.setIs_synced(false);

            if(expenseId != -1){

                expenses.setId(expenseId);

                List<Expenses> eList = expensesDao.queryBuilder()
                        .where(ExpensesDao.Properties.Id.eq(expenseId)).list();

                for(Expenses e : eList){

                    expenses.setDate(e.getDate());

                }

            }else{
                expenses.setDate(new Date());
            }

            expenseId = -1;

            expensesDao.insertOrReplaceInTx(expenses);

            clearInputFields();

            onViewReady();
        }

    }

    private void clearInputFields(){

        etDescription.setText("");
        etAmount.setText("");

    }

}
