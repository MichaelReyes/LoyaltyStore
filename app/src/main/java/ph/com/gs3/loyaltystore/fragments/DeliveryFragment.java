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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.ViewPagerAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventory;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemInventoryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDeliveryDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDelivery;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductForDeliveryDao;

/**
 * Created by Bryan-PC on 29/04/2016.
 */
public class DeliveryFragment extends Fragment implements ViewPager.OnPageChangeListener {

    public static final String TAG = DeliveryFragment.class.getSimpleName();

    private Context context;
    private FragmentActivity activity;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private View v;

    private Fragment currentFragment;

    public DeliveryFragment createInstance() {

        DeliveryFragment deliveryFragment = new DeliveryFragment();
        return deliveryFragment;

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
        this.context = activity;
        this.activity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_delivery, container, false);
        v = rootView;

        Log.d(TAG, "DeliveryFragment created");

        initializeViews();

        return rootView;
    }

    public void initializeViews() {
        viewPager = (ViewPager) v.findViewById(R.id.DeliveryFragment_viewpager);
        viewPager.addOnPageChangeListener(this);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) v.findViewById(R.id.DeliveryFragment_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle extras = activity.getIntent().getExtras();

        viewPagerAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager(),
                extras);


        viewPagerAdapter.addFragment(new DeliveryHistoryFragment(), "Delivery History");
        viewPagerAdapter.addFragment(new DeliveriesForConfirmationFragment(), "Get and Confirm Deliveries");

        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentFragment = (Fragment) viewPagerAdapter.instantiateItem(viewPager, position);

        if(currentFragment instanceof DeliveryHistoryFragment){
            setDistinctDeliveryList();
        }else if(currentFragment instanceof DeliveriesForConfirmationFragment){

            Log.d(TAG, " instanceof DeliveriesForConfirmationFragment ");

            ProductDeliveryDao productDeliveryDao
                    = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

            List<ProductDelivery> productDeliveryList =
                    productDeliveryDao
                            .queryBuilder()
                            .whereOr(
                                    ProductDeliveryDao.Properties.Is_synced.eq(false),
                                    ProductDeliveryDao.Properties.Status.eq("For Store Confirmation")
                            ).list();

            Log.d(TAG, "productDeliveryList size : " + productDeliveryList.size());
            Log.d(TAG, "all productDeliveryList size : " + productDeliveryDao.loadAll().size());

            ((DeliveriesForConfirmationFragment) currentFragment).setProductDeliveryList(productDeliveryList);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setDistinctDeliveryList(){

        currentFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());

        if(currentFragment instanceof DeliveryHistoryFragment){

            List<ProductForDelivery> productForDeliveryHistoryList = new ArrayList<>();

            String query =
                    "SELECT DISTINCT "
                            + ProductForDeliveryDao.Properties.Date_received.columnName +
                            " FROM " + ProductForDeliveryDao.TABLENAME;

            Cursor c = LoyaltyStoreApplication.getSession().getDatabase().rawQuery(query, null);
            try {
                if (c.moveToFirst()) {
                    do {

                        ProductForDelivery productForDelivery = new ProductForDelivery();

                        Date date = new Date(c.getLong(0));
                        productForDelivery.setDate_received(date);

                        productForDeliveryHistoryList.add(productForDelivery);

                        Log.d(TAG, "**************************> START");

                        Log.d(TAG, "Date Delivered :" + productForDelivery.getDate_received());

                        Log.d(TAG, "**************************> END");

                    } while (c.moveToNext());
                }
            } finally {
                c.close();
            }

            ((DeliveryHistoryFragment) currentFragment).setDeliveryListByDateAndAgentName(productForDeliveryHistoryList);

        }

    }

    public void setDeliveryListByDateAndAgentName(ProductForDelivery productForDelivery) {

        if(currentFragment instanceof DeliveryHistoryFragment) {

            ProductForDeliveryDao productForDeliveryDao
                    = LoyaltyStoreApplication.getSession().getProductForDeliveryDao();

            List<ProductForDelivery> productForDeliveryList
                    = productForDeliveryDao
                    .queryBuilder()
                    .where(
                            ProductForDeliveryDao.Properties.Date_received.eq(
                                    productForDelivery.getDate_received()
                            )
                    ).list();

            Gson gson = new Gson();

            Log.d(TAG, gson.toJson(productForDeliveryList));

            ((DeliveryHistoryFragment) currentFragment).setProductDeliveryList(productForDeliveryList);

        }

    }

    public void confirmProductDeliveries() {

        if (currentFragment instanceof DeliveriesForConfirmationFragment) {

            ProductDao productDao
                    = LoyaltyStoreApplication.getSession().getProductDao();

            ProductDeliveryDao productDeliveryDao
                    = LoyaltyStoreApplication.getSession().getProductDeliveryDao();

            ItemInventoryDao itemInventoryDao
                    = LoyaltyStoreApplication.getSession().getItemInventoryDao();

            List<ProductDelivery> productDeliveryList =
                    ((DeliveriesForConfirmationFragment) currentFragment).getProductDeliveryList();

            for (ProductDelivery productDelivery : productDeliveryList) {

                productDeliveryDao.insertOrReplace(productDelivery);

                if ("ACCEPTED".equals(productDelivery.getStatus().trim().toUpperCase())
                        && "PRODUCT".equals(productDelivery.getDistribution_type().trim().toUpperCase())) {

                    List<ItemInventory> itemInventoryList
                            = itemInventoryDao
                            .queryBuilder()
                            .where(
                                    ItemInventoryDao.Properties.Product_id.eq(
                                            productDelivery.getProduct_id()
                                    )
                            ).limit(1).list();

                    if (itemInventoryList.size() > 0) {

                        ItemInventory itemInventory = itemInventoryList.get(0);

                        itemInventory.setQuantity(itemInventory.getQuantity() + productDelivery.getQuantity());
                        itemInventoryDao.insertOrReplace(itemInventory);

                    } else {
                        ItemInventory itemInventory = new ItemInventory();
                        itemInventory.setProduct_id(productDelivery.getProduct_id());
                        itemInventory.setName(productDelivery.getName());
                        itemInventory.setQuantity(productDelivery.getQuantity());

                        itemInventoryDao.insertOrReplace(itemInventory);
                    }

                }

            }

            Log.d(TAG, " ***************************************************");

            List<ItemInventory> itemInventoryList = itemInventoryDao.loadAll();

            for (ItemInventory itemInventory : itemInventoryList) {

                Log.d(TAG, "--> Id : " + itemInventory.getId());
                Log.d(TAG, "--> Name : " + itemInventory.getName());
                Log.d(TAG, "--> Product Id : " + itemInventory.getProduct_id());
                Log.d(TAG, "--> Quantity : " + itemInventory.getQuantity());

            }

            Log.d(TAG, " ***************************************************");

            Toast.makeText(context, "Products for delivery confirmed!", Toast.LENGTH_SHORT).show();

        }
    }

    public void setProductsForDelivery(List<ProductForDelivery> productsForDelivery){
        if(currentFragment instanceof DeliveriesForConfirmationFragment){

            ((DeliveriesForConfirmationFragment) currentFragment).setProductsForDelivery(productsForDelivery);

        }
    }

    public void addRecievedProductForDelivery(ProductForDelivery productForDelivery){
        if(currentFragment instanceof DeliveriesForConfirmationFragment){
            ((DeliveriesForConfirmationFragment) currentFragment).addReceivedProductForDelivery(productForDelivery);
        }
    }

    public void clearDeliveryList(){
        if(currentFragment instanceof DeliveriesForConfirmationFragment){
            ((DeliveriesForConfirmationFragment) currentFragment).clearDeliveryList();
        }
    }

}
