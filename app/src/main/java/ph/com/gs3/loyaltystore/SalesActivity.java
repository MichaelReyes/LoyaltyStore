package ph.com.gs3.loyaltystore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;
import ph.com.gs3.loyaltystore.adapters.SalesListAdapter;
import ph.com.gs3.loyaltystore.adapters.SalesProductListAdapter;
import ph.com.gs3.loyaltystore.fragments.SalesViewFragment;
import ph.com.gs3.loyaltystore.models.sqlite.dao.Sales;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProduct;
import ph.com.gs3.loyaltystore.models.sqlite.dao.SalesProductDao;

/**
 * Created by Bryan-PC on 18/02/2016.
 */
public class SalesActivity extends AppCompatActivity implements SalesViewFragment.SalesViewFragmentListener {

    public static final String TAG = SalesActivity.class.getSimpleName();

    private SalesViewFragment salesViewFragment;

    private List<Sales> salesList;
    private List<SalesProduct> salesProductList;

    private SalesListAdapter salesListAdapter;
    private SalesProductListAdapter salesProductListAdapter;

    private SalesDao salesDao;
    private SalesProductDao salesProductDao;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        salesViewFragment = (SalesViewFragment)
                getFragmentManager().findFragmentByTag(SalesViewFragment.TAG);

        salesList = new ArrayList<>();
        salesListAdapter = new SalesListAdapter(this, salesList);

        salesProductList = new ArrayList<>();
        salesProductListAdapter = new SalesProductListAdapter(this, salesProductList);

        if (salesViewFragment == null) {
            salesViewFragment = new SalesViewFragment();
            salesViewFragment = SalesViewFragment.createInstance(salesListAdapter,
                    salesProductListAdapter);
            getFragmentManager().beginTransaction().add(
                    R.id.container_sales,
                    salesViewFragment, SalesViewFragment.TAG).commit();
        }

        salesDao = LoyaltyStoreApplication.getInstance().getSession().getSalesDao();
        salesProductDao = LoyaltyStoreApplication.getInstance().getSession().getSalesProductDao();

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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onViewReady() {

        salesList.clear();

        List<Sales> sList = new ArrayList<>();

        if (salesViewFragment.getFilterDate().equals("[No Date Selected]")) {
            sList = salesDao.queryBuilder().orderDesc(
                    SalesDao.Properties.Transaction_date
            ).list();
        } else {
            /*String sql =
                    "WHERE DATE("
                            + SalesDao.Properties.Transaction_date.columnName +
                            ") = '" +  java.sql.Date.valueOf(salesViewFragment.getFilterDate()) + "'" +
                            " ORDER BY " + SalesDao.Properties.Transaction_date.columnName + " DESC";

            sList = salesDao.queryRaw(sql,null);*/

            /*sList = salesDao.queryBuilder()
                    .where(SalesDao.Properties.Transaction_date.eq(java.sql.Date.valueOf(salesViewFragment.getFilterDate())))
                    .orderDesc(
                            SalesDao.Properties.Transaction_date
                    ).list();*/

            List<Sales> salesRecords = salesDao.loadAll();

            for(Sales sales : salesRecords){

                Date dtSales =  java.sql.Date.valueOf(
                        formatter.format(sales.getTransaction_date())
                );

                Date dtFilter =  java.sql.Date.valueOf(salesViewFragment.getFilterDate());

                if(dtSales.compareTo(dtFilter) == 0){
                    sList.add(sales);
                }
            }

        }

        float totalSalesAmount = 0;

        for (Sales sales : sList) {

            totalSalesAmount += sales.getAmount();
            salesList.add(sales);

        }

        salesViewFragment.setTotalSalesAmount(totalSalesAmount);

        salesListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onViewOrder(Sales sales) {

        salesProductList.clear();

        QueryBuilder qBuilder = salesProductDao.queryBuilder();
        qBuilder.where(SalesProductDao.Properties.Sales_transaction_number.eq(sales.getTransaction_number()));
        List<SalesProduct> salesProducts = qBuilder.list();

        for (SalesProduct salesProduct : salesProducts) {

            salesProductList.add(salesProduct);

        }

        salesProductListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDateSelected() {
        onViewReady();
    }
}
