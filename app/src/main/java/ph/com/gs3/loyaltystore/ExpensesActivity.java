package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.ExpensesListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;

/**
 * Created by Bryan-PC on 24/02/2016.
 */
public class ExpensesActivity extends Activity {

    private List<Expenses> expensesList;
    private ExpensesListAdapter expensesListAdapter;
    private ExpensesDao expensesDao;

    private ListView lvExpenses;

    private EditText etDescription;
    private EditText etAmount;

    private Button bAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expenses);

        expensesList = new ArrayList<>();
        expensesListAdapter = new ExpensesListAdapter(this,expensesList);

        initializeDataAccessObject();
        initializeViews();

        onViewReady();


    }

    private void initializeDataAccessObject(){

        expensesDao = LoyaltyStoreApplication.getInstance().getSession().getExpensesDao();

    }

    private void initializeViews(){

        lvExpenses = (ListView) findViewById(R.id.Expenses_lvExpenses);
        lvExpenses.setAdapter(expensesListAdapter);

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
        }

        if("".equals(etAmount.getText().toString().trim())){
            valid = false;
        }

        if(valid){
            Expenses expenses = new Expenses();
            expenses.setDate(new Date());
            expenses.setDescription(etDescription.getText().toString());
            expenses.setAmount(Float.valueOf(etAmount.getText().toString()));

            expensesDao.insert(expenses);

            clearInputFields();

            onViewReady();
        }

    }

    private void clearInputFields(){

        etDescription.setText("");
        etAmount.setText("");

    }

}
