package ph.com.gs3.loyaltystore.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.DynamicMenuButtonListViewAdapterOld;
import ph.com.gs3.loyaltystore.adapters.DynamicMenuButtonListViewAdapter;
import ph.com.gs3.loyaltystore.adapters.objects.MenuRowItem;
import ph.com.gs3.loyaltystore.models.sqlite.dao.DaoSession;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;

/**
 * Created by Bryan-PC on 22/04/2016.
 */
public class DynamicMenuAsButtonsFragment extends Fragment {

    public static final String TAG = DynamicMenuAsButtonsFragment.class.getSimpleName();

    private DynamicMenuButtonsViewFragmentListener listener;

    private DynamicMenuButtonListViewAdapterOld dynamicMenuButtonListViewAdapterOld;

    private DynamicMenuButtonListViewAdapter adapter;

    private View v;

    private Context context;

    private List<Product> productListToBeDisplayed;

    private ListView lvMenu;

    private LinearLayout llMenuButtonsLayout;

    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentTotalItemCount;
    private int currentScrollState;
    private int totalItemCount;

    public DynamicMenuAsButtonsFragment createInstance() {
        DynamicMenuAsButtonsFragment dynamicMenuAsButtonsFragment = new DynamicMenuAsButtonsFragment();
        return dynamicMenuAsButtonsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, " ON CREATE ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " ON RESUME ");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {

        Log.d(TAG, " ON ATTACH ");

        super.onAttach(context);
        this.context = context;
        if (context instanceof DynamicMenuButtonsViewFragmentListener) {
            listener = (DynamicMenuButtonsViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must implement DynamicMenuButtonsViewFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu_buttons, container, false);
        v = rootView;

        Log.d(TAG, "DynamicMenuButtonsViewFragment created");

        //dynamicMenuButtonListViewAdapterOld = new DynamicMenuButtonListViewAdapterOld(context);

        adapter = new DynamicMenuButtonListViewAdapter(context);

        //productListToBeDisplayed = new ArrayList<>();

        llMenuButtonsLayout = (LinearLayout) rootView.findViewById(R.id.DynamicMenuButtons_llMenu);

        if (productListToBeDisplayed != null) {
            //setMenuButtons();
        }

        lvMenu = (ListView) rootView.findViewById(R.id.DynamicMenuButtons_lvMenu);
        lvMenu.setAdapter(adapter);

        /*
        Button bLoadMore = new Button(context);
        bLoadMore.setText("Load More");
        bLoadMore.setBackgroundColor(Color.TRANSPARENT);
        bLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLoadMoreProducts();
            }
        });
        lvMenu.addFooterView(bLoadMore);
        */

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

        listener.onDynamicMenuButtonsViewReady();

        return rootView;
    }

    private void isScrollCompleted() {

        if (this.currentVisibleItemCount > 0 && this.currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

            listener.onLoadMoreProducts();

        }
    }

    public void setMenuButtons() {

        int buttonPerLinearLayoutCount = 3;

        llMenuButtonsLayout.removeAllViews();

        LinearLayout menuRow = createNewMenuRow();

        for (int i = 0; i < productListToBeDisplayed.size(); i++) {

            Product product = productListToBeDisplayed.get(i);

            if (buttonPerLinearLayoutCount != 0) {

                menuRow.addView(createButtonMenu(product));

                buttonPerLinearLayoutCount -= 1;
            } else {

                llMenuButtonsLayout.addView(menuRow);
                buttonPerLinearLayoutCount = 2;
                menuRow = createNewMenuRow();

                menuRow.addView(createButtonMenu(product));

            }

        }

        if (buttonPerLinearLayoutCount != 0) {

            while (buttonPerLinearLayoutCount != 0) {

                menuRow.addView(createButtonMenu(null));

                buttonPerLinearLayoutCount -= 1;
            }

        }

        //Add last menu row created

        if (menuRow != null) {
            llMenuButtonsLayout.addView(menuRow);
        }
    }

    private LinearLayout createNewMenuRow() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        return linearLayout;

    }

    private Button createButtonMenu(final Product product) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        Button buttonMenu = new Button(context);
        buttonMenu.setLayoutParams(params);
        buttonMenu.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);

        if (product == null) {
            buttonMenu.setVisibility(View.INVISIBLE);
        } else {

            final String name = product.getName();

            buttonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onDynamicMenuButtonsProductClicked(product);

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
                    if (cursor.getString(0) != null)
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

            if (product.getCategory().trim().equals(category.trim())) {
                productsByCategory.add(product);
            }
        }

        return productsByCategory;

    }

    public void setProducts(List<Product> products) {

        ArrayList<Object> objectArrayList = new ArrayList<>();

        List<String> categories = getProductCategories();

        totalItemCount = 0;

        for (String category : categories) {

            List<Product> productsByCategory = getProductInListByCategory(products, category);

            if(productsByCategory.size() > 0){
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


        if (adapter != null) {
            adapter.setMenuRow(objectArrayList);
        }

        productListToBeDisplayed = products;

        /*if (v != null)
            setMenuButtons();*/

    }

    public interface DynamicMenuButtonsViewFragmentListener {

        void onDynamicMenuButtonsViewReady();

        void onDynamicMenuButtonsProductClicked(Product product);

        void onLoadMoreProducts();

    }

}
