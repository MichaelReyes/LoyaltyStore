package ph.com.gs3.loyaltystore.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.DynamicMenuListViewAdapterOld;
import ph.com.gs3.loyaltystore.adapters.DynamicMenuListViewAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.DaoSession;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;

/**
 * Created by Bryan-PC on 22/04/2016.
 */
public class DynamicMenuAsListFragment extends Fragment {

    public static final String TAG = DynamicMenuAsListFragment.class.getSimpleName();

    private DynamicMenuListViewFragmentListener listener;

    private View v;

    private Context context;

    private DynamicMenuListViewAdapterOld dynamicMenuListViewAdapterOld;

    private DynamicMenuListViewAdapter adapter;

    private List<Product> productListToBeDisplayed;

    private ListView lvMenu;

    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentTotalItemCount;
    private int currentScrollState;
    private int totalItemCount;

    public DynamicMenuAsListFragment createInstance() {
        DynamicMenuAsListFragment dynamicMenuAsListFragment = new DynamicMenuAsListFragment();
        return dynamicMenuAsListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof DynamicMenuListViewFragmentListener) {
            listener = (DynamicMenuListViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DynamicMenuListViewFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu_list, container, false);
        v = rootView;

        Log.d(TAG, "DynamicMenuListViewFragment created");

        listener.onDynamicMenuListViewReady();

        dynamicMenuListViewAdapterOld = new DynamicMenuListViewAdapterOld(context);
        adapter = new DynamicMenuListViewAdapter(context);

        lvMenu = (ListView) rootView.findViewById(R.id.DynamicMenuList_lvMenu);
        lvMenu.setAdapter(adapter);

        /*Button bLoadMore = new Button(context);
        bLoadMore.setText("Load More");
        bLoadMore.setBackgroundColor(Color.TRANSPARENT);
        bLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLoadMoreProducts();
            }
        });
        lvMenu.addFooterView(bLoadMore);*/

        lvMenu.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;

                Log.d(TAG, "currentTotalItemCount : " + currentTotalItemCount);
                Log.d(TAG, "currentVisibleItemCount : " + currentVisibleItemCount);
                Log.d(TAG, "currentFirstVisibleItem : " + currentFirstVisibleItem);
                Log.d(TAG, "currentVisibleItemCount + currentFirstVisibleItem : " + (currentVisibleItemCount + currentFirstVisibleItem));

                if (currentTotalItemCount == (currentVisibleItemCount + currentFirstVisibleItem))
                    isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }
        });

        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onDynamicMenuListProductClicked((Product) adapter.getItem(position));
            }
        });


        return rootView;
    }

    private void isScrollCompleted() {

        //Log.d(TAG, "Scroll Completed");

        if (this.currentVisibleItemCount > 0 && this.currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

            //Log.d(TAG, " I N ");

            listener.onLoadMoreProducts();

        }
    }

    public void setMenuList() {

        LinearLayout rootLinearLayout = (LinearLayout) v.findViewById(R.id.DynamicMenuList_llMenu);

        rootLinearLayout.removeAllViews();

        for (int i = 0; i < productListToBeDisplayed.size(); i++) {

            LinearLayout menuRow = createNewMenuRow();

            Product product = productListToBeDisplayed.get(i);

            menuRow.addView(createButtonMenu(product));

            rootLinearLayout.addView(menuRow);
        }


    }

    private LinearLayout createNewMenuRow() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.weight = 1;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        return linearLayout;

    }

    private Button createButtonMenu(final Product product) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.weight = 1;
        Button buttonMenu = new Button(context);
        buttonMenu.setLayoutParams(params);
        buttonMenu.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
        buttonMenu.setBackgroundColor(Color.TRANSPARENT);
        buttonMenu.setTextColor(Color.BLACK);

        if (product == null) {
            buttonMenu.setVisibility(View.INVISIBLE);
        } else {

            final String name = product.getName();

            buttonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onDynamicMenuListProductClicked(product);

                }
            });

            buttonMenu.setText(name);
        }

        return buttonMenu;
    }

    private List<String> getProductCategories() {

        DaoSession session = LoyaltyStoreApplication.getSession();

        String sql = "SELECT DISTINCT " +
                ProductDao.Properties.Category.columnName +
                " FROM " + ProductDao.TABLENAME;

        List<String> result = new ArrayList<>();

        Cursor cursor = session.getDatabase().rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(0) != null) {
                        result.add(cursor.getString(0));
                        Log.d(TAG, "FETCHED CATEGORY : " + cursor.getString(0));
                    }
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

            if (product.getCategory().trim().equals(category.trim())) {
                productsByCategory.add(product);
            }
        }

        return productsByCategory;

    }

    public void setProducts(List<Product> products) {

        currentTotalItemCount = products.size();

        List<String> categories = getProductCategories();

        List<Object> productArrayObject = new ArrayList<>();

        for (String category : categories) {

            Log.d(TAG, "CATEGORY : " + category);

            List<Product> productList = getProductInListByCategory(products, category);

            if (productList.size() > 0) {

                Log.d(TAG, "Product list size : " + productList.size());

                productArrayObject.add(category);
                currentTotalItemCount++;

                for (Product product : productList) {
                    productArrayObject.add(product);
                }
            }

        }

        if (adapter != null) {
            adapter.setProductList(productArrayObject);
        }

        /*
        if(dynamicMenuListViewAdapterOld != null){
            dynamicMenuListViewAdapterOld.setProductList(products);
        }
        */

    }

    public interface DynamicMenuListViewFragmentListener {

        void onDynamicMenuListViewReady();

        void onDynamicMenuListProductClicked(Product product);

        void onLoadMoreProducts();


    }

}
