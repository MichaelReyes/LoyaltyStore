package ph.com.gs3.loyaltystore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.ViewPagerAdapter;
import ph.com.gs3.loyaltystore.fragments.ItemInventoryDetailsFragment;
import ph.com.gs3.loyaltystore.fragments.ItemStockCountDetailsFragment;
import ph.com.gs3.loyaltystore.fragments.StoreSalesInventoryDetailsFragment;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCountDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;

/**
 * Created by Bryan-PC on 07/04/2016.
 */
public class InventoryActivity extends AppCompatActivity implements
        StoreSalesInventoryDetailsFragment.StoreSalesInventoryDetailsFragmentListener,
        ItemStockCountDetailsFragment.ItemStockCountDetailsFragmentListener,
        ItemInventoryDetailsFragment.ItemInventoryDetailsFragmentListener,
        ViewPager.OnPageChangeListener {

    public static final String TAG = InventoryActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_inventory);

        formatter = Constants.SIMPLE_DATE_FORMAT;

        initializeViews();


    }

    private void initializeViews() {

        viewPager = (ViewPager) findViewById(R.id.Inventory_viewpager);
        viewPager.addOnPageChangeListener(this);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.Inventory_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle extras = getIntent().getExtras();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                extras);

        viewPagerAdapter.addFragment(new StoreSalesInventoryDetailsFragment(), "Inventory Of Sales");
        viewPagerAdapter.addFragment(new ItemInventoryDetailsFragment(), "Inventory Of Stocks");
        viewPagerAdapter.addFragment(new ItemStockCountDetailsFragment(), "Item Stock Count");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Fragment fragment = (Fragment) viewPagerAdapter.instantiateItem(viewPager, position);

        if(fragment instanceof StoreSalesInventoryDetailsFragment){

            //Log.d(TAG, "StoreSalesInventoryDetailsFragment Triggered");

            onLoadStoreSalesInventory(((StoreSalesInventoryDetailsFragment) fragment).getDateFilter());

        }else if(fragment instanceof  ItemInventoryDetailsFragment){

            //Log.d(TAG, "ItemInventoryDetailsFragment Triggered");
            onLoadItemInventoryStockCount(((ItemInventoryDetailsFragment) fragment).getDateFilter());


        }else if(fragment instanceof  ItemStockCountDetailsFragment){
            //Log.d(TAG, "ItemStockCountDetailsFragment Triggered");

            ((ItemStockCountDetailsFragment) fragment).setProductButtons();

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoadStoreSalesInventory(Date dateFilter) {
        Fragment storeSalesInventoryFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        List<SalesProduct> salesProductList = new ArrayList<>();

        String query =
                "SELECT " + SalesProductDao.Properties.Id.columnName +
                        " ," + SalesProductDao.Properties.Product_id.columnName +
                        ", SUM(" + SalesProductDao.Properties.Quantity.columnName +
                        "), SUM(" + SalesProductDao.Properties.Sub_total.columnName +
                        ") FROM " + SalesProductDao.TABLENAME;

        /*if (dateFilter == null) {
            //dateFilter = new Date();
            dateFilter = java.sql.Date.valueOf(tvDateFilter.getText().toString());

        }*/

        /*query += " WHERE " + SalesProductDao.Properties.Sales_transaction_number.columnName +
                "= (SELECT " + SalesDao.Properties.Transaction_number.columnName +
                " FROM " + SalesDao.TABLENAME + " WHERE strftime('%Y-%m-%d'," +
                SalesDao.Properties.Transaction_date.columnName + ")=date('" + formatter.format(dateFilter) + "'))";*/

        SalesDao salesDao =
                LoyaltyStoreApplication.getSession().getSalesDao();

        List<Sales> salesList = new ArrayList<>();

        for (Sales sales : salesDao.loadAll()) {

            Date dtSales = java.sql.Date.valueOf(
                    formatter.format(sales.getTransaction_date())
            );

            if (dtSales.compareTo(dateFilter) == 0) {
                salesList.add(sales);
            }
        }

        if (salesList.size() > 0) {
            query += " WHERE ";

            for (int i = 0; i < salesList.size(); i++) {

                query += SalesProductDao.Properties.Sales_transaction_number.columnName
                        + " = '" + salesList.get(i).getTransaction_number() + "'";

                if (i != salesList.size() - 1) {
                    query += " OR ";
                }

            }

        } else {
            query += " WHERE " + SalesProductDao.Properties.Sales_transaction_number.columnName + " IS NULL";
        }

        query += " GROUP BY " + SalesProductDao.Properties.Product_id.columnName;

        Cursor c = LoyaltyStoreApplication.getSession().getDatabase().rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {

                    SalesProduct salesProduct = new SalesProduct();
                    salesProduct.setId(c.getLong(0));
                    salesProduct.setProduct_id(c.getLong(1));
                    salesProduct.setQuantity(c.getInt(2));
                    salesProduct.setSub_total(c.getFloat(3));

                    salesProductList.add(salesProduct);

                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        if (storeSalesInventoryFragment instanceof StoreSalesInventoryDetailsFragment)
            ((StoreSalesInventoryDetailsFragment) storeSalesInventoryFragment).setSales(salesProductList);
    }

    @Override
    public List<ItemInventory> getItemInventory() {

        List<ItemInventory> inventories = new ArrayList<>();

        ProductDao productDao
                = LoyaltyStoreApplication.getSession().getProductDao();

        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();

        List<Product> products =
                productDao
                        .queryBuilder()
                        .where(
                                ProductDao.Properties.Type.eq("Product for Delivery")
                        ).list();

        for (Product product : products) {

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                    .queryBuilder()
                    .where(
                            ItemInventoryDao.Properties.Product_id.eq(
                                    product.getId()
                            )
                    ).list();

            for (ItemInventory itemInventory : itemInventoryList) {
                inventories.add(itemInventory);
            }

        }

        return inventories;
    }

    @Override
    public void onItemClick(final ItemInventory item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getName());

        StringBuilder sb = new StringBuilder();
        sb.append("Expected Output : " + item.getQuantity());
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        //sb.append("Physical count : ");

        builder.setMessage(sb.toString());

        final EditText inputPhysicalCount = new EditText(this);
        inputPhysicalCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputPhysicalCount.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputPhysicalCount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView tvPhysicalCount = new TextView(this);
        tvPhysicalCount.setText("Physical Count : ");

        final EditText inputRemarks = new EditText(this);
        inputRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputRemarks.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TextView tvRemarks = new TextView(this);
        tvRemarks.setText("Remarks : ");

        //builder.setView(inputPhysicalCount);

        LinearLayout layout = new LinearLayout(InventoryActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(tvPhysicalCount);
        layout.addView(inputPhysicalCount);
        layout.addView(tvRemarks);
        layout.addView(inputRemarks);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(inputPhysicalCount.getText().toString().trim())) {

                    inputPhysicalCount.setError("This field is required!");
                    return;

                }


                Log.d(TAG, " ------ > " + item.getQuantity() + " ~ " + Double.parseDouble(inputPhysicalCount.getText().toString()));
                Log.d(TAG, " ------ > " + (item.getQuantity() != Double.parseDouble(inputPhysicalCount.getText().toString())));
                if (item.getQuantity() != Double.parseDouble(inputPhysicalCount.getText().toString())
                        && "".equals(inputRemarks.getText().toString().trim())) {

                    inputRemarks.setError("This field is required if the value" +
                            " of Expected Output and Physical Count is not equal.");

                    return;
                }

                double quantity = Double.parseDouble(inputPhysicalCount.getText().toString());

                ItemStockCount itemStockCount = new ItemStockCount();
                itemStockCount.setProduct_id(item.getProduct_id());
                itemStockCount.setName(item.getName());
                itemStockCount.setExpectedQuantity(item.getQuantity());
                itemStockCount.setQuantity(quantity);
                itemStockCount.setRemarks(inputRemarks.getText().toString());

                Fragment itemInventoryDetailsFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

                ((ItemStockCountDetailsFragment) itemInventoryDetailsFragment).addStockCount(itemStockCount);


                dialog.dismiss();

            }
        });


    }


    @Override
    public void onItemStockCountClicked(ItemStockCount itemStockCount) {

        if (!"".equals(itemStockCount.getRemarks())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(itemStockCount.getName());

            StringBuilder sb = new StringBuilder();
            sb.append("Expected Output : " + itemStockCount.getExpectedQuantity());
            sb.append(System.getProperty("line.separator"));
            sb.append("Physical Count : " + itemStockCount.getQuantity());
            sb.append(System.getProperty("line.separator"));
            sb.append("Remarks : " + itemStockCount.getRemarks());

            builder.setMessage(sb.toString());

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }

    }

    @Override
    public void onLoadMoreInventory() {

    }

    @Override
    public void onSaveItemStockCount(List<ItemStockCount> stockCountList) {

        ItemStockCountDao itemStockCountDao
                = LoyaltyStoreApplication.getSession().getItemStockCountDao();
        ItemInventoryDao itemInventoryDao
                = LoyaltyStoreApplication.getSession().getItemInventoryDao();


        Date currDate = new Date();

        for (ItemStockCount itemStockCount : stockCountList) {

            long productId = itemStockCount.getProduct_id();

            itemStockCount.setDate_counted(currDate);
            itemStockCountDao.insertOrReplace(itemStockCount);

            List<ItemInventory> itemInventoryList
                    = itemInventoryDao
                        .queryBuilder()
                        .where(
                                ItemInventoryDao.Properties.Product_id.eq(productId)
                        ).limit(1).list();

            if(itemInventoryList.size() > 0){

                ItemInventory itemInventory = itemInventoryList.get(0);

                itemInventory.setQuantity(itemStockCount.getQuantity());
                itemInventoryDao.insertOrReplace(itemInventory);

            }

        }
    }

    @Override
    public void onLoadItemInventoryStockCount(Date dateFilter) {

        Log.d(TAG, " onLoadItemInventoryStockCount ");

        Fragment itemInventoryDetailsFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        ItemStockCountDao itemStockCountDao
                = LoyaltyStoreApplication.getSession().getItemStockCountDao();


        List<ItemStockCount> itemStockCountList = new ArrayList<>();

        for(ItemStockCount itemStockCount : itemStockCountDao.loadAll()){

            if(java.sql.Date.valueOf(formatter.format(itemStockCount.getDate_counted())).compareTo(dateFilter) == 0){
                itemStockCountList.add(itemStockCount);
            }

        }

        ((ItemInventoryDetailsFragment) itemInventoryDetailsFragment).setItems(itemStockCountList);

    }
}
