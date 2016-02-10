package ph.com.gs3.loyaltystore.models.sqlite.tables;

import ph.com.gs3.loyaltystore.models.sqlite.DatabaseHelperDefault;


/**
 * Created by Michael Reyes on 10/30/2015.
 */
public class ProductsTable {

    private DatabaseHelperDefault databaseHelperDefault;

    public static final String TABLE_NAME = "tblProducts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WEB_ID = "web_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_UNIT_OF_MEASURE_ID = "uom_id";
    public static final String COLUMN_UNIT_COST = "unit_cost";
    public static final String COLUMN_SKU = "sku";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final String CREATION_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_WEB_ID + " INTEGER, "

            + COLUMN_NAME + " TEXT, "
            + COLUMN_UNIT_OF_MEASURE_ID + " INTEGER DEFAULT 0, "
            + COLUMN_UNIT_COST + " REAL DEFAULT 0, "
            + COLUMN_SKU + " TEXT, "

            + COLUMN_CREATED_AT + " TEXT NOT NULL,"
            + COLUMN_UPDATED_AT + " TEXT NOT NULL"

            + ");";

    /*public ProductsTable(Context context) {
        databaseHelperDefault = new DatabaseHelperDefault(context);
    }

    public boolean insertData(int web_id, String name, int uom_id,
                              float unit_cost, String sku) {

        if (!productExist(web_id)) {

            SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_WEB_ID, web_id);
            contentValues.put(COLUMN_NAME, name);
            contentValues.put(COLUMN_UNIT_OF_MEASURE_ID, uom_id);
            contentValues.put(COLUMN_UNIT_COST, unit_cost);
            contentValues.put(COLUMN_SKU, sku);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateobj = new Date();
            String currDate = df.format(dateobj);

            contentValues.put(COLUMN_DATE_UPDATED, currDate);

            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                return false;
            } else {
                return true;
            }

        }else{
            return updateData(web_id,name,uom_id,unit_cost,sku);
        }

    }

    public boolean productExist(int web_id) {
        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_WEB_ID + "=" + web_id;

        Cursor cursor = db.rawQuery(query, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        return cursor;
    }

    public boolean updateData(int web_id, String name, int uom_id,
                              float unit_cost, String sku) {

        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_UNIT_OF_MEASURE_ID, uom_id);
        contentValues.put(COLUMN_UNIT_COST, unit_cost);
        contentValues.put(COLUMN_SKU, sku);
        db.update(TABLE_NAME,
                contentValues,
                COLUMN_WEB_ID + " = ?",
                new String[]{Integer.toString(web_id)});
        return true;
    }

    public Integer deleteData(int web_id) {

        SQLiteDatabase db = databaseHelperDefault.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_WEB_ID + " = ?", new String[]{Integer.toString(web_id)});

    }*/

}
