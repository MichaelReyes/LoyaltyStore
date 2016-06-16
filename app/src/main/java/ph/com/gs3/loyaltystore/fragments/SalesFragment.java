package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.SalesListAdapter;
import ph.com.gs3.loyaltystore.adapters.SalesProductWithReturnListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

/**
 * Created by Bryan-PC on 27/04/2016.
 */
public class SalesFragment extends Fragment {

    public static final String TAG = SalesFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    private List<Sales> salesList;
    private List<SalesProduct> salesProductList;

    private SalesListAdapter salesListAdapter;
    private SalesProductWithReturnListAdapter salesProductWithReturnListAdapter;

    private DatePickerDialog dpFilterDate;

    private Button bSelectDate;
    private Button bAllRecords;

    private ListView lvSales;
    private ListView lvSalesProducts;

    private TextView tvDateFilter;
    private TextView tvTotalSales;

    private SalesFragmentListener listener;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);


    public SalesFragment createInstance(){

        SalesFragment salesFragment  = new SalesFragment();
        return salesFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if(context instanceof SalesFragmentListener){
            listener = (SalesFragmentListener) context;
        }else{
            throw new RuntimeException(getContext().getClass().getSimpleName() + " must implement SalesFragmentListener");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activity = (FragmentActivity) activity;

        if(activity instanceof SalesFragmentListener){
            listener = (SalesFragmentListener) activity;
        }else{
            throw new RuntimeException(getContext().getClass().getSimpleName() + " must implement SalesFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sales,container,false);

        setDateTimeField();

        salesList = new ArrayList<>();
        salesListAdapter = new SalesListAdapter(context, salesList);

        salesProductList = new ArrayList<>();
        salesProductWithReturnListAdapter = new SalesProductWithReturnListAdapter(context);

        lvSales = (ListView) rootView.findViewById(R.id.Sales_lvSales);
        lvSales.setAdapter(salesListAdapter);
        lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onClickSales((Sales) salesListAdapter.getItem(position));
            }
        });

        lvSalesProducts = (ListView) rootView.findViewById(R.id.Sales_lvSalesProducts);
        lvSalesProducts.setAdapter(salesProductWithReturnListAdapter);
        lvSalesProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        bSelectDate = (Button) rootView.findViewById(R.id.Sales_bSelectDate);
        bSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpFilterDate.show();
            }
        });

        bAllRecords = (Button) rootView.findViewById(R.id.Sales_bAll);
        bAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDateFilter.setText("[No Date Selected]");
                listener.onClickAllRecords();
            }
        });


        tvDateFilter = (TextView) rootView.findViewById(R.id.Sales_tvDateFilter);
        tvDateFilter.setText(formatter.format(new Date()));

        tvTotalSales = (TextView) rootView.findViewById(R.id.Sales_tvTotalSales);

        listener.onSalesFragmentViewReady();

        return rootView;
    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dpFilterDate = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDateFilter.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dpFilterDate.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.onSalesFragmentViewReady();
            }
        });

    }

    public Date getFilterDate(){

        return java.sql.Date.valueOf(tvDateFilter.getText().toString());

    }

    public void setTotalSalesAmount(float totalSalesAmount){

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        tvTotalSales.setText(decimalFormat.format(totalSalesAmount));

    }

    public void setSalesList(List<Sales> salesList){

        if(salesListAdapter != null){
            salesListAdapter.setSalesList(salesList);
        }

    }

    public void setSalesProductList(List<SalesProduct> salesProductList){

        if(salesProductWithReturnListAdapter != null){
            salesProductWithReturnListAdapter.setSalesProducts(salesProductList);
        }

    }

    public int getSelectedSalesProductCount(){

        return lvSalesProducts.getCheckedItemCount();

    }

    public List<SalesProduct> getSelectedSalesProducts(){
        List<SalesProduct> salesProducts = new ArrayList<>();
        SparseBooleanArray checked = lvSalesProducts.getCheckedItemPositions();

        for (int i = 0; i < salesProductWithReturnListAdapter.getCount(); i++) {
            if (checked.get(i)) {
                salesProducts.add((SalesProduct) salesProductWithReturnListAdapter.getItem(i));
            }
        }

        return salesProducts;
    }

    public interface SalesFragmentListener{

        void onSalesFragmentViewReady();

        void onClickAllRecords();

        void onClickSales(Sales sales);

    }


}
