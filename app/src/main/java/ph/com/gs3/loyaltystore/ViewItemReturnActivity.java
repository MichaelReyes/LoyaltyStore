package ph.com.gs3.loyaltystore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.loyaltystore.adapters.CashReturnListAdapter;
import ph.com.gs3.loyaltystore.adapters.ItemReturnListAdapter;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.CashReturnDao;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturn;
import ph.com.gs3.loyaltystore.models.sqlite.dao.ItemReturnDao;

/**
 * Created by Bryan-PC on 22/02/2016.
 */
public class ViewItemReturnActivity extends AppCompatActivity {

    public static final String TAG = ViewItemReturnActivity.class.getSimpleName();

    private ItemReturnListAdapter itemReturnListAdapter;
    private CashReturnListAdapter cashReturnListAdapter;

    private List<ItemReturn> itemReturnList;
    private List<CashReturn> cashReturnList;

    private ListView lvItemReturn;
    private ListView lvCashReturn;

    private ItemReturnDao itemReturnDao;
    private CashReturnDao cashReturnDao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_to_return_list);

        itemReturnList= new ArrayList<>();
        itemReturnListAdapter = new ItemReturnListAdapter(this,itemReturnList);

        cashReturnList = new ArrayList<>();
        cashReturnListAdapter = new CashReturnListAdapter(this,cashReturnList);

        lvItemReturn = (ListView) findViewById(R.id.ITR_lvItems);
        lvItemReturn.setAdapter(itemReturnListAdapter);
        lvItemReturn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ItemReturn itemReturn = (ItemReturn) itemReturnListAdapter.getItem(position);

                if(!itemReturn.getIs_synced()){

                    Intent intent = new Intent(ViewItemReturnActivity.this,AddItemToReturnActivity.class);
                    intent.putExtra(
                            AddItemToReturnActivity.EXTRA_ITEM_RETURN_ID,
                            itemReturn.getId()
                    );
                    intent.putExtra(
                            AddItemToReturnActivity.EXTRA_ITEM_RETURN_VALUE,
                            itemReturn.getItem()
                    );

                    startActivity(intent);

                }


            }
        });

        lvCashReturn = (ListView) findViewById(R.id.ITR_lvCash);
        lvCashReturn.setAdapter(cashReturnListAdapter);
        lvCashReturn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CashReturn cashReturn = (CashReturn) cashReturnListAdapter.getItem(position);

                Log.d(TAG, " Is synced :" + cashReturn.getIs_synced());

                if(!cashReturn.getIs_synced()){

                    Log.d(TAG, " NOT SYNCED ");

                    Intent intent = new Intent(ViewItemReturnActivity.this,AddItemToReturnActivity.class);
                    intent.putExtra(
                            AddItemToReturnActivity.EXTRA_ITEM_RETURN_ID,
                            cashReturn.getId()
                    );
                    intent.putExtra(
                            AddItemToReturnActivity.EXTRA_ITEM_RETURN_VALUE,
                            cashReturn.getItem()
                    );

                    startActivity(intent);

               }

            }
        });


        initializeDataAccessObjects();
        setDataToList();

    }

    private void initializeDataAccessObjects(){

        itemReturnDao = LoyaltyStoreApplication.getInstance().getSession().getItemReturnDao();
        cashReturnDao = LoyaltyStoreApplication.getInstance().getSession().getCashReturnDao();

    }

    private void setDataToList(){

        itemReturnList.clear();
        cashReturnList.clear();

        /*List<ItemReturn> itemReturns = itemReturnDao.queryBuilder()
                .where(ItemReturnDao.Properties.Is_synced.eq(false)).list();*/

        List<ItemReturn> itemReturns = itemReturnDao.loadAll();

        for(ItemReturn itemReturn : itemReturns){

            itemReturnList.add(itemReturn);

        }

        itemReturnListAdapter.notifyDataSetChanged();

        /*List<CashReturn> cashReturns = cashReturnDao.queryBuilder()
                .where(CashReturnDao.Properties.Is_synced.eq(false)).list();*/

        List<CashReturn> cashReturns = cashReturnDao.loadAll();

        for(CashReturn cashReturn : cashReturns){

            cashReturnList.add(cashReturn);

        }

        cashReturnListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataToList();
        Log.d(TAG, "ON RESUME");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_to_return, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_Add_Items_To_Return :
                Intent intent = new Intent(this, AddItemToReturnActivity.class);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
