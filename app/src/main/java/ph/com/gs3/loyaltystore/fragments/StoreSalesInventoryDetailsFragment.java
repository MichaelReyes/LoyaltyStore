package ph.com.gs3.loyaltystore.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.StoreSalesInventoryListViewAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

/**
 * Created by Bryan-PC on 07/04/2016.
 */
public class StoreSalesInventoryDetailsFragment extends Fragment {

    public static final String TAG = StoreSalesInventoryDetailsFragment.class.getSimpleName();

    private Context context;

    private StoreSalesInventoryDetailsFragmentListener listener;
    private StoreSalesInventoryListViewAdapter adapter;

    private List<SalesProduct> productsToBeDisplayed;

    private ListView lvSalesInventory;

    private DatePickerDialog dpFilterDate;

    private Button bSelectDate;
    private Button bAllRecords;

    private TextView tvDateFilter;

    private SimpleDateFormat formatter;

    private View v;

    public static StoreSalesInventoryDetailsFragment newInstance() {
        StoreSalesInventoryDetailsFragment fragment = new StoreSalesInventoryDetailsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On Resume");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof StoreSalesInventoryDetailsFragmentListener) {
            listener = (StoreSalesInventoryDetailsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StoreSalesInventoryDetailsFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_store_sales_inventory_details, container, false);

        v = rootView;

        formatter = Constants.SIMPLE_DATE_FORMAT;

        adapter = new StoreSalesInventoryListViewAdapter(context);

        initializeViews();

        Log.d(TAG,"StoreSalesInventoryDetailsFragment CREATED");

        listener.onLoadStoreSalesInventory(java.sql.Date.valueOf(tvDateFilter.getText().toString()));

        if (productsToBeDisplayed != null) {

            adapter.setSalesProducts(productsToBeDisplayed);
        }

        return rootView;
    }

    private void initializeViews(){

        lvSalesInventory = (ListView) v.findViewById(R.id.StoreSalesInventoryDetails_lvSalesInventory);
        lvSalesInventory.setAdapter(adapter);

        setDateTimeField();

        tvDateFilter = (TextView) v.findViewById(R.id.StoreSalesInventoryDetails_tvDateFilter);
        tvDateFilter.setText(formatter.format(new Date()));

        bSelectDate = (Button) v.findViewById(R.id.StoreSalesInventoryDetails_bSelectDate);
        bSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpFilterDate.show();
            }
        });

        bAllRecords = (Button) v.findViewById(R.id.StoreSalesInventoryDetails_bAll);
        bAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDateFilter.setText("[No Date Selected]");
                listener.onLoadStoreSalesInventory(null);
            }
        });


    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dpFilterDate = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDateFilter.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dpFilterDate.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.onLoadStoreSalesInventory(java.sql.Date.valueOf(tvDateFilter.getText().toString()));
            }
        });

    }

    public void setSales(List<SalesProduct> salesProducts){

        if(adapter == null){
            productsToBeDisplayed =salesProducts;
        }else{
            adapter.setSalesProducts(salesProducts);
        }

    }

    public Date getDateFilter(){

        return java.sql.Date.valueOf(tvDateFilter.getText().toString());

    }

    public interface StoreSalesInventoryDetailsFragmentListener{

        void onLoadStoreSalesInventory(Date transactionDate);

    }

}
