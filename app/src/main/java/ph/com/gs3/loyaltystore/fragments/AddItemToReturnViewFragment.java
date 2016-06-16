package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.loyaltystore.AddItemToReturnActivity;
import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class AddItemToReturnViewFragment extends Fragment {

    public static final String TAG = AddItemToReturnViewFragment.class.getSimpleName();

    public static final String RETURNABLE_TRAY = "Tray";
    public static final String RETURNABLE_SPOILAGE = "Spoilage";
    public static final String RETURNABLE_ITEM = "Item";
    public static final String RETURNABLE_CASH = "Cash";

    public static final String CASH_TYPE_VALUE_CASH_ON_HAND = "Cash on Hand";
    public static final String CASH_TYPE_VALUE_CASH_ON_BANK = "Cash on Bank";

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private AddItemToReturnViewFragmentEventListener addItemToReturnViewFragmentEventListener;

    private Activity activity;

    private Spinner sProduct;
    private Spinner sType;
    private Spinner sCashType;

    private EditText etQuantityOrAmount;
    private EditText etRemarks;
    private EditText etBank;
    private EditText etTimeOfDeposit;

    private TextView tvDateTimeOfDeposit;

    private Button bSave;
    private Button bCancel;
    private Button bChangeDateTimeOfDeposit;

    private DatePicker dpDateOfDeposit;
    private TimePicker tpTimeOfDeposit;

    private ImageView ivDepositSlip;

    private LinearLayout llProduct;
    private LinearLayout llCashType;
    private LinearLayout llBankDepositDetails;
    private LinearLayout llDateTimeOfDepositPicker;
    private LinearLayout llDateTimeOfDeposit;

    private Button bSelectImage;

    private List<String> spinnerReturnableArray;
    private List<String> spinnerProductArray;
    private List<String> spinnerCashTypeArray;

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

        spinnerReturnableArray = new ArrayList<String>();
        //spinnerReturnableArray.add(RETURNABLE_TRAY);
        //spinnerReturnableArray.add(RETURNABLE_SPOILAGE);
        spinnerReturnableArray.add(RETURNABLE_ITEM);
        spinnerReturnableArray.add(RETURNABLE_CASH);

        final ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_item, spinnerReturnableArray);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType = (Spinner) rootView.findViewById(R.id.ITR_sType);
        sType.setAdapter(itemAdapter);

        sType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemToReturnViewFragmentEventListener.onItemSelect(itemAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCashTypeArray = new ArrayList<String>();
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
        setProductToList();

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

        dpDateOfDeposit = (DatePicker) rootView.findViewById(R.id.ITR_dpDateOfDeposit);
        tpTimeOfDeposit = (TimePicker) rootView.findViewById(R.id.ITR_tpTimeOfDeposit);


        llDateTimeOfDeposit = (LinearLayout) rootView.findViewById(R.id.ITR_llDateTimeOfDeposit);
        llDateTimeOfDepositPicker = (LinearLayout) rootView.findViewById(R.id.ITR_llDateTimeOfDepositPicker);

        tvDateTimeOfDeposit = (TextView) rootView.findViewById(R.id.ITR_tvDateTimeOfDeposit);
        bChangeDateTimeOfDeposit = (Button) rootView.findViewById(R.id.ITR_bChangeDateTimeOfDeposit);
        bChangeDateTimeOfDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDateTimeOfDeposit.setVisibility(View.GONE);
                llDateTimeOfDepositPicker.setVisibility(View.VISIBLE);
            }
        });

        if (activity.getIntent().hasExtra(AddItemToReturnActivity.EXTRA_ITEM_RETURN_ID) &&
                activity.getIntent().hasExtra(AddItemToReturnActivity.EXTRA_ITEM_RETURN_VALUE)) {

            long itemReturnId =  activity.getIntent().getLongExtra(AddItemToReturnActivity.EXTRA_ITEM_RETURN_ID,-1);
            String itemReturnValue = activity.getIntent().getStringExtra(AddItemToReturnActivity.EXTRA_ITEM_RETURN_VALUE);

            onEditItemReturn(itemReturnValue,itemReturnId);

        }

        addItemToReturnViewFragmentEventListener.onViewReady();


        return rootView;
    }

    private void onEditItemReturn(String item, long id) {

        ItemReturnDao itemReturnDao = LoyaltyStoreApplication.getSession().getItemReturnDao();
        CashReturnDao cashReturnDao = LoyaltyStoreApplication.getSession().getCashReturnDao();

        sType.setSelection(getItemIndexInSpinner(spinnerReturnableArray, item));

        if (item.equals(RETURNABLE_TRAY) || item.equals(RETURNABLE_SPOILAGE) || item.equals(RETURNABLE_ITEM)) {

            List<ItemReturn> itemReturnList =
                    itemReturnDao.queryBuilder().where(ItemReturnDao.Properties.Id.eq(id)).list();

            for (ItemReturn itemReturn : itemReturnList) {

                etQuantityOrAmount.setText(String.valueOf(itemReturn.getQuantity()));
                etRemarks.setText(itemReturn.getRemarks());

                if (item.equals(RETURNABLE_SPOILAGE) || item.equals(RETURNABLE_ITEM)) {

                    sProduct.setVisibility(View.VISIBLE);
                    sProduct.setSelection(getItemIndexInSpinner(
                            spinnerProductArray,
                            itemReturn.getProduct_name()
                    ));


                }

            }


        } else if (item.equals(RETURNABLE_CASH)) {

            List<CashReturn> cashReturns =
                    cashReturnDao.queryBuilder().where(CashReturnDao.Properties.Id.eq(id)).list();

            for(CashReturn cashReturn : cashReturns){

                sCashType.setSelection(getItemIndexInSpinner(spinnerCashTypeArray,cashReturn.getType()));
                etQuantityOrAmount.setText(String.valueOf(cashReturn.getAmount()));
                etRemarks.setText(cashReturn.getRemarks());

                if(cashReturn.getType().equals(CASH_TYPE_VALUE_CASH_ON_BANK)){

                    llBankDepositDetails.setVisibility(View.VISIBLE);

                    etBank.setText(cashReturn.getDeposited_to_bank());

                    llDateTimeOfDepositPicker.setVisibility(View.GONE);
                    llDateTimeOfDeposit.setVisibility(View.VISIBLE);

                    tvDateTimeOfDeposit.setText(formatter.format(cashReturn.getTime_of_deposit()));

                }

            }

        }

    }

    private int getItemIndexInSpinner(List<String> spinnerArray, String item) {

        int spinnerItemIndex = -1;

        for (int i = 0; i < spinnerArray.size(); i++) {

            if (spinnerArray.get(i).equals(item)) {
                spinnerItemIndex = i;
            }

        }
        return spinnerItemIndex;

    }


    public String getRemarks() {

        return etRemarks.getText().toString();

    }

    public void setProductToList() {

        ProductDao productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

        spinnerProductArray = new ArrayList<>();

        spinnerProductArray.add("Tray");

        List<Product> products =
                productDao
                        .queryBuilder()
                        .whereOr(
                                ProductDao.Properties.Type.eq("Product for Delivery"),
                                ProductDao.Properties.Type.eq("For Direct Transactions")
                        ).list();

       for(Product product : products){
           spinnerProductArray.add(product.getName());
       }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_item, spinnerProductArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sProduct.setAdapter(adapter);

    }

    public void setFieldsVisibilityByItem(String item) {
        switch (item) {
            case RETURNABLE_TRAY:

                llProduct.setVisibility(View.GONE);
                llCashType.setVisibility(View.GONE);
                llBankDepositDetails.setVisibility(View.GONE);

                break;
            case RETURNABLE_SPOILAGE:

                llProduct.setVisibility(View.VISIBLE);
                llCashType.setVisibility(View.GONE);
                llBankDepositDetails.setVisibility(View.GONE);

                setProductToList();

                break;
            case RETURNABLE_ITEM:

                llProduct.setVisibility(View.VISIBLE);
                llCashType.setVisibility(View.GONE);
                llBankDepositDetails.setVisibility(View.GONE);

                setProductToList();

                break;
            case RETURNABLE_CASH:

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

    public String getType() {

        Log.d(TAG, "Selected Type : " +  sType.getSelectedItem().toString());

        return sType.getSelectedItem().toString();

    }

    public String getCashType() {

        return sCashType.getSelectedItem().toString();

    }

    public String getQuantityOrAmount() {

        return etQuantityOrAmount.getText().toString();

    }

    public void setQuantityOrAmountError(String errorMessage){

        etQuantityOrAmount.setError(errorMessage);

    }

    public String getProduct() {

        return sProduct.getSelectedItem().toString();

    }

    public String getBank() {

        return etBank.getText().toString();

    }

    public Date getDepositDateTime() {

        int day = dpDateOfDeposit.getDayOfMonth();
        int month = dpDateOfDeposit.getMonth();
        int year = dpDateOfDeposit.getYear();
        int hour;
        if (Build.VERSION.SDK_INT >= 23)
            hour = tpTimeOfDeposit.getHour();
        else
            hour = tpTimeOfDeposit.getCurrentHour();

        int minute;

        if (Build.VERSION.SDK_INT >= 23)
            minute = tpTimeOfDeposit.getMinute();
        else
            minute = tpTimeOfDeposit.getCurrentMinute();


        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        return calendar.getTime();

    }


    public boolean validate(String item) {

        boolean isValid = true;

        if ("".equals(etQuantityOrAmount.getText().toString().trim())) {
            etQuantityOrAmount.setError("This field is required.");
            isValid = false;
        }

        if (item.equals(RETURNABLE_CASH)) {

            if ((sCashType.getSelectedItem().toString().equals(CASH_TYPE_VALUE_CASH_ON_BANK))) {

                if ("".equals(etBank.getText().toString().trim())) {

                    etBank.setError("This field is required.");
                    isValid = false;

                }

            }


        }

        return isValid;

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
