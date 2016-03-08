package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Locale;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.SalesListAdapter;
import ph.com.gs3.loyaltystore.adapters.SalesProductListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;

/**
 * Created by Bryan-PC on 18/02/2016.
 */
public class SalesViewFragment extends Fragment {

    public static final String TAG = SalesViewFragment.class.getSimpleName();

    private ListView lvSales;
    private ListView lvSalesProducts;

    private Activity mActivity;

    private SalesListAdapter salesListAdapter;
    private SalesProductListAdapter salesProductListAdapter;

    private SalesViewFragmentListener salesViewFragmentListener;

    private DatePickerDialog dpFilterDate;

    private Button bSelectDate;
    private Button bAllRecords;

    private TextView tvDateFilter;
    private TextView tvTotalSales;

    public static SalesViewFragment createInstance(
            SalesListAdapter salesListAdapter, SalesProductListAdapter salesProductListAdapter
    ){
        SalesViewFragment salesViewFragment = new SalesViewFragment();
        salesViewFragment.salesListAdapter = salesListAdapter;
        salesViewFragment.salesProductListAdapter = salesProductListAdapter;
        return salesViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            salesViewFragmentListener = (SalesViewFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement SalesViewFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales, container, false);

        setDateTimeField();

        lvSales = (ListView) rootView.findViewById(R.id.Sales_lvSales);
        lvSales.setAdapter(salesListAdapter);
        lvSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                salesViewFragmentListener.onViewOrder(
                        (Sales) salesListAdapter.getItem(position)
                );
            }
        });

        lvSalesProducts = (ListView) rootView.findViewById(R.id.Sales_lvSalesProducts);
        lvSalesProducts.setAdapter(salesProductListAdapter);
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
                salesViewFragmentListener.onViewReady();
            }
        });

        tvDateFilter = (TextView) rootView.findViewById(R.id.Sales_tvDateFilter);
        tvTotalSales = (TextView) rootView.findViewById(R.id.Sales_tvTotalSales);

        salesViewFragmentListener.onViewReady();

        return rootView;
    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dpFilterDate = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDateFilter.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dpFilterDate.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                salesViewFragmentListener.onDateSelected();
            }
        });

    }

    public String getFilterDate(){

        return tvDateFilter.getText().toString();

    }

    public void setTotalSalesAmount(float totalSalesAmount){

        Constants constants = new Constants();
        DecimalFormat decimalFormat = constants.DECIMAL_FORMAT;

        tvTotalSales.setText(decimalFormat.format(totalSalesAmount));

    }

    public interface SalesViewFragmentListener {

        void onViewReady();

        void onViewOrder(Sales sales);

        void onDateSelected();
    }

}
