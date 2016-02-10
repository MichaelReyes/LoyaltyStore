package ph.com.gs3.loyaltystore;

import android.app.Application;

import ph.com.gs3.loyaltystore.models.sqlite.dao.DBHelper;
import ph.com.gs3.loyaltystore.models.sqlite.dao.DaoSession;

/**
 * Created by Bryan-PC on 09/02/2016.
 */
public class LoyaltyStoreApplication extends Application {

    private static LoyaltyStoreApplication instance = null;
    private DBHelper dbHelper = new DBHelper(this);

    public static LoyaltyStoreApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static DaoSession getNewSession() {
        return getInstance().dbHelper.getSession(true);
    }

    public static DaoSession getSession() {
        return getInstance().dbHelper.getSession(false);
    }

}
