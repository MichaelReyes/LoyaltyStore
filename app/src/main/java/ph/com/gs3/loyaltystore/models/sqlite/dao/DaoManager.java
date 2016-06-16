package ph.com.gs3.loyaltystore.models.sqlite.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ervinne Sodusta on 2/8/2016.
 */
public class DaoManager {

    private static DaoMaster daoMaster;

    public static void initialize(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "loyaltydb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
    }

    public static DaoSession getDaoSession() {

        if (daoMaster == null) {
            throw new RuntimeException("DaoManager not yet initialized!");
        }

        return daoMaster.newSession();
    }

}
