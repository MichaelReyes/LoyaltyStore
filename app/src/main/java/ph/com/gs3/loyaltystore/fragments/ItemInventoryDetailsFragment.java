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
import ph.com.gs3.loyaltystore.adapters.ItemStockCountListViewAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;

/**
 * Created by Bryan-PC on 14/04/2016.
 */
public class ItemInventoryDetailsFragment extends Fragment {

    public static final String TAG = ItemInventoryDetailsFragment.class.getSimpleName();

    private Context context;

    private ItemInventoryDetailsFragmentListener listener;

    private ItemStockCountListViewAdapter adapter;

    private ListView lvItems;

    private DatePickerDialog dpFilterDate;

    private Button bSelectDate;
    private Button bAllRecords;

    private TextView tvDateFilter;

    private SimpleDateFormat formatter;

    private View v;

    public static ItemInventoryDetailsFragment newInstance() {
        ItemInventoryDetailsFragment fragment = new ItemInventoryDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof ItemInventoryDetailsFragmentListener) {
            listener = (ItemInventoryDetailsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemInventoryDetailsFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_inventory_details, container, false);

        v = rootView;

        Log.d(TAG,"ItemInventoryDetailsFragment CREATED");

        adapter = new ItemStockCountListViewAdapter(context);

        formatter = Constants.SIMPLE_DATE_FORMAT;

        lvItems = (ListView) rootView.findViewById(R.id.ItemInventoryDetails_lvItems);
        lvItems.setAdapter(adapter);

        setDateTimeField();

        tvDateFilter = (TextView) v.findViewById(R.id.ItemInventoryDetails_tvDateFilter);
        tvDateFilter.setText(formatter.format(new Date()));

        bSelectDate = (Button) v.findViewById(R.id.ItemInventoryDetails_bSelectDate);
        bSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpFilterDate.show();
            }
        });

        bAllRecords = (Button) v.findViewById(R.id.ItemInventoryDetails_bAll);
        bAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDateFilter.setText("[No Date Selected]");
                listener.onLoadItemInventoryStockCount(null);
            }
        });

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
                listener.onLoadItemInventoryStockCount(java.sql.Date.valueOf(tvDateFilter.getText().toString()));
            }
        });

    }

    public void setItems(List<ItemStockCount> items){

        if(adapter !=null){
            adapter.setItemStockCountList(items);
        }

    }

    public Date getDateFilter(){

        return java.sql.Date.valueOf(tvDateFilter.getText().toString());

    }

    public interface ItemInventoryDetailsFragmentListener {

        void onLoadItemInventoryStockCount(Date date);

    }

}
