package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.CashReturnListAdapter;
import ph.com.gs3.loyaltystore.adapters.ItemReturnListAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;

/**
 * Created by Bryan-PC on 27/04/2016.
 */
public class ReturnsToCommissaryFragment extends Fragment {

    public static final String TAG = ReturnsToCommissaryFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    private ItemReturnListAdapter itemReturnListAdapter;
    private CashReturnListAdapter cashReturnListAdapter;

    private List<ItemReturn> itemReturnList;
    private List<CashReturn> cashReturnList;

    private ListView lvItemReturn;
    private ListView lvCashReturn;

    private DatePickerDialog dpFilterDate;

    private TextView tvDateFilter;

    private Button bSelectDate;
    private Button bAdd;

    private SimpleDateFormat formatter;

    private ReturnsToCommissaryViewFragmentListener listener;

    public ReturnsToCommissaryFragment createInstance(){
        ReturnsToCommissaryFragment returnsToCommissaryFragment = new ReturnsToCommissaryFragment();
        return returnsToCommissaryFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if(context instanceof  ReturnsToCommissaryViewFragmentListener){
            listener = (ReturnsToCommissaryViewFragmentListener) context;
        }else{
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement ReturnsToCommissaryViewFragmentListener");
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.activity = (FragmentActivity) activity;

        if(activity instanceof  ReturnsToCommissaryViewFragmentListener){
            listener = (ReturnsToCommissaryViewFragmentListener) activity;
        }else{
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement ReturnsToCommissaryViewFragmentListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "ReturnsToCommissaryViewFragment on Resume ");

        listener.onReturnToCommissaryViewFragmentReady();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_returns_to_commissary,container,false);

        formatter = Constants.SIMPLE_DATE_FORMAT;

        itemReturnList= new ArrayList<>();
        itemReturnListAdapter = new ItemReturnListAdapter(context,itemReturnList);

        cashReturnList = new ArrayList<>();
        cashReturnListAdapter = new CashReturnListAdapter(context,cashReturnList);

        lvItemReturn = (ListView) rootView.findViewById(R.id.ReturnsToCommissary_lvItems);
        lvItemReturn.setAdapter(itemReturnListAdapter);
        lvItemReturn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemReturn itemReturn = (ItemReturn) itemReturnListAdapter.getItem(position);

                listener.onEditItemReturn(itemReturn);
            }
        });

        lvCashReturn = (ListView) rootView.findViewById(R.id.ReturnsToCommissary_lvCash);
        lvCashReturn.setAdapter(cashReturnListAdapter);
        lvCashReturn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CashReturn cashReturn = (CashReturn) cashReturnListAdapter.getItem(position);

                listener.onEditCashReturn(cashReturn);
            }
        });

        bAdd = (Button) rootView.findViewById(R.id.ReturnsToCommissary_bAdd);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddReturns();
            }
        });

        setDateTimeField();

        tvDateFilter = (TextView) rootView.findViewById(R.id.ReturnsToCommissary_tvDateFilter);
        tvDateFilter.setText(formatter.format(new Date()));

        bSelectDate = (Button) rootView.findViewById(R.id.ReturnsToCommissary_bSelectDate);
        bSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpFilterDate.show();
            }
        });

        listener.onReturnToCommissaryViewFragmentReady();

        return rootView;
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
                //listener.onLoadItemInventoryStockCount(java.sql.Date.valueOf(tvDateFilter.getText().toString()));
                listener.onReturnToCommissaryViewFragmentReady();
            }
        });

    }

    public void setItemReturnList(List<ItemReturn> itemReturnList){

        if(itemReturnListAdapter != null){
            itemReturnListAdapter.setItemReturnList(itemReturnList);
        }

    }

    public void addItemReturn(ItemReturn itemReturn){
        if (itemReturnListAdapter != null) {
            itemReturnListAdapter.addItemReturn(itemReturn);
        }
    }

    public void setCashReturnList(List<CashReturn> cashReturnList){

        if(cashReturnListAdapter != null){
            cashReturnListAdapter.setCashReturnList(cashReturnList);
        }

    }

    public void addCashReturn(CashReturn cashReturn){
        if (cashReturnListAdapter != null) {
            cashReturnListAdapter.addCashReturn(cashReturn);
        }
    }

    public Date getDateFilter(){

        return java.sql.Date.valueOf(tvDateFilter.getText().toString());

    }


    public interface ReturnsToCommissaryViewFragmentListener{
        void onReturnToCommissaryViewFragmentReady();

        void onAddReturns();

        void onEditItemReturn(ItemReturn itemReturn);

        void onEditCashReturn(CashReturn cashReturn);

    }
}
