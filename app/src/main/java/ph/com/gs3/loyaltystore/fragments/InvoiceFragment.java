package ph.com.gs3.loyaltystore.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.DynamicMenuButtonListViewAdapter;
import ph.com.gs3.loyaltystore.adapters.SalesProductListViewAdapter;
import ph.com.gs3.loyaltystore.adapters.objects.MenuRowItem;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.TabMaintenance;
import ph.com.gs3.loyaltystore.models.sqlite.dao.DaoSession;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

/**
 * Created by Bryan-PC on 22/04/2016.
 */
public class InvoiceFragment extends Fragment implements
        View.OnClickListener {

    public static final String TAG = InvoiceFragment.class.getSimpleName();

    private View v;

    private Context context;
    private FragmentActivity activity;

    private ListView lvSalesProducts;
    private SalesProductListViewAdapter salesProductListViewAdapter;

    private InvoiceViewFragmentListener listener;

    private DynamicMenuButtonListViewAdapter dynamicMenuButtonListViewAdapter;

    private Button bSearch;
    private Button bCheckout;
    private Button bClear;
    private Button bRefresh;
    private Button bQrCodeReader;

    private CheckBox cbSenior;

    private ListView lvMenu;

    private Spinner sCategories;

    private List<String> categoryList;

    private TextView tvTotal;

    private EditText etSearch;

    private String searchString = "";

    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentTotalItemCount;
    private int currentScrollState;
    private int totalItemCount;

    public InvoiceFragment createInstance() {
        InvoiceFragment invoiceFragment = new InvoiceFragment();
        return invoiceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Log.d(TAG, "ON CREATE Invoice Fragment");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
        this.context = activity;

        if (activity instanceof InvoiceViewFragmentListener) {
            listener = (InvoiceViewFragmentListener) activity;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement InvoiceViewFragmentListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;

        if (activity instanceof InvoiceViewFragmentListener) {
            listener = (InvoiceViewFragmentListener) activity;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement InvoiceViewFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_invoice, container, false);
        v = rootView;

        initializeViews();

        listener.onInvoiceFragmentViewReady();

        return rootView;
    }

    private void initializeViews() {

        salesProductListViewAdapter = new SalesProductListViewAdapter(context);

        dynamicMenuButtonListViewAdapter = new DynamicMenuButtonListViewAdapter(context);
        lvMenu = (ListView) v.findViewById(R.id.Invoice_lvMenu);
        lvMenu.setAdapter(dynamicMenuButtonListViewAdapter);

        lvMenu.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;

                if (currentTotalItemCount == (currentVisibleItemCount + currentFirstVisibleItem))
                    isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
                currentTotalItemCount = totalItemCount;
            }
        });

        lvSalesProducts = (ListView) v.findViewById(R.id.Invoice_lvSalesProductList);
        lvSalesProducts.setAdapter(salesProductListViewAdapter);
        lvSalesProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final SalesProduct salesProduct = (SalesProduct) salesProductListViewAdapter.getItem(position);

                if (salesProduct.getQuantity() <= 1) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("");
                    builder.setMessage("Are you sure you want to delete this record?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onRemoveSalesProduct(salesProduct);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {
                    listener.onDeductSalesProduct(salesProduct);
                }

            }
        });

        tvTotal = (TextView) v.findViewById(R.id.Invoice_tvTotal);

        etSearch = (EditText) v.findViewById(R.id.Invoice_etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InvoiceFragment.this.searchString = etSearch.getText().toString();
                listener.onSearchProduct(etSearch.getText().toString(), sCategories.getSelectedItem().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bSearch = (Button) v.findViewById(R.id.Invoice_bSearch);
        bSearch.setOnClickListener(this);
        bCheckout = (Button) v.findViewById(R.id.Invoice_bCheckout);
        bCheckout.setOnClickListener(this);
        bClear = (Button) v.findViewById(R.id.Invoice_bClear);
        bClear.setOnClickListener(this);
        bRefresh = (Button) v.findViewById(R.id.Invoice_bRefresh);
        bRefresh.setOnClickListener(this);
        bQrCodeReader = (Button) v.findViewById(R.id.Invoice_bQrCodeReader);
        bQrCodeReader.setOnClickListener(this);
        bQrCodeReader.setVisibility(View.GONE);

        cbSenior = (CheckBox) v.findViewById(R.id.Invoice_cbSenior);
        cbSenior.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onSeniorChecked(isChecked);
            }
        });

        TabMaintenance tabMaintenance = TabMaintenance.getTabMaintenanceFromSharedPreferences(context);
        if (!tabMaintenance.isSynchronizeByWebTabActive()) {
            cbSenior.setVisibility(View.GONE);
        }

        categoryList = new ArrayList<>();
        categoryList.add("None");
        if (getProductCategories().size() > 1)
            categoryList.addAll(getProductCategories());

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_item, categoryList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sCategories = (Spinner) v.findViewById(R.id.Invoice_sCategories);
        sCategories.setAdapter(categoriesAdapter);
        sCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onSearchProduct(etSearch.getText().toString(), sCategories.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void isScrollCompleted() {

        if (this.currentVisibleItemCount > 0 && this.currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

            listener.onLoadMoreProducts();

        }
    }

    @Override
    public void onClick(View v) {
        if (v == bSearch) {
            searchProduct();
        } else if (v == bCheckout) {
            checkout();
        } else if (v == bClear) {
            clearSalesProductList();
        } else if (v == bRefresh) {
            refreshProductMenuList();
        } else if (v == bQrCodeReader) {
            onReadQrCode();
        }
    }

    private void onReadQrCode() {
        //listener.onReadQRCode();
    }

    private void searchProduct() {

        String searchString = etSearch.getText().toString();
        this.searchString = searchString;
        listener.onSearchProduct(searchString, sCategories.getSelectedItem().toString());


    }

    private void checkout() {
        listener.onCheckOut();
    }

    private void clearSalesProductList() {
        listener.onClearProductList();
    }

    private void refreshProductMenuList() {
        searchString = "";
        listener.onRefreshProductMenuList();
    }


    public List<SalesProduct> getSalesProducts() {
        return salesProductListViewAdapter.getSalesProducts();
    }


    public Float getTotalAmount() {

        String totalAmountString = tvTotal.getText().toString();

        float totalAmount = Float.parseFloat(totalAmountString.replace(",", ""));

        return totalAmount;

    }

    public void setTotalAmount(Float amount) {

        DecimalFormat decimalFormat = Constants.DECIMAL_FORMAT;

        tvTotal.setText(decimalFormat.format(amount));

    }

    public void setSalesProducts(List<SalesProduct> salesProductList) {
        List<SalesProduct> salesProducts = new ArrayList<>();
        salesProducts.addAll(salesProductList);
        salesProductListViewAdapter.setSalesProducts(salesProducts);
    }

    public void addSalesProduct(SalesProduct salesProduct) {
        salesProductListViewAdapter.addSalesProduct(salesProduct);
    }

    public SalesProduct getSalesProduct(int position) {
        return (SalesProduct) salesProductListViewAdapter.getItem(position);
    }

    public void updateSalesProductList() {
        salesProductListViewAdapter.notifyDataSetChanged();
    }

    private List<String> getProductCategories() {

        DaoSession session = LoyaltyStoreApplication.getSession();

        String sql = "SELECT DISTINCT " +
                ProductDao.Properties.Category.columnName +
                " FROM " + ProductDao.TABLENAME + " WHERE " +
                ProductDao.Properties.Type.columnName + "='Product for Retail' OR " +
                ProductDao.Properties.Type.columnName + "='For Direct Transactions'";

        List<String> result = new ArrayList<>();

        Cursor cursor = session.getDatabase().rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(0) != null && !"".equals(cursor.getString(0)))
                        result.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;

    }

    private List<Product> getProductInListByCategory(List<Product> productList, String category) {

        List<Product> productsByCategory = new ArrayList<>();

        for (Product product : productList) {

            if(product.getCategory() != null) {
                if (product.getCategory().trim().equals(category.trim())) {
                    productsByCategory.add(product);
                }
            }
        }

        return productsByCategory;

    }

    public void setProducts(List<Product> products) {

        ArrayList<Object> objectArrayList = new ArrayList<>();

        List<String> categories = getProductCategories();

        totalItemCount = 0;

        if (categories.size() > 1) {

            for (String category : categories) {

                List<Product> productsByCategory = getProductInListByCategory(products, category);

                if (productsByCategory.size() > 0) {
                    objectArrayList.add(category);
                }

                int itemPerRowLimit = 3;

                List<MenuRowItem> menuRowItemList = new ArrayList<>();

                MenuRowItem menuRowItem = new MenuRowItem();

                for (Product product : productsByCategory) {

                    if (itemPerRowLimit > 0) {
                        switch (itemPerRowLimit) {
                            case 3:
                                menuRowItem.setProduct1(product);
                                break;
                            case 2:
                                menuRowItem.setProduct2(product);
                                break;
                            case 1:
                                menuRowItem.setProduct3(product);
                                break;
                        }
                    } else {
                        menuRowItemList.add(menuRowItem);
                        totalItemCount++;
                        objectArrayList.add(menuRowItem);
                        menuRowItem = new MenuRowItem();
                        itemPerRowLimit = 3;
                        menuRowItem.setProduct1(product);
                    }

                    itemPerRowLimit--;

                }

                if (productsByCategory.size() > 0) {
                    objectArrayList.add(menuRowItem);
                    menuRowItemList.add(menuRowItem);
                    totalItemCount++;
                }

                //objectArrayList.add(menuRowItemList);
            }

        } else {

            //Log.d(TAG, "in else : " + products.size());

            int itemPerRowLimit = 3;

            List<MenuRowItem> menuRowItemList = new ArrayList<>();

            MenuRowItem menuRowItem = new MenuRowItem();

            for (Product product : products) {

                if (itemPerRowLimit > 0) {
                    switch (itemPerRowLimit) {
                        case 3:
                            menuRowItem.setProduct1(product);
                            break;
                        case 2:
                            menuRowItem.setProduct2(product);
                            break;
                        case 1:
                            menuRowItem.setProduct3(product);
                            break;
                    }
                } else {
                    menuRowItemList.add(menuRowItem);
                    totalItemCount++;
                    objectArrayList.add(menuRowItem);
                    menuRowItem = new MenuRowItem();
                    itemPerRowLimit = 3;
                    menuRowItem.setProduct1(product);
                }

                itemPerRowLimit--;

            }

            if (products.size() > 0) {
                objectArrayList.add(menuRowItem);
                menuRowItemList.add(menuRowItem);
                totalItemCount++;
            }

            //Log.d(TAG, "array list size : " + objectArrayList.size());
        }


        if (dynamicMenuButtonListViewAdapter != null) {
            dynamicMenuButtonListViewAdapter.setMenuRow(objectArrayList);
        }


    }

    public boolean isSenior() {

        return cbSenior.isChecked();

    }

    public void resetView() {

        initializeViews();

    }

    public String getSelectedCategory() {

        return sCategories.getSelectedItem().toString();

    }

    public String getSearchString() {
        return searchString;
    }


    public interface InvoiceViewFragmentListener {

        void onInvoiceFragmentViewReady();

        void onRemoveSalesProduct(SalesProduct salesProduct);

        void onDeductSalesProduct(SalesProduct salesProduct);

        void onSearchProduct(String searchString, String searchCategory);

        void onCheckOut();

        void onClearProductList();

        void onRefreshProductMenuList();

        void onSeniorChecked(boolean isChecked);

        void onLoadMoreProducts();

        //void onReadQRCode();

    }

}
