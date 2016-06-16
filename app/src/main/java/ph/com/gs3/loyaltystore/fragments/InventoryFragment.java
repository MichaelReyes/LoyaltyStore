package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.ViewPagerAdapter;
import ph.com.gs3.loyaltystore.globals.Constants;
import ph.com.gs3.loyaltystore.models.TabMaintenance;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Expenses;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ExpensesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCount;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemStockCountDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;
import ph.com.gs3.loyaltystore.models.values.Retailer;

/**
 * Created by Bryan-PC on 25/04/2016.
 */
public class InventoryFragment extends Fragment implements ViewPager.OnPageChangeListener {

    public static final String TAG = InventoryFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Context context;
    private FragmentActivity activity;

    private View v;

    private Fragment currentFragment;

    public InventoryFragment createInstance() {
        InventoryFragment inventoryFragment = new InventoryFragment();
        return inventoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (FragmentActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
        this.context = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);
        v = rootView;

        Log.d(TAG, "InventoryFragment created");

        initializeViews();

        return rootView;
    }

    public void initializeViews() {
        viewPager = (ViewPager) v.findViewById(R.id.Inventory_fragment_viewpager);
        viewPager.addOnPageChangeListener(this);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) v.findViewById(R.id.Inventory_fragment_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle extras = activity.getIntent().getExtras();

        TabMaintenance tabMaintenance = TabMaintenance.getTabMaintenanceFromSharedPreferences(context);

        viewPagerAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager(),
                extras);

        if (tabMaintenance.isInventoryOfSalesTabActive())
            viewPagerAdapter.addFragment(new StoreSalesInventoryDetailsFragment(), "Inventory Of Sales");
        if (tabMaintenance.isInventoryOfStocksTabActive())
            viewPagerAdapter.addFragment(new ItemInventoryDetailsFragment(), "Inventory Of Stocks");
        if (tabMaintenance.isInventoryItemStockCountTabActive())
            viewPagerAdapter.addFragment(new ItemStockCountDetailsFragment(), "Item Stock Count");
        if (tabMaintenance.isInventoryReturnsToCommissaryTabActive())
            viewPagerAdapter.addFragment(new ReturnsToCommissaryFragment(), "Returns to Commissary");
        if (tabMaintenance.isInventoryExpensesTabActive())
            viewPagerAdapter.addFragment(new ExpensesFragment(), "Expenses");

        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentFragment = (Fragment) viewPagerAdapter.instantiateItem(viewPager, position);

        if (currentFragment instanceof StoreSalesInventoryDetailsFragment) {
            loadStoreSalesInventory();
        } else if (currentFragment instanceof ItemInventoryDetailsFragment) {
            loadItemInventoryStockCount();
        } else if (currentFragment instanceof ItemStockCountDetailsFragment) {
            ((ItemStockCountDetailsFragment) currentFragment).setInventory();
        } else if (currentFragment instanceof ReturnsToCommissaryFragment) {
            loadReturnsToCommissary();
        } else if (currentFragment instanceof ExpensesFragment) {
            loadExpenses();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void loadStoreSalesInventory() {

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment instanceof StoreSalesInventoryDetailsFragment) {

            Date dateFilter = ((StoreSalesInventoryDetailsFragment) currentFragment).getDateFilter();

            SimpleDateFormat formatter = Constants.SIMPLE_DATE_FORMAT;

            List<SalesProduct> salesProductList = new ArrayList<>();

            float totalSalesAmount = 0;

            String query =
                    "SELECT " + SalesProductDao.Properties.Id.columnName +
                            " ," + SalesProductDao.Properties.Product_id.columnName +
                            ", SUM(" + SalesProductDao.Properties.Quantity.columnName +
                            "), SUM(" + SalesProductDao.Properties.Sub_total.columnName +
                            ") FROM " + SalesProductDao.TABLENAME;


            Log.d(TAG, "QUERY : " + query);

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

                Log.d(TAG, " DATE : " + dtSales + " ~ " + dateFilter);

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

            Log.d(TAG, " QUERY : " + query);

            Cursor c = LoyaltyStoreApplication.getSession().getDatabase().rawQuery(query, null);
            try {
                if (c.moveToFirst()) {
                    do {

                        SalesProduct salesProduct = new SalesProduct();
                        salesProduct.setId(c.getLong(0));
                        salesProduct.setProduct_id(c.getLong(1));
                        salesProduct.setQuantity(c.getInt(2));
                        salesProduct.setSub_total(c.getFloat(3));

                        totalSalesAmount += salesProduct.getSub_total();

                        salesProductList.add(salesProduct);

                    } while (c.moveToNext());
                }
            } finally {
                c.close();
            }

            ((StoreSalesInventoryDetailsFragment) currentFragment).setTotalSales(totalSalesAmount);
            ((StoreSalesInventoryDetailsFragment) currentFragment).setSales(salesProductList);
        }
    }

    public void addItemStockCount(ItemStockCount itemStockCount) {
        if (currentFragment instanceof ItemStockCountDetailsFragment) {
            ((ItemStockCountDetailsFragment) currentFragment).addStockCount(itemStockCount);
        }
    }

    public void loadMoreItemInventory(){
        if (currentFragment instanceof ItemStockCountDetailsFragment) {
            ((ItemStockCountDetailsFragment) currentFragment).setInventory();
        }
    }

    public void loadItemInventoryStockCount() {
        if (currentFragment instanceof ItemInventoryDetailsFragment) {
            Log.d(TAG, " onLoadItemInventoryStockCount ");

            SimpleDateFormat formatter = Constants.SIMPLE_DATE_FORMAT;

            Date dateFilter = ((ItemInventoryDetailsFragment) currentFragment).getDateFilter();

            ItemStockCountDao itemStockCountDao
                    = LoyaltyStoreApplication.getSession().getItemStockCountDao();


            List<ItemStockCount> itemStockCountList = new ArrayList<>();

            for (ItemStockCount itemStockCount : itemStockCountDao.loadAll()) {

                if (java.sql.Date.valueOf(formatter.format(itemStockCount.getDate_counted())).compareTo(dateFilter) == 0) {
                    itemStockCountList.add(itemStockCount);
                }

            }

            ((ItemInventoryDetailsFragment) currentFragment).setItems(itemStockCountList);

        }
    }

    public void loadReturnsToCommissary() {

        ItemReturnDao itemReturnDao
                = LoyaltyStoreApplication.getSession().getItemReturnDao();

        CashReturnDao cashReturnDao
                = LoyaltyStoreApplication.getSession().getCashReturnDao();

        SimpleDateFormat formatter = Constants.SIMPLE_DATE_FORMAT;

        if (currentFragment instanceof ReturnsToCommissaryFragment) {

            Log.d(TAG, " ReturnsToCommissary itemReturn : " + itemReturnDao.loadAll().size());
            Log.d(TAG, " ReturnsToCommissary cashReturn : " + cashReturnDao.loadAll());

            Date dateFilter = ((ReturnsToCommissaryFragment) currentFragment).getDateFilter();

            List<ItemReturn> allItemReturn = itemReturnDao.loadAll();
            List<ItemReturn> itemReturnListFilteredByDate = new ArrayList<>();

            for (ItemReturn itemReturn : allItemReturn) {

                if (java.sql.Date.valueOf(formatter.format(itemReturn.getDate_created())).compareTo(dateFilter) == 0) {
                    itemReturnListFilteredByDate.add(itemReturn);
                }

            }

            List<CashReturn> allCashReturn = cashReturnDao.loadAll();
            List<CashReturn> cashReturnListFilteredByDate = new ArrayList<>();

            for (CashReturn cashReturn : allCashReturn) {

                if (java.sql.Date.valueOf(formatter.format(cashReturn.getDate_created())).compareTo(dateFilter) == 0) {
                    cashReturnListFilteredByDate.add(cashReturn);
                }

            }

            ((ReturnsToCommissaryFragment) currentFragment).setItemReturnList(itemReturnListFilteredByDate);

            ((ReturnsToCommissaryFragment) currentFragment).setCashReturnList(cashReturnListFilteredByDate);

        }

    }

    public void loadExpenses() {
        if (currentFragment instanceof ExpensesFragment) {
            {
                ExpensesDao expensesDao =
                        LoyaltyStoreApplication.getSession().getExpensesDao();

                ((ExpensesFragment) currentFragment).setExpensesList(
                        expensesDao.queryBuilder().orderDesc(ExpensesDao.Properties.Date).list());
            }
        }
    }

    public void addExpense(long id) {

        if (currentFragment instanceof ExpensesFragment) {

            boolean valid = true;

            String description = ((ExpensesFragment) currentFragment).getDescription();
            String amountString = ((ExpensesFragment) currentFragment).getAmountString();

            ExpensesDao expensesDao =
                    LoyaltyStoreApplication.getSession().getExpensesDao();

            if ("".equals(description)) {
                valid = false;
                ((ExpensesFragment) currentFragment).setDescriptionError("This field is required.");
            }

            if ("".equals(amountString)) {
                valid = false;
                ((ExpensesFragment) currentFragment).setAmountError("This field is required.");
            }

            if (valid) {
                Expenses expenses = new Expenses();
                expenses.setDescription(description);
                expenses.setAmount(Float.valueOf(amountString));
                expenses.setIs_synced(false);

                Retailer retailer = Retailer.getDeviceRetailerFromSharedPreferences(context);

                expenses.setStore_id(retailer.getStoreId());

                if (id != -1) {

                    expenses.setId(id);

                    List<Expenses> eList = expensesDao.queryBuilder()
                            .where(ExpensesDao.Properties.Id.eq(id)).list();

                    for (Expenses e : eList) {

                        expenses.setDate(e.getDate());

                    }

                } else {
                    expenses.setDate(new Date());
                }

                expensesDao.insertOrReplaceInTx(expenses);

                ((ExpensesFragment) currentFragment).clearInputFields();

                loadExpenses();
            }

            Gson gson = new Gson();
            Log.d(TAG, gson.toJson(expensesDao.loadAll()));
        }

    }

    public void clearItemStockCountList(){
        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if(currentFragment instanceof ItemStockCountDetailsFragment){
            ((ItemStockCountDetailsFragment) currentFragment).clearList();
        }
    }


}
