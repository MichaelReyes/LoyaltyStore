package ph.com.gs3.loyaltystore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Bryan-PC on 16/02/2016.
 */
public class MaintenanceActivity extends Activity {

    public static final String TAG = MaintenanceActivity.class.getSimpleName();

    private Button bSettings;
    private Button bSync;
    private Button bSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenenace);

        initializeComponents();

    }

    private void initializeComponents(){

        bSales = (Button) findViewById(R.id.Maintenane_bSales);
        bSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartActivity(SalesActivity.class);
            }
        });

        bSettings = (Button) findViewById(R.id.Maintenance_bSettings);
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartActivity(SettingsActivity.class);
            }
        });

        bSync = (Button) findViewById(R.id.Maintenance_bSync);
        bSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartActivity(SynchronizeWithAgentActivity.class);
            }
        });

    }
    private void onStartActivity(Class activityClass){

        Intent intent = new Intent(this, activityClass);
        startActivity(intent);

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
}
