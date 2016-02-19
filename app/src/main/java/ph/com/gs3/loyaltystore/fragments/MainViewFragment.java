package ph.com.gs3.loyaltystore.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ph.com.gs3.loyaltystore.LoyaltyStoreApplication;
import ph.com.gs3.loyaltystore.R;
import ph.com.gs3.loyaltystore.adapters.SalesProductListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Product;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ProductDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;

/**
 * Created by Michael Reyes on 8/17/2015.
 */
public class MainViewFragment extends Fragment {

    public static final String TAG = MainViewFragment.class.getSimpleName();

    private MainViewFragmentEventListener mainViewFragmentEventListener;

    private ListView lvSalesProducts;

    private Button bCheckout;
    private Button bClear;
    private Button bSynchronize;
    private Button bSettings;
    private Button bMaintenance;
    private Button bRefresh;

    private TextView tvTotal;
    private Activity activity;

    private List<Product> products;
    private ProductDao productDao;

    private SalesProductListAdapter salesProductListAdapter;

    private View rootView;

    public static MainViewFragment createInstance(SalesProductListAdapter salesProductListAdapter) {
        MainViewFragment mainViewFragment = new MainViewFragment();
        mainViewFragment.salesProductListAdapter = salesProductListAdapter;
        return mainViewFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainViewFragmentEventListener = (MainViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must implement MainViewFragmentEventListener");
        }

        this.activity = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        this.rootView = rootView;

        productDao = LoyaltyStoreApplication.getInstance().getSession().getProductDao();

        tvTotal = (TextView) rootView.findViewById(R.id.Main_tvTotal);

        lvSalesProducts = (ListView) rootView.findViewById(R.id.Main_lvSalesProductList);
        lvSalesProducts.setAdapter(salesProductListAdapter);
        lvSalesProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mainViewFragmentEventListener
                                        .onRemoveTransaction(
                                                (SalesProduct) salesProductListAdapter.getItem(position));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to delete this transaction?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        bCheckout = (Button) rootView.findViewById(R.id.Main_bCheckout);
        bCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onCheckOut();
            }
        });

        bClear = (Button) rootView.findViewById(R.id.Main_bClear);
        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onClearTransaction();
            }
        });

        bSynchronize = (Button) rootView.findViewById(R.id.Main_bSync);
        bSynchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onSynchronizeClicked();
            }
        });

        bSettings = (Button) rootView.findViewById(R.id.Main_bSettings);
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onSettingsClicked();
            }
        });

        bMaintenance = (Button) rootView.findViewById(R.id.Main_bMaintenace);
        bMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onMaintenanceClicked();
            }
        });

        bRefresh = (Button) rootView.findViewById(R.id.Main_bRefresh);
        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuButtons();
            }
        });

        mainViewFragmentEventListener.onViewReady();

        return rootView;
    }

    public void setMenuButtons() {

        int buttonPerLinearLayoutCount = 3;
        LinearLayout rootLinearLayout = (LinearLayout) rootView.findViewById(R.id.Main_llMenu);

        rootLinearLayout.removeAllViews();

        getProducts();

        LinearLayout menuRow = createNewMenuRow();

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);

            if (buttonPerLinearLayoutCount != 0) {

                menuRow.addView(createButtonMenu(product.getName()));

                buttonPerLinearLayoutCount -= 1;
            } else {

                rootLinearLayout.addView(menuRow);
                buttonPerLinearLayoutCount = 2;
                menuRow = createNewMenuRow();

                menuRow.addView(createButtonMenu(product.getName()));

            }

        }

        if (buttonPerLinearLayoutCount != 0) {

            while (buttonPerLinearLayoutCount != 0) {

                menuRow.addView(createButtonMenu(""));

                buttonPerLinearLayoutCount -= 1;
            }

        }

        //Add last menu row created

        if (menuRow != null) {
            rootLinearLayout.addView(menuRow);
        }
    }

    private void getProducts() {

        products = productDao.loadAll();

    }

    private LinearLayout createNewMenuRow() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        return linearLayout;

    }

    private Button createButtonMenu(final String name) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        Button buttonMenu = new Button(activity);
        buttonMenu.setLayoutParams(params);
        buttonMenu.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
        buttonMenu.setText(name);

        if (name.equals("")) {
            buttonMenu.setVisibility(View.INVISIBLE);
        } else {
            buttonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mainViewFragmentEventListener.onProductClicked(name);

                }
            });
        }

        return buttonMenu;
    }

    public void setTotalAmount(float total) {
        tvTotal.setText(Float.toString(total));
    }

    public float getTotalAmount() {

        String totalAmountString = tvTotal.getText().toString();

        return Float.parseFloat(totalAmountString);

    }

    public interface MainViewFragmentEventListener {

        void onViewReady();

        void onProductClicked(String productName);

        void onCheckOut();

        void onClearTransaction();

        void onRemoveTransaction(SalesProduct salesProduct);

        void onSynchronizeClicked();

        void onSettingsClicked();

        void onMaintenanceClicked();
    }

}
