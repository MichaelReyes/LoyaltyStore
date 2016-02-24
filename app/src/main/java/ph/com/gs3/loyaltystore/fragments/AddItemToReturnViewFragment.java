package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class AddItemToReturnViewFragment extends Fragment {

    public static final String TAG = AddItemToReturnViewFragment.class.getSimpleName();

    private static final String ITEM_VALUE_TRAY = "Tray";
    private static final String ITEM_VALUE_SPOILAGE = "Spoilage";
    private static final String ITEM_VALUE_CASH = "Cash";

    private static final String CASH_TYPE_VALUE_CASH_ON_HAND = "Cash on Hand";
    private static final String CASH_TYPE_VALUE_CASH_ON_BANK = "Cash on Bank";

    private AddItemToReturnViewFragmentEventListener addItemToReturnViewFragmentEventListener;

    private Activity activity;

    private Spinner sProduct;
    private Spinner sItem;
    private Spinner sCashType;

    private EditText etQuantityOrAmount;
    private EditText etRemarks;
    private EditText etBank;
    private EditText etTimeOfDeposit;

    private Button bSave;
    private Button bCancel;

    private ImageView ivDepositSlip;

    private LinearLayout llProduct;
    private LinearLayout llCashType;
    private LinearLayout llBankDepositDetails;

    private Button bSelectImage;

    private View rootView;

    public static AddItemToReturnViewFragment createInstance() {
        AddItemToReturnViewFragment addItemToReturnViewFragment = new AddItemToReturnViewFragment();
        return addItemToReturnViewFragment;
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (Activity) activity;

        try {
            addItemToReturnViewFragmentEventListener = (AddItemToReturnViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement ItemsToReturnViewFragmentEventListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_item_to_return, container, false);

        List<String> spinnerItemArray = new ArrayList<String>();
        spinnerItemArray.add(ITEM_VALUE_TRAY);
        spinnerItemArray.add(ITEM_VALUE_SPOILAGE);
        spinnerItemArray.add(ITEM_VALUE_CASH);

        final ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_item, spinnerItemArray);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItem = (Spinner) rootView.findViewById(R.id.ITR_sItem);
        sItem.setAdapter(itemAdapter);

        sItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemToReturnViewFragmentEventListener.onItemSelect(itemAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> spinnerCashTypeArray = new ArrayList<String>();
        spinnerCashTypeArray.add(CASH_TYPE_VALUE_CASH_ON_HAND);
        spinnerCashTypeArray.add(CASH_TYPE_VALUE_CASH_ON_BANK);

        final ArrayAdapter<String> cashTypeAdapter = new ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_item, spinnerCashTypeArray);
        cashTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCashType = (Spinner) rootView.findViewById(R.id.ITR_sCashType);
        sCashType.setAdapter(cashTypeAdapter);

        sCashType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemToReturnViewFragmentEventListener.onCashTypeSelect(cashTypeAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sProduct = (Spinner) rootView.findViewById(R.id.ITR_sProduct);

        etQuantityOrAmount = (EditText) rootView.findViewById(R.id.ITR_etQuantityOrAmount);
        etRemarks = (EditText) rootView.findViewById(R.id.ITR_etRemarks);

        etBank = (EditText) rootView.findViewById(R.id.ITR_etBank);
        etTimeOfDeposit = (EditText) rootView.findViewById(R.id.ITR_etTimeOfDeposit);
        ivDepositSlip = (ImageView) rootView.findViewById(R.id.ITR_ivDepositSlip);

        llProduct = (LinearLayout) rootView.findViewById(R.id.ITR_llProduct);

        llCashType = (LinearLayout) rootView.findViewById(R.id.ITR_llCashType);

        llBankDepositDetails = (LinearLayout) rootView.findViewById(R.id.ITR_llBankDepositDetails);

        bSelectImage = (Button) rootView.findViewById(R.id.ITR_bSelectImage);
        bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToReturnViewFragmentEventListener.onSelectImage();
            }
        });

        bSave = (Button) rootView.findViewById(R.id.ITR_bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToReturnViewFragmentEventListener.onSave();
            }
        });

        bCancel = (Button) rootView.findViewById(R.id.ITR_bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToReturnViewFragmentEventListener.onCancel();
            }
        });

        addItemToReturnViewFragmentEventListener.onViewReady();


        return rootView;
    }


    public String getRemarks() {

        return etRemarks.getText().toString();

    }

    public void setProductToList() {

        ProductDao productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

        List<String> spinnerArray = new ArrayList<String>();

        String sql =
                "SELECT " + ProductDao.Properties.Name.columnName + " FROM " + ProductDao.TABLENAME;

        Cursor cursor = productDao.getDatabase().rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {

                spinnerArray.add(cursor.getString(0));

            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sProduct.setAdapter(adapter);


    }

    public void setFieldsVisibilityByItem(String item) {
        switch (item) {
            case ITEM_VALUE_TRAY:

                llProduct.setVisibility(View.GONE);
                llCashType.setVisibility(View.GONE);
                llBankDepositDetails.setVisibility(View.GONE);

                break;
            case ITEM_VALUE_SPOILAGE:

                llProduct.setVisibility(View.VISIBLE);
                llCashType.setVisibility(View.GONE);
                llBankDepositDetails.setVisibility(View.GONE);

                setProductToList();

                break;
            case ITEM_VALUE_CASH:

                llProduct.setVisibility(View.GONE);
                llCashType.setVisibility(View.VISIBLE);
                llBankDepositDetails.setVisibility(View.GONE);

                break;
        }

    }

    public void setBankDetailsVisibility(String cashType) {

        switch (cashType) {

            case CASH_TYPE_VALUE_CASH_ON_HAND:
                llBankDepositDetails.setVisibility(View.GONE);
                break;

            case CASH_TYPE_VALUE_CASH_ON_BANK:
                llBankDepositDetails.setVisibility(View.VISIBLE);
                break;

        }

    }

    public void setImage(Bitmap bitmap) {
        ivDepositSlip.setImageBitmap(bitmap);
    }

    public void save(Retailer retailer) {

        String item = sItem.getSelectedItem().toString();

        ItemReturnDao itemReturnDao =
                LoyaltyStoreApplication.getInstance().getSession().getItemReturnDao();

        ItemReturn itemReturn = new ItemReturn();

        CashReturnDao cashReturnDao =
                LoyaltyStoreApplication.getInstance().getSession().getCashReturnDao();

        CashReturn cashReturn = new CashReturn();

        switch (item) {
            case ITEM_VALUE_TRAY:

                Log.d("SAVING", "SAVING");

                itemReturn.setItem(item);
                itemReturn.setQuantity(Float.valueOf(etQuantityOrAmount.getText().toString()));
                itemReturn.setRemarks(etRemarks.getText().toString());
                itemReturn.setIs_synced(false);
                itemReturn.setStore_id(retailer.getStoreId());

                itemReturnDao.insert(itemReturn);

                break;
            case ITEM_VALUE_SPOILAGE:

                itemReturn.setItem(item);
                itemReturn.setProduct_name(sProduct.getSelectedItem().toString());
                itemReturn.setQuantity(Float.valueOf(etQuantityOrAmount.getText().toString()));
                itemReturn.setRemarks(etRemarks.getText().toString());
                itemReturn.setIs_synced(false);
                itemReturn.setStore_id(retailer.getStoreId());

                itemReturnDao.insert(itemReturn);

                break;
            case ITEM_VALUE_CASH:

                cashReturn.setItem(item);
                cashReturn.setAmount(Float.valueOf(etQuantityOrAmount.getText().toString()));
                cashReturn.setRemarks(etRemarks.getText().toString());
                cashReturn.setType(sCashType.getSelectedItem().toString());
                cashReturn.setIs_synced(false);
                cashReturn.setStore_id(retailer.getStoreId());

                if(sCashType.getSelectedItem().equals(CASH_TYPE_VALUE_CASH_ON_BANK)){

                    cashReturn.setDeposited_to_bank(etBank.getText().toString());
                    cashReturn.setTime_of_deposit(new Date());

                }

                cashReturnDao.insert(cashReturn);

                break;
        }

    }

    public interface AddItemToReturnViewFragmentEventListener {

        void onItemSelect(String item);

        void onCashTypeSelect(String cashType);

        void onViewReady();

        void onSelectImage();

        void onSave();

        void onCancel();

    }

}
