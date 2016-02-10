package ph.com.gs3.loyaltystore.models.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ph.com.gs3.loyaltystore.models.sqlite.tables.ProductsTable;
import ph.com.gs3.loyaltystore.models.sqlite.tables.RewardsTable;
import ph.com.gs3.loyaltystore.models.sqlite.tables.SalesHasRewardsTable;
import ph.com.gs3.loyaltystore.models.sqlite.tables.SalesProductsTable;
import ph.com.gs3.loyaltystore.models.sqlite.tables.SalesTable;
import ph.com.gs3.loyaltystore.models.sqlite.tables.UnitsOfMeasureTable;


/**
 * Created by Michael Reyes on 10/30/2015.
 */
public class DatabaseHelperDefault extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHelperDefault.class.getSimpleName();

    public static final String DATABASE_NAME = "LoyaltyStore.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelperDefault instance;

    public static synchronized DatabaseHelperDefault getHelper(Context context) {
        if (instance == null)
            instance = new DatabaseHelperDefault(context);
        return instance;
    }

    private DatabaseHelperDefault(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductsTable.CREATION_QUERY);
        db.execSQL(UnitsOfMeasureTable.CREATION_QUERY);
        db.execSQL(SalesProductsTable.CREATION_QUERY);
        db.execSQL(SalesTable.CREATION_QUERY);
        db.execSQL(RewardsTable.CREATION_QUERY);
        db.execSQL(SalesHasRewardsTable.CREATION_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db , int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ProductsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UnitsOfMeasureTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SalesProductsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SalesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RewardsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SalesHasRewardsTable.TABLE_NAME);

        onCreate(db);
    }

}
